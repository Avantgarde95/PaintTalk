package com.github.avantgarde95.painttalk.grammar

object Lexer {
    fun toTokens(input: String): List<Token> {
        val tokens = mutableListOf<Token>()
        val types = Token.Type.values()
        var startIndex = 0
        var lineIndex = 1

        loop@ while (startIndex < input.length) {
            for (type in types) {
                val value = when (val pattern = type.pattern) {
                    is StringPattern -> matchStringPattern(input, startIndex, pattern)
                    is RegexPattern -> matchRegexPattern(input, startIndex, pattern)
                }

                if (value != null) {
                    if (type == Token.Type.Ignore) {
                        if (value == "\n") {
                            lineIndex++
                        }
                    } else {
                        tokens.add(Token(type, value, lineIndex))
                    }

                    startIndex += value.length
                    continue@loop
                }
            }

            throw GrammarException(lineIndex, "Unknown character!")
        }

        return tokens
    }

    private fun matchStringPattern(
        input: String,
        startIndex: Int,
        pattern: StringPattern
    ): String? {
        if (input.startsWith(pattern.string, startIndex, pattern.ignoreCase)) {
            return input.substring(startIndex, startIndex + pattern.string.length)
        }

        return null
    }

    private fun matchRegexPattern(
        input: String,
        startIndex: Int,
        pattern: RegexPattern
    ): String? {
        val match = pattern.regex.find(input.subSequence((startIndex..input.lastIndex)))

        if (match != null) {
            return match.value
        }

        return null
    }
}
