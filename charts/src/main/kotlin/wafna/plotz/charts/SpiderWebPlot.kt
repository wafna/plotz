package wafna.plotz.charts

import javax.imageio.ImageIO
import kotlin.math.PI
import kotlin.math.max
import wafna.exocorps.util.buildPath
import wafna.exocorps.util.centeredText
import wafna.exocorps.util.withColor
import wafna.exocorps.util.withFont
import wafna.exocorps.util.withGraphics2D
import java.awt.BasicStroke
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream

class LineSettings {
    var color: Color? = null
    var thickness: Double = 0.5
}

class LabelSettings {
    var color: Color? = null
    var size: Double = 16.0
}

sealed class Scaling {
    data object Auto : Scaling()
    data class Fixed(val scale: Double) : Scaling()
}

class PlotSettings {
    var backgroundColor = Color.WHITE
    var defaultColor = Color.BLACK
    var chartLines = LineSettings()
    var dataLines = LineSettings()
    var labels = LabelSettings()
    var dataColors = emptyList<Color>()
    var scaling: Scaling = Scaling.Auto
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
    val settings = PlotSettings().apply { configure() }
    val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    image.withGraphics2D {
        background = Color.WHITE
        val bounds = Rectangle(0.0, 0.0, width.toDouble(), height.toDouble())
        withColor(settings.backgroundColor) {
            fill(bounds)
        }
        val center = Point(width / 2.0, height / 2.0)
        val extent = max(center.x, center.y)
        val maxRadius = extent * 0.9
        val labelRadius = extent * 0.95
        // points per pixel
        val scale = when (val scale = settings.scaling) {
            Scaling.Auto -> maxY
            is Scaling.Fixed -> scale.scale
        } / maxRadius
        // Grid lines.
        val groups = data.keys
        val angles = (2 * PI / keys.size).let { dt ->
            (0 until keys.size).map { i ->
                i * dt - PI / 2.0
            }
        }
        withColor(settings.defaultColor) {
            val gridLineColor = settings.chartLines.color ?: settings.defaultColor
            keys.zip(angles).forEach { (key, theta) ->
                stroke = BasicStroke(settings.chartLines.thickness.toFloat())
                withColor(gridLineColor) {
                    draw(Line(center, center.movePolar(maxRadius, theta)))
                    (1..4).forEach { i ->
                        val ds = i * maxRadius / 4
                        val w = (ds * 2).toInt()
                        drawOval((center.x - ds).toInt(), (center.y - ds).toInt(), w, w)
                    }
                    withFont(font.deriveFont(settings.labels.size.toFloat())) {
                        centeredText(key, center.movePolar(labelRadius, theta))
                    }
                }
            }
            // Groups
            stroke = BasicStroke(5f)
            data.forEach { groupName, data ->
                withColor(settings.dataLines.color ?: settings.defaultColor) {
                    val path = buildPath {
                        data.zip(angles) { data, theta ->
                            val r = data.second / scale
                            add(center.movePolar(r, theta))
                        }
                    }
                    draw(path)
                }
            }
        }
    }
    return image
}

fun BufferedImage.exportToPNG(): ByteArray =
    ByteArrayOutputStream().use { stream ->
        ImageIO.write(this, "png", stream)
        stream.toByteArray()
    }