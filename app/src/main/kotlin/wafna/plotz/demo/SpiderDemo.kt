package wafna.plotz.demo

import javax.swing.BoxLayout
import javax.swing.JFrame
import javax.swing.JPanel
import wafna.plotz.charts.createSpiderWebPlot
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Image
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import java.awt.image.ImageObserver

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
            g.drawImage(chart, AffineTransform(), object : ImageObserver {
                override fun imageUpdate(p0: Image?, p1: Int, p2: Int, p3: Int, p4: Int, p5: Int): Boolean = true
            })
        }
    }
}

class DemoApp(s: String) : JFrame(s) {
    val chartPanel = ChartPanel().apply {
        preferredSize = Dimension(500, 500)
        contentPane = this
        layout = BoxLayout(this, 1)
    }

    init {
        defaultCloseOperation = EXIT_ON_CLOSE
        setLocationRelativeTo(null)
    }

    fun setChart(image: BufferedImage) {
        chartPanel.setChart(image)
        repaint()
    }
}

fun main() {
    DemoApp("JFreeChart Demo").apply {
        pack()
        isVisible = true
        val chart = createSpiderWebPlot(500, 500)
        setChart(chart)
    }
}
