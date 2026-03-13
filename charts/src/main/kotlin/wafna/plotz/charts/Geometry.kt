package wafna.plotz.charts

import kotlin.math.cos
import kotlin.math.sin
import java.awt.geom.Line2D
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D

typealias Rectangle = Rectangle2D.Double
typealias Point = Point2D.Double
typealias Line = Line2D.Double

fun Point.movePolar(radius: Double, theta: Double) = Point(x + radius * cos(theta), y + radius * sin(theta))