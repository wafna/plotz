package wafna.plotz.charts

import kotlin.math.cos
import kotlin.math.sin
import wafna.plotz.graphics.Point

fun Point.movePolar(radius: Double, theta: Double) = Point(x + radius * cos(theta), y + radius * sin(theta))