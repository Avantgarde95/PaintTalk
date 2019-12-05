package com.github.avantgarde95.painttalk

import com.github.avantgarde95.painttalk.grammar.GrammarException
import com.github.avantgarde95.painttalk.grammar.Lexer
import com.github.avantgarde95.painttalk.grammar.Parser
import com.github.avantgarde95.painttalk.interpret.InterpretException
import com.github.avantgarde95.painttalk.interpret.Interpreter
import com.github.avantgarde95.painttalk.view.CanvasPanel
import com.github.avantgarde95.painttalk.view.ControlPanel
import com.github.avantgarde95.painttalk.view.InputPanel
import com.github.avantgarde95.painttalk.view.LogPanel
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
    private val logPanel = LogPanel()

    fun start() {
        connectEvents()
        showWindow()
    }

    private fun connectEvents() {
        Logger.logEvent.addListener { log ->
            logPanel.addLog(log)
        }

        controlPanel.openRequestEvent.addListener {
            askAndOpenInput()
        }

        controlPanel.saveRequestEvent.addListener {
            askAndSaveInput()
        }

        controlPanel.exportRequestEvent.addListener {
            askAndExportCanvas()
        }

        controlPanel.drawRequestEvent.addListener {
            parseAndDrawInput()
        }
    }

    private fun showWindow() {
        SwingUtilities.invokeLater {
            JFrame("PaintTalk").apply {
                layout = BorderLayout()
                preferredSize = Dimension(600, 500)
                defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
                isVisible = true
                isResizable = true

                add(controlPanel, BorderLayout.NORTH)

                add(
                        JSplitPane(
                                JSplitPane.VERTICAL_SPLIT,
                                JSplitPane(
                                        JSplitPane.HORIZONTAL_SPLIT,
                                        inputPanel,
                                        canvasPanel
                                ).apply {
                                    dividerLocation = 300
                                },
                                logPanel
                        ).apply {
                            dividerLocation = 280
                        }, BorderLayout.CENTER
                )

                pack()
            }

            Logger.addLog("Welcome!")
        }
    }

    private fun askAndOpenInput() {
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

    private fun askAndSaveInput() {
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

    private fun askAndExportCanvas() {
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

    private fun parseAndDrawInput() {
        Logger.addLog("Parsing the input...")
        println("\n${"=".repeat(10)} Start ${"=".repeat(10)}")

        val input = inputPanel.getInput()

        val tokens = try {
            Lexer.toTokens(input)
        } catch (exception: GrammarException) {
            Logger.addLog("Error at line ${exception.lineIndex}: ${exception.message}")
            return
        }

        if (tokens.isEmpty()) {
            return
        }

        println("\n${tokens.joinToString()}")

        val ast = try {
            Parser.toAST(tokens)
        } catch (exception: GrammarException) {
            Logger.addLog("Error at line ${exception.lineIndex}: ${exception.message}")
            return
        }

        println("\n${ast.toPrettyString()}")
        Logger.addLog("Creating a picture...")

        val picture = try {
            Interpreter.toPicture(ast)
        } catch (exception: InterpretException) {
            Logger.addLog("Error at line ${exception.lineIndex}: ${exception.message}")
            return
        }

        println("\n${picture.toPrettyString()}")

        Logger.addLog("Drawing on the canvas...")
        canvasPanel.drawImageFromPicture(picture)
        Logger.addLog("Done!")
        println("${"=".repeat(10)} Finish ${"=".repeat(9)}")
    }
}
