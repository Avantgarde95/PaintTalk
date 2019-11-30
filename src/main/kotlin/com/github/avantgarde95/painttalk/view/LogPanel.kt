package com.github.avantgarde95.painttalk.view

import java.awt.BorderLayout
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextArea

class LogPanel : JPanel() {
    private val logTextArea = JTextArea().apply {
        lineWrap = true
        isEditable = false
    }

    fun addLog(log: String) {
        logTextArea.append(
            when {
                log.isNotEmpty() && log.last() != '\n' -> "$log\n"
                else -> log
            }
        )
    }

    init {
        layout = BorderLayout()

        add(JScrollPane(logTextArea), BorderLayout.CENTER)
    }
}
