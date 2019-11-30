package com.github.avantgarde95.painttalk.grammar

class GrammarException(
    val lineIndex: Int,
    message: String
) : Exception(message)
