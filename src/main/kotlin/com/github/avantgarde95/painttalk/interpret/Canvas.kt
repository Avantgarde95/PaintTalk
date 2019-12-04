package com.github.avantgarde95.painttalk.interpret

class Canvas {
    var size = Value(listOf(100, 100))
    var color = Value(listOf(40, 40, 40))
    var borderSize = Value(listOf(0))
    var borderColor = Value(listOf(40, 40, 40))

    fun toPrettyString() = """
        |Canvas
        |- size: $size
        |- color: $color
        |- border.size: $borderSize
        |- border.color: $borderColor
        |""".trimMargin()
}
