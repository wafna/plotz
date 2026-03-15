package wafna.plotz.graphics

import javax.imageio.ImageIO
import java.awt.Color
import java.awt.Font
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.geom.Line2D
import java.awt.geom.Path2D
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream

typealias Rectangle = Rectangle2D.Double
typealias Point = Point2D.Double
typealias Line = Line2D.Double
typealias Path = Path2D.Double

fun BufferedImage.withGraphics2D(f: Graphics2D.() -> Unit): BufferedImage = apply {
    val g2 = createGraphics()
    try {
        g2.f()
    } finally {
        g2.dispose()
    }
}

/**
 * Overrides the color for the duration of the block.
 */
fun Graphics.withColor(temp: Color, block: () -> Unit) {
    val oldColor = color
    color = temp
    block()
    color = oldColor
}

/**
 * Overrides the font for the duration of the block.
 */
fun Graphics.centeredText(text: String, center: Point, withBox: Rectangle2D.() -> Unit = {}) {
    val rect = centeredTextBox(text, center).apply { withBox() }
    drawString(text, rect.x.toInt(), rect.y.toInt() + rect.height.toInt())
}

/**
 * Calculates a rectangle that centers the text at the given coordinates given the current font.
 */
fun Graphics.centeredTextBox(text: String, center: Point): Rectangle2D {
    val metrics = getFontMetrics(font)
    val stringWidth = metrics.stringWidth(text).toDouble()
    val left = center.x - (stringWidth / 2)
    val stringHeight = metrics.ascent.toDouble()
    val top = center.y - (stringHeight / 2)
    return Rectangle2D.Double(left, top, stringWidth, stringHeight)
}

fun BufferedImage.exportToPNG(): ByteArray =
    ByteArrayOutputStream().use { stream ->
        ImageIO.write(this, "png", stream)
        stream.toByteArray()
    }
