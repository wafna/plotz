package wafna.exocorps.util

class PathBuilder {
    internal val path = Path2()
    internal var started = false
    fun add(point: Point2) {
        if (!started) {
            path.moveTo(point.x, point.y)
            started = true
        } else {
            path.lineTo(point.x, point.y)
        }
    }
}

fun buildPath(block: PathBuilder.() -> Unit): Path2 =
    PathBuilder().apply(block).path.apply { closePath() }

fun Collection<Point2>.buildPath(): Path2 = buildPath {
    forEach { add(it) }
}