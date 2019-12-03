package com.github.avantgarde95.painttalk.interpret

data class Value(
    val numbers: List<Int>
) {
    override fun toString() = "(${numbers.joinToString()})"
}
