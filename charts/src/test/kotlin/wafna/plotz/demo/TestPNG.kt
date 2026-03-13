package wafna.plotz.demo

import org.junit.jupiter.api.Test
import wafna.plotz.graphics.exportToPNG
import wafna.plotz.charts.createSpiderWebPlot
import java.io.File
import java.nio.file.Files

class TestPNG {
    @Test
    fun test() {
        val data = mapOf("Agency" to listOf("NSA" to 10.0, "CIA" to 15.0, "FBI" to 8.0, "DIA" to 20.0))
        val chart = createSpiderWebPlot(data, 500, 500)
        val bytes = chart.exportToPNG()
        val tempFile = File.createTempFile("chart-demo", ".png").toPath()
        try {
            Files.write(tempFile, bytes)
        } finally {
            Files.delete(tempFile)
        }
    }
}