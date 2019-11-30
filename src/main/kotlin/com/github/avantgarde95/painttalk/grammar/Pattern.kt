package com.github.avantgarde95.painttalk.grammar

sealed class Pattern

class StringPattern(
    val string: String,
    val ignoreCase: Boolean = true
) : Pattern()

class RegexPattern(
    val regex: Regex
) : Pattern()
