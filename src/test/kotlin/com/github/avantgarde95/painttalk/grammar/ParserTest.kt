package com.github.avantgarde95.painttalk.grammar

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ParserTest {
    @Test
    fun testNormalInput() {
        val input = "A is circle and size of its border is 10."
        val tokens = Lexer.toTokens(input)

        val expectedAST = AST(
            root = InputNode(
                tokens[0],
                listOf(
                    SentenceNode(
                        tokens[0],
                        listOf(
                            BasicSentenceNode(
                                tokens[0],
                                BasicSentenceNode.Type.Shape,
                                firstName = NameNode(tokens[0]),
                                shape = ShapeNode(tokens[2], ShapeNode.Type.Circle)
                            ),
                            BasicSentenceNode(
                                tokens[4],
                                BasicSentenceNode.Type.Value,
                                target = TargetNode(
                                    tokens[4],
                                    TargetNode.Type.IndirectAreaAttribute,
                                    attribute = AttributeNode(
                                        tokens[4],
                                        AttributeNode.Type.Size
                                    ),
                                    area = AreaNode(
                                        tokens[7],
                                        AreaNode.Type.Border
                                    )
                                ),
                                value = ValueNode(
                                    tokens[9],
                                    ValueNode.Type.Number,
                                    value = NumberNode(tokens[9])
                                )
                            )
                        )
                    )
                )
            )
        )

        val actualAST = Parser.toAST(tokens)

        TestUtil.assertMultilineEquals(expectedAST.toString(), actualAST.toString())
    }

    @Test
    fun testWrongInput() {
        val input = """
            |Size of canvas is (100, 100).
            |
            |A is circle and position of A is (30, 40).
            |Size of A is 40 and position is (20, 3).
            |Color of A is blue.
        """.trimMargin()

        val tokens = Lexer.toTokens(input)
        val exception = assertFailsWith<GrammarException> { Parser.toAST(tokens) }

        assertEquals(4, exception.lineIndex)
    }
}
