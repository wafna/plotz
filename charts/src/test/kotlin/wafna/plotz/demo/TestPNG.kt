package wafna.plotz.demo

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.sqrt
import kotlin.random.Random
import org.junit.jupiter.api.Test
import wafna.plotz.charts.Scaling
import wafna.plotz.graphics.exportToPNG
import wafna.plotz.charts.createSpiderWebPlot
import java.awt.Color
import java.io.File
import java.nio.file.Files

private fun score(mean: Double, stdDev: Double) =
    mean + (stdDev * sqrt(-2 * ln(Random.nextDouble())) * cos(2 * PI * Random.nextDouble()))

private fun scores(n: Int, mean: Double, stdDev: Double) =
    List(n) { max(0.0, score(mean, stdDev)) }

class TestPNG {
    @Test
    fun test() {
        val groups = listOf("Alfa", "Bravo", "Charlie", "Delta", "Echo", "Foxtrot", "Golf")
        val data = groups.size.let { size ->
            listOf(
                groups.zip(scores(size, 50.0, 25.0)),
                groups.zip(scores(size, 60.0, 15.0)),
                groups.zip(scores(size, 70.0, 20.0)),
            )
        }
        val chart = createSpiderWebPlot(data, 1000, 1000) {
            scaling = Scaling.Fixed(25.0)
            dataLines.colors = listOf(Color.GREEN, Color.BLUE, Color.MAGENTA)
            dataLines.thickness = 4.0
            labels.size = 16.0
        }
        val bytes = chart.exportToPNG()
        val tempFile = File.createTempFile("chart-demo", ".png").toPath()
        try {
            Files.write(tempFile, bytes)
        } finally {
            Files.delete(tempFile)
        }
    }
}