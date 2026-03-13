package wafna.exocorps.util

import java.awt.Color
import java.awt.Font
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.geom.Path2D
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D
import java.awt.image.BufferedImage

typealias Point2 = Point2D.Double
typealias Path2 = Path2D.Double

fun <T> BufferedImage.withGraphics2D(f: Graphics2D.() -> T): T {
    val g2 = createGraphics()
    try {
        return g2.f()
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
fun Graphics.withFont(temp: Font, block: () -> Unit) {
    val oldFont = font
    font = temp
    block()
    font = oldFont
}

fun Graphics.centeredText(text: String, center: Point2) {
    val rect = centeredTextBox(text, center)
    drawString(text, rect.x.toInt(), rect.y.toInt() + rect.height.toInt())
}

/**
 * Calculates a rectangle that centers the text at the given coordinates given the current font.
 */
fun Graphics.centeredTextBox(text: String, center: Point2): Rectangle2D {
    val metrics = getFontMetrics(font)
    val stringWidth = metrics.stringWidth(text).toDouble()
    val left = center.x - (stringWidth / 2)
    val stringHeight = metrics.ascent.toDouble()
    val top = center.y - (stringHeight / 2)
    return Rectangle2D.Double(left, top, stringWidth, stringHeight)
}
