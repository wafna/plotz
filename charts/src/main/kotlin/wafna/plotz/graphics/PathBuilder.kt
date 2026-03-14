package wafna.exocorps.util

import wafna.plotz.graphics.Path
import wafna.plotz.graphics.Point

class PathBuilder {
    internal val path = Path()
    internal var started = false
    fun add(point: Point) {
        if (!started) {
            path.moveTo(point.x, point.y)
            started = true
        } else {
            path.lineTo(point.x, point.y)
        }
    }
}

fun buildPath(block: PathBuilder.() -> Unit): Path =
    PathBuilder().apply(block).path.apply { closePath() }

fun Collection<Point>.buildPath(): Path = buildPath {
    forEach { add(it) }
}