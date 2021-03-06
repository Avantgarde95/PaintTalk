package com.github.avantgarde95.painttalk.interpret

class Shape(
        val type: Type,
        val name: String
) {
    enum class Type(
            val sizeDimension: Int
    ) {
        Circle(sizeDimension = 1),
        Square(sizeDimension = 1),
        Ellipse(sizeDimension = 2),
        Rectangle(sizeDimension = 2)
    }

    var position = Value(listOf(0, 0))
    var size = Value((1..type.sizeDimension).map { 1 })
    var color = Value(listOf(0, 0, 0))
    var borderSize = Value(listOf(0))
    var borderColor = Value(listOf(0, 0, 0))

    fun toPrettyString() = """
        |$type "$name"
        |- size: $size
        |- color: $color
        |- border.size: $borderSize
        |- border.color: $borderColor
        |""".trimMargin()
}
