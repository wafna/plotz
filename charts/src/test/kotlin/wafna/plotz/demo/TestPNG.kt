package wafna.plotz.demo

import org.junit.jupiter.api.Test
import wafna.plotz.charts.createSpiderWebPlot
import wafna.plotz.charts.exportToPNG
import java.io.File
import java.nio.file.Files

class TestPNG {
    @Test
    fun test() {
        val dataset = DefaultCategoryDataset().apply {
            addValue(35.0, "Agency", "CIA")
            addValue(45.0, "Agency", "FBI")
            addValue(15.0, "Agency", "DIA")
            addValue(75.0, "Agency", "NSA")
            addValue(25.0, "Agency", "NRO")
            addValue(45.0, "Agency", "NGA")
        }
        val (width, height) = 300 to 300
        val chart = createSpiderWebPlot(dataset, width, height)
        val bytes = exportToPNG(chart, width, height)
        val tempFile = File.createTempFile("chart-demo", ".png").toPath()
        try {
            Files.write(tempFile, bytes)
        } finally {
            Files.delete(tempFile)
        }
    }
}