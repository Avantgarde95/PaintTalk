package com.github.avantgarde95.painttalk.interpret

data class Value(
        val numbers: List<Int>
) {
    val dimension = numbers.size

    override fun toString() = "(${numbers.joinToString()})"
}
