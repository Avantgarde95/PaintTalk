package com.github.avantgarde95.painttalk.interpret

data class Shape(
    val type: Type
) {
    enum class Type {
        Circle,
        Square,
        Ellipse,
        Rectangle
    }


}
