package com.github.avantgarde95.painttalk.grammar

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class LexerTest {
    @Test
    fun testEmptyInput() {
        val input = ""
        val expectedTokens = emptyList<Token>()
        val actualTokens = Lexer.toTokens(input)

        TestUtil.assertListEquals(expectedTokens, actualTokens)
    }

    @Test
    fun testNormalInput() {
        val input = "Size of canvas is (100, 100)."

        val expectedTokens = listOf(
            Token(Token.Type.Size, "Size", 1),
            Token(Token.Type.Of, "of", 1),
            Token(Token.Type.Canvas, "canvas", 1),
            Token(Token.Type.Is, "is", 1),
            Token(Token.Type.LParen, "(", 1),
            Token(Token.Type.Number, "100", 1),
            Token(Token.Type.Comma, ",", 1),
            Token(Token.Type.Number, "100", 1),
            Token(Token.Type.RParen, ")", 1),
            Token(Token.Type.Period, ".", 1)
        )

        val actualTokens = Lexer.toTokens(input)

        TestUtil.assertListEquals(expectedTokens, actualTokens)
    }

    @Test
    fun testWrongInput() {
        val input = """
            |Size of canvas is (100, 100).
            |
            |A is circle.
            |Position of A is (30$, 40).
            |Size of A is 40.
        """.trimMargin()

        val exception = assertFailsWith<GrammarException> { Lexer.toTokens(input) }

        assertEquals(4, exception.lineIndex)
    }
}
