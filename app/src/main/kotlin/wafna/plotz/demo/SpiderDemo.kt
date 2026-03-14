package wafna.plotz.demo

import javax.swing.BoxLayout
import javax.swing.JFrame
import javax.swing.JPanel
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.sqrt
import kotlin.random.Random
import wafna.plotz.charts.createSpiderWebPlot
import java.awt.Color
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage

class ChartPanel : JPanel() {
    init {
        layout = FlowLayout(FlowLayout.LEFT)
    }

    private var chart: BufferedImage? = null
    fun setChart(image: BufferedImage) {
        chart = image
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        val g = g as Graphics2D
        if (null != chart) {
            g.drawImage(chart, AffineTransform()) { p0, p1, p2, p3, p4, p5 -> true }
        }
    }
}

class DemoApp(s: String) : JFrame(s) {
    init {
        defaultCloseOperation = EXIT_ON_CLOSE
        setLocationRelativeTo(null)
    }

    val chartPanel = ChartPanel().apply {
        preferredSize = Dimension(500, 500)
        contentPane = this
        layout = BoxLayout(this, 1)
    }

    fun setChart(image: BufferedImage) {
        chartPanel.setChart(image)
        repaint()
    }
}

private fun score(mean: Double, stdDev: Double) =
    mean + (stdDev * sqrt(-2 * ln(Random.nextDouble())) * cos(2 * PI * Random.nextDouble()))

fun main() {
    DemoApp("Plotz!").apply {
        pack()
        isVisible = true
        val agencies = listOf("NSA", "CIA", "FBI", "DIA", "DEA", "ATF")
        val data = mapOf(
            "This" to agencies.map { it to score(50.0, 5.0) },
            "That" to agencies.map { it to score(60.0, 10.0) },
            "Other" to agencies.map { it to score(70.0, 5.0) },
        )
        val chart = createSpiderWebPlot(data, 500, 500) {
            with(chartLines) {
                color = Color.GRAY
                thickness = .5
            }
            dataColors = listOf(Color.GREEN, Color.BLUE, Color.ORANGE)
        }
        setChart(chart)
    }
}
