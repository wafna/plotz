package wafna.plotz.demo

import javax.imageio.ImageIO
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin
import java.awt.Color
import java.awt.Graphics2D
import java.awt.geom.Line2D
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream

class PlotSettings {
    var backgroundColor = Color.WHITE
    var defaultColor = Color.BLACK
}

private typealias Rectangle = Rectangle2D.Double
private typealias Point = Point2D.Double
private typealias Line = Line2D.Double

fun <T> BufferedImage.withGraphics2D(f: Graphics2D.() -> T): T {
    val g2 = createGraphics()
    try {
        return g2.f()
    } finally {
        g2.dispose()
    }
}

fun createSpiderWebPlot(width: Int, height: Int): BufferedImage =
    BufferedImage(width, height, BufferedImage.TYPE_INT_RGB).apply {
        withGraphics2D {
            draw(Rectangle(0.0, 0.0, width.toDouble(), height.toDouble()))
            val center = Point(width / 2.0, height / 2.0)
            val extent = max(center.x, center.y)
            val dt = PI / 3
            (0 until 6).forEach { i ->
                val theta = i * dt
                draw(Line(center, Point(extent * cos(theta), extent * sin(theta))))
            }
        }
    }

fun BufferedImage.exportToPNG(width: Int, height: Int): ByteArray =
    ByteArrayOutputStream().use { stream ->
        ImageIO.write(this, "png", stream)
        stream.toByteArray()
    }