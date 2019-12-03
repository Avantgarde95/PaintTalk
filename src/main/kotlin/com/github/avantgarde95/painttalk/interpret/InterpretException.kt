package com.github.avantgarde95.painttalk.interpret

class InterpretException(
    val lineIndex: Int,
    message: String
) : Exception(message)
