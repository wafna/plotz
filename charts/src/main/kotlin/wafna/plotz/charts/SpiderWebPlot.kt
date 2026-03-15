package wafna.plotz.charts

import kotlin.math.PI
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.roundToInt
import wafna.exocorps.util.buildPath
import wafna.plotz.graphics.Line
import wafna.plotz.graphics.Point
import wafna.plotz.graphics.Rectangle
import wafna.plotz.graphics.centeredText
import wafna.plotz.graphics.withColor
import wafna.plotz.graphics.withGraphics2D
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Font
import java.awt.image.BufferedImage

class LineSettings {
    var color: Color? = null
    var thickness: Double? = 0.5
}

class LabelSettings {
    var color: Color? = null
    var size: Double? = null
}

sealed class Scaling {
    // Makes the outermost radial a nice round number up from the max value.
    data class Auto(val hashes: Int = 4) : Scaling()
    data class Fixed(val hash: Double) : Scaling()
}

class PlotSettings {
    var backgroundColor = Color.WHITE
    var defaultColor = Color.BLACK
    var chartLines = LineSettings()
    var dataLines = LineSettings()
    var labels = LabelSettings()
    var dataColors = emptyList<Color>()
    var scaling: Scaling = Scaling.Auto()
}

fun createSpiderWebPlot(
    data: Map<String, List<Pair<String, Double>>>,
    width: Int,
    height: Int,
    configure: PlotSettings.() -> Unit = {}
): BufferedImage {
    data.forEach {
        require(it.value.map { it.second }.all { 0 <= it }) { "ALl data values must be positive." }
    }
    val dataGroupCount = data.size
    val keys = data.iterator().next().value.map { it.first }
    if (0 < dataGroupCount) {
        require(data.all { keys == it.value.map { it.first } }) {
            "All data groups must have the same keys."
        }
    }
    val maxY = data.entries.fold(0.0) { max, group ->
        group.value.fold(max) { max, y -> max(max, y.second) }
    }

//    val radialHashes =
    val settings = PlotSettings().apply { configure() }
    val maxRadial = when (val scaling = settings.scaling) {
        is Scaling.Auto -> {
            val m = ceil(maxY / 10.0) * 10.0
            val u = m / scaling.hashes
            ceil(maxY / u) * u
        }

        is Scaling.Fixed -> {
            ceil(maxY / scaling.hash) * scaling.hash
        }
    }

    val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    image.withGraphics2D {
        background = Color.WHITE
        val bounds = Rectangle(0.0, 0.0, width.toDouble(), height.toDouble())
        withColor(settings.backgroundColor) {
            fill(bounds)
        }
        val center = Point(width / 2.0, height / 2.0)
        // The maximum square real estate.
        val extent = max(center.x, center.y)
        // We need to leave room for the labels.
        val labelFont =
            if (null == settings.labels.size) font
            else font.deriveFont(Font.BOLD, settings.labels.size!!.toFloat())
        val margin = getFontMetrics(labelFont).let { metrics ->
            keys.fold(metrics.ascent.toDouble()) { max, key ->
                max(max, metrics.stringWidth(key).toDouble())
            }
        } * 1.05 // plus some padding

        val maxRadius = extent - margin
        require(0.0 < maxRadius) { "Not enough room!" }
        val labelRadius = extent - margin / 2.0
        // points per pixel
        val scale = maxRadial / maxRadius
        // Grid lines.
        val gridLineColor = settings.chartLines.color ?: settings.defaultColor
        color = gridLineColor
        stroke = BasicStroke(settings.chartLines.thickness?.toFloat() ?: 1f)
        val angles = (2 * PI / keys.size).let { dt ->
            (0 until keys.size).map { it * dt - PI / 2.0 }
        }
        // concentric hashes
        (1..4).forEach { i ->
            val ds = i * maxRadius / 4
            val w = (ds * 2).toInt()
            drawOval((center.x - ds).toInt(), (center.y - ds).toInt(), w, w)
            // magnitudes
            val magOffset = PI / keys.size
            angles.forEach { angle ->
                centeredText(
                    (ds * scale).roundToInt().toString(),
                    center.movePolar(ds, angle + magOffset)
                ) {
                    // erase the grid lines around the numbers for legibility.
                    withColor(settings.backgroundColor) { fill(this) }
                }
            }
        }
        font = labelFont
        keys.zip(angles).forEach { (key, theta) ->
            // radial hashes
            draw(Line(center, center.movePolar(maxRadius, theta)))
            // labels
            color = settings.labels.color ?: settings.defaultColor
            centeredText(key, center.movePolar(labelRadius, theta))
        }
        // Data
        if (null != settings.dataLines.thickness)
            stroke = BasicStroke(5f)
        // Fill the list with the default color if it's too short.
        val colors = settings.dataColors.let {
            val defaultColor = settings.dataLines.color ?: settings.defaultColor
            val need = data.size - it.size
            if (0 < need) {
                buildList {
                    addAll(settings.dataColors)
                    repeat(need) { add(defaultColor) }
                }
            } else it
        }
        data.toList().zip(colors).forEach { (data, color) ->
            withColor(color) {
                val path = buildPath {
                    data.second.zip(angles) { data, theta ->
                        val r = data.second / scale
                        add(center.movePolar(r, theta))
                    }
                }
                draw(path)
            }
        }
    }

    return image
}
