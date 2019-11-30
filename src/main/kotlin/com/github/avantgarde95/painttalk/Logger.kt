package com.github.avantgarde95.painttalk

object Logger {
    val logEvent = SimpleEvent<String>()

    fun addLog(log: String) {
        logEvent.fire(log)
    }
}
