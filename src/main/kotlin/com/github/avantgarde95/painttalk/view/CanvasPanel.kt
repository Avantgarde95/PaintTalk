package com.github.avantgarde95.painttalk.view

import java.awt.*
import java.awt.image.BufferedImage
import javax.swing.JButton
import javax.swing.JPanel

class CanvasPanel : JPanel() {
    private val drawPanel = object : JPanel() {
        override fun paintComponent(g: Graphics?) {
            super.paintComponent(g)

            if (g != null) {
                g.drawImage(scaledImage, 0, 0, this)
                g.dispose()
            }
        }
    }

    private val zoomOutButton = JButton("-").apply {
        addActionListener {
            if (zoomValue > zoomDifference) {
                zoomValue -= 0.1f
                updateScaledImage()
                updateDrawPanel()
            }
        }
    }

    private val zoomInButton = JButton("+").apply {
        addActionListener {
            zoomValue += 0.1f
            updateScaledImage()
            updateDrawPanel()
        }
    }

    private var zoomValue = 1.0f
    private val zoomDifference = 0.1f

    private var originalImage = BufferedImage(400, 300, BufferedImage.TYPE_INT_RGB).apply {
        createGraphics().apply {
            background = Color.CYAN
            stroke = BasicStroke(10.0f)
            color = Color.BLUE
            fillOval(50, 50, 50, 50)
            color = Color.RED
            drawOval(50, 50, 50, 50)
        }
    }

    private var scaledImage = originalImage

    init {
        layout = BorderLayout()

        add(drawPanel, BorderLayout.CENTER)

        add(JPanel().apply {
            layout = GridLayout(1, 2)

            add(zoomOutButton)
            add(zoomInButton)
        }, BorderLayout.SOUTH)
    }

    fun getImage() = originalImage

    private fun updateScaledImage() {
        scaledImage = BufferedImage(
            (originalImage.width * zoomValue).toInt(),
            (originalImage.height * zoomValue).toInt(),
            BufferedImage.TYPE_INT_RGB
        )

        scaledImage.createGraphics().apply {
            scale(zoomValue.toDouble(), zoomValue.toDouble())
            drawImage(originalImage, 0, 0, null)
            dispose()
        }
    }

    private fun updateDrawPanel() {
        drawPanel.apply {
            revalidate()
            repaint()
        }
    }
}
