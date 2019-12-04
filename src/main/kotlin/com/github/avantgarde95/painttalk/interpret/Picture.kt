package com.github.avantgarde95.painttalk.interpret

class Picture(
        val canvas: Canvas,
        val shapes: List<Shape>
) {
    fun toPrettyString() = "${canvas.toPrettyString()}\n" +
            shapes.joinToString("\n") { it.toPrettyString() }
}
