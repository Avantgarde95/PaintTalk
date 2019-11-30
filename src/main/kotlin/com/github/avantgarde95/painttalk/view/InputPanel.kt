package com.github.avantgarde95.painttalk.view

import com.github.avantgarde95.painttalk.TextLineNumber
import java.awt.BorderLayout
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextArea

class InputPanel : JPanel() {
    private val inputTextArea = JTextArea().apply {
        lineWrap = true
    }

    fun getInput() = inputTextArea.text

    fun setInput(input: String) {
        inputTextArea.text = input
    }

    init {
        layout = BorderLayout()

        add(JScrollPane(inputTextArea).apply {
            setRowHeaderView(TextLineNumber(inputTextArea))
        }, BorderLayout.CENTER)
    }
}
