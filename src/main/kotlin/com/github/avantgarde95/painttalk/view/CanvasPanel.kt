package com.github.avantgarde95.painttalk.view

import com.github.avantgarde95.painttalk.interpret.Canvas
import com.github.avantgarde95.painttalk.interpret.Picture
import com.github.avantgarde95.painttalk.interpret.Shape
import com.github.avantgarde95.painttalk.interpret.Value
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

    private var originalImage =
            BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB).apply {
                createGraphics().apply {
                    color = Color.WHITE
                    fillRect(0, 0, 100, 100)
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

    fun drawImageFromPicture(picture: Picture) {
        drawCanvas(picture.canvas)
        drawCanvasBorder(picture.canvas)

        picture.shapes.forEach {
            drawShape(it)
            drawShapeBorder(it)
        }

        updateScaledImage()
        updateDrawPanel()
    }

    private fun drawCanvas(canvas: Canvas) {
        originalImage = BufferedImage(
                canvas.size.numbers[0],
                canvas.size.numbers[1],
                BufferedImage.TYPE_INT_RGB
        )

        val originalGraphics = originalImage.createGraphics()

        originalGraphics.color = valueToColor(canvas.color)

        originalGraphics.fillRect(
                0,
                0,
                canvas.size.numbers[0],
                canvas.size.numbers[1]
        )
    }

    private fun drawCanvasBorder(canvas: Canvas) {
        val borderSize = canvas.borderSize.numbers[0]
        val originalGraphics = originalImage.createGraphics()

        if (borderSize == 0) {
            return
        }

        originalGraphics.stroke = BasicStroke(borderSize.toFloat())
        originalGraphics.color = valueToColor(canvas.borderColor)

        originalGraphics.drawRect(
                0,
                0,
                canvas.size.numbers[0],
                canvas.size.numbers[1]
        )
    }

    private fun drawShape(shape: Shape) {
        val originalGraphics = originalImage.createGraphics()

        originalGraphics.color = valueToColor(shape.color)

        when (shape.type) {
            Shape.Type.Circle -> {
                originalGraphics.fillOval(
                        shape.position.numbers[0],
                        shape.position.numbers[1],
                        shape.size.numbers[0],
                        shape.size.numbers[0]
                )
            }
            Shape.Type.Square -> {
                originalGraphics.fillRect(
                        shape.position.numbers[0],
                        shape.position.numbers[1],
                        shape.size.numbers[0],
                        shape.size.numbers[0]
                )
            }
            Shape.Type.Ellipse -> {
                originalGraphics.fillOval(
                        shape.position.numbers[0],
                        shape.position.numbers[1],
                        shape.size.numbers[0],
                        shape.size.numbers[1]
                )
            }
            Shape.Type.Rectangle -> {
                originalGraphics.fillRect(
                        shape.position.numbers[0],
                        shape.position.numbers[1],
                        shape.size.numbers[0],
                        shape.size.numbers[1]
                )
            }
        }
    }

    private fun drawShapeBorder(shape: Shape) {
        val originalGraphics = originalImage.createGraphics()
        val borderSize = shape.borderSize.numbers[0]

        if (borderSize == 0) {
            return
        }

        originalGraphics.stroke = BasicStroke(borderSize.toFloat())
        originalGraphics.color = valueToColor(shape.borderColor)

        when (shape.type) {
            Shape.Type.Circle -> {
                originalGraphics.drawOval(
                        shape.position.numbers[0],
                        shape.position.numbers[1],
                        shape.size.numbers[0],
                        shape.size.numbers[0]
                )
            }
            Shape.Type.Square -> {
                originalGraphics.drawRect(
                        shape.position.numbers[0],
                        shape.position.numbers[1],
                        shape.size.numbers[0],
                        shape.size.numbers[0]
                )
            }
            Shape.Type.Ellipse -> {
                originalGraphics.drawOval(
                        shape.position.numbers[0],
                        shape.position.numbers[1],
                        shape.size.numbers[0],
                        shape.size.numbers[1]
                )
            }
            Shape.Type.Rectangle -> {
                originalGraphics.drawRect(
                        shape.position.numbers[0],
                        shape.position.numbers[1],
                        shape.size.numbers[0],
                        shape.size.numbers[1]
                )
            }
        }
    }

    private fun valueToColor(value: Value) = Color(
            value.numbers[0],
            value.numbers[1],
            value.numbers[2]
    )

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
