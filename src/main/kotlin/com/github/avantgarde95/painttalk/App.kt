package com.github.avantgarde95.painttalk

import com.github.avantgarde95.painttalk.view.CanvasPanel
import com.github.avantgarde95.painttalk.view.ControlPanel
import com.github.avantgarde95.painttalk.view.InputPanel
import java.awt.BorderLayout
import java.awt.Dimension
import java.io.File
import javax.imageio.ImageIO
import javax.swing.*
import javax.swing.filechooser.FileNameExtensionFilter

class App {
    private val controlPanel = ControlPanel()
    private val inputPanel = InputPanel()
    private val canvasPanel = CanvasPanel()

    fun start() {
        connectEvents()
        showWindow()
    }

    private fun connectEvents() {
        controlPanel.openRequestEvent.addListener {
            val fileChooser = JFileChooser().apply {
                dialogTitle = "Open your input"
                fileFilter = FileNameExtensionFilter("Text file", "txt")
            }

            val returnValue = fileChooser.showOpenDialog(null)

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                val path = fileChooser.selectedFile.path
                val input = File(path).readText()

                inputPanel.setInput(input)
            }
        }

        controlPanel.saveRequestEvent.addListener {
            val fileChooser = JFileChooser().apply {
                dialogTitle = "Save your input"
                fileFilter = FileNameExtensionFilter("Text file", "txt")
            }

            val returnValue = fileChooser.showSaveDialog(null)

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                val path = fileChooser.selectedFile.path

                File(path).writeText(inputPanel.getInput())
            }
        }

        controlPanel.exportRequestEvent.addListener {
            val fileChooser = JFileChooser().apply {
                dialogTitle = "Export to image"
                fileFilter = FileNameExtensionFilter("PNG file", "png")
            }

            val returnValue = fileChooser.showSaveDialog(null)

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                val path = fileChooser.selectedFile.path

                ImageIO.write(canvasPanel.getImage(), "png", File(path))
            }
        }

        controlPanel.drawRequestEvent.addListener {

        }
    }

    private fun showWindow() {
        SwingUtilities.invokeLater {
            JFrame("PaintTalk").apply {
                layout = BorderLayout()
                preferredSize = Dimension(600, 300)
                defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
                isVisible = true
                isResizable = true

                add(controlPanel, BorderLayout.NORTH)

                add(
                    JSplitPane(
                        JSplitPane.HORIZONTAL_SPLIT,
                        inputPanel,
                        canvasPanel
                    ).apply {
                        dividerLocation = 300
                    }, BorderLayout.CENTER
                )

                pack()
            }
        }
    }
}
