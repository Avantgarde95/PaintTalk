package com.github.avantgarde95.painttalk.grammar

object Parser {
    fun toAST(tokens: List<Token>) = AST(
            root = ParserInstance(tokens).parse()
    )
}

private object FirstTokenType {
    val color = listOf(
            Token.Type.Red,
            Token.Type.Blue,
            Token.Type.Green,
            Token.Type.White,
            Token.Type.Black
    )

    val number = listOf(Token.Type.Number)
    val tuple = listOf(Token.Type.LParen)

    val area = listOf(
            Token.Type.Border//,
            //Token.Type.Inside
    )

    val attribute = listOf(
            Token.Type.Position,
            Token.Type.Size,
            Token.Type.Color
    )

    val order = listOf(
            Token.Type.In,
            Token.Type.Behind
    )

    val shape = listOf(
            Token.Type.Circle,
            Token.Type.Square,
            Token.Type.Ellipse,
            Token.Type.Rectangle
    )

    val name = listOf(Token.Type.Name)
    val canvas = listOf(Token.Type.Canvas)
    val value = number + tuple + color
    val obj = name + canvas
    val target = listOf(Token.Type.Its) + attribute
    val basicSentence = name + target
    val sentence = basicSentence
}

private class ParserInstance(
        inputTokens: List<Token>
) {
    val tokens = inputTokens + listOf(
            Token(Token.Type.EOF, "", inputTokens.last().lineIndex + 1)
    )

    private var tokenIndex = 0

    fun parse(): InputNode {
        val root = parseInput()
        val eofToken = getCurrentToken()

        if (eofToken.type != Token.Type.EOF) {
            throw createException(getCurrentToken())
        }

        return root
    }

    private fun parseInput(): InputNode {
        val firstToken = getCurrentToken()
        val sentences = mutableListOf(parseSentence())

        val firstPeriodToken = getCurrentToken()

        if (firstPeriodToken.type != Token.Type.Period) {
            throw createException(firstPeriodToken)
        }

        moveToNextToken()

        while (true) {
            val sentenceToken = getCurrentToken()

            if (sentenceToken.type !in FirstTokenType.sentence) {
                break
            }

            sentences.add(parseSentence())
            val periodToken = getCurrentToken()

            if (periodToken.type != Token.Type.Period) {
                throw createException(periodToken)
            }

            moveToNextToken()
        }

        return InputNode(firstToken, sentences)
    }

    private fun parseSentence(): SentenceNode {
        val token = getCurrentToken()
        val basicSentences = mutableListOf(parseBasicSentence())

        while (true) {
            val andToken = getCurrentToken()

            if (andToken.type != Token.Type.And) {
                break
            }

            moveToNextToken()
            basicSentences.add(parseBasicSentence())
        }

        return SentenceNode(token, basicSentences)
    }

    private fun parseBasicSentence(): BasicSentenceNode {
        val firstToken = getCurrentToken()

        when (firstToken.type) {
            in FirstTokenType.name -> {
                val firstName = parseName()
                val isToken = getCurrentToken()

                if (isToken.type != Token.Type.Is) {
                    throw createException(isToken)
                }

                moveToNextToken()
                val orderToken = getCurrentToken()

                when (orderToken.type) {
                    Token.Type.In,
                    Token.Type.Behind -> {
                        val order = parseOrder()
                        val secondName = parseName()

                        return BasicSentenceNode(
                                firstToken,
                                BasicSentenceNode.Type.Order,
                                firstName = firstName,
                                order = order,
                                secondName = secondName
                        )
                    }
                    else -> {
                        val shape = parseShape()

                        return BasicSentenceNode(
                                firstToken,
                                BasicSentenceNode.Type.Shape,
                                firstName = firstName,
                                shape = shape
                        )
                    }
                }
            }
            in FirstTokenType.target -> {
                val target = parseTarget()
                val isToken = getCurrentToken()

                if (isToken.type != Token.Type.Is) {
                    throw createException(isToken)
                }

                moveToNextToken()
                val value = parseValue()

                return BasicSentenceNode(
                        firstToken,
                        BasicSentenceNode.Type.Value,
                        target = target,
                        value = value
                )
            }
            else -> {
                throw createException(firstToken)
            }
        }
    }

    private fun parseTarget(): TargetNode {
        val firstToken = getCurrentToken()

        if (firstToken.type == Token.Type.Its) {
            moveToNextToken()
            val attribute = parseAttribute()
            return TargetNode(
                    firstToken,
                    TargetNode.Type.IndirectAttribute,
                    attribute = attribute
            )
        }

        val attribute = parseAttribute()
        val ofToken1 = getCurrentToken()

        if (ofToken1.type != Token.Type.Of) {
            throw createException(ofToken1)
        }

        moveToNextToken()
        val afterOfToken = getCurrentToken()

        when (afterOfToken.type) {
            Token.Type.Its -> {
                moveToNextToken()
                val area = parseArea()
                return TargetNode(
                        firstToken,
                        TargetNode.Type.IndirectAreaAttribute,
                        attribute = attribute,
                        area = area
                )
            }
            in FirstTokenType.area -> {
                val area = parseArea()
                val ofToken2 = getCurrentToken()

                if (ofToken2.type != Token.Type.Of) {
                    throw createException(ofToken2)
                }

                moveToNextToken()
                val obj = parseObject()
                return TargetNode(
                        firstToken,
                        TargetNode.Type.AreaAttribute,
                        attribute = attribute,
                        area = area,
                        obj = obj
                )
            }
            in FirstTokenType.obj -> {
                val obj = parseObject()
                return TargetNode(
                        firstToken,
                        TargetNode.Type.Attribute,
                        attribute = attribute,
                        obj = obj
                )
            }
            else -> {
                throw createException(afterOfToken)
            }
        }
    }

    private fun parseObject(): ObjectNode {
        val token = getCurrentToken()

        return when (token.type) {
            in FirstTokenType.name ->
                ObjectNode(token, ObjectNode.Type.Name, parseName())
            in FirstTokenType.canvas ->
                ObjectNode(token, ObjectNode.Type.Canvas, parseCanvas())
            else ->
                throw createException(token)
        }
    }

    private fun parseValue(): ValueNode {
        val token = getCurrentToken()

        return when (token.type) {
            in FirstTokenType.number ->
                ValueNode(token, ValueNode.Type.Number, parseNumber())
            in FirstTokenType.tuple ->
                ValueNode(token, ValueNode.Type.Tuple, parseTuple())
            in FirstTokenType.color ->
                ValueNode(token, ValueNode.Type.Color, parseColor())
            else ->
                throw createException(token)
        }
    }

    private fun parseCanvas(): CanvasNode {
        val token = getCurrentToken()

        if (token.type != Token.Type.Canvas) {
            throw createException(token)
        }

        moveToNextToken()
        return CanvasNode(token)
    }

    private fun parseName(): NameNode {
        val token = getCurrentToken()

        if (token.type != Token.Type.Name) {
            throw createException(token)
        }

        moveToNextToken()
        return NameNode(token)
    }

    private fun parseShape(): ShapeNode {
        val token = getCurrentToken()

        if (token.type !in FirstTokenType.shape) {
            throw createException(token)
        }

        moveToNextToken()
        return ShapeNode(token)
    }

    private fun parseOrder(): OrderNode {
        val firstToken = getCurrentToken()

        if (firstToken.type == Token.Type.Behind) {
            moveToNextToken()
            return OrderNode(firstToken, OrderNode.Type.Behind)
        } else if (firstToken.type != Token.Type.In) {
            throw createException(firstToken)
        }

        moveToNextToken()
        val frontToken = getCurrentToken()

        if (frontToken.type != Token.Type.Front) {
            throw createException(frontToken)
        }

        moveToNextToken()
        val ofToken = getCurrentToken()

        if (ofToken.type != Token.Type.Of) {
            throw createException(ofToken)
        }

        moveToNextToken()
        return OrderNode(firstToken, OrderNode.Type.Front)
    }

    private fun parseAttribute(): AttributeNode {
        val token = getCurrentToken()

        if (token.type !in FirstTokenType.attribute) {
            throw createException(token)
        }

        moveToNextToken()
        val type = AttributeNode.Type.values().find { it.name == token.type.name }!!
        return AttributeNode(token, type)
    }

    private fun parseArea(): AreaNode {
        val token = getCurrentToken()

        if (token.type !in FirstTokenType.area) {
            throw createException(token)
        }

        moveToNextToken()
        val type = AreaNode.Type.values().find { it.name == token.type.name }!!
        return AreaNode(token, type)
    }

    private fun parseTuple(): TupleNode {
        val lParenToken = getCurrentToken()

        if (lParenToken.type != Token.Type.LParen) {
            throw createException(lParenToken)
        }

        moveToNextToken()
        val numbers = mutableListOf(parseNumber())

        while (true) {
            val commaToken = getCurrentToken()

            if (commaToken.type != Token.Type.Comma) {
                break
            }

            moveToNextToken()
            numbers.add(parseNumber())
        }

        val rParenToken = getCurrentToken()

        if (rParenToken.type != Token.Type.RParen) {
            throw createException(rParenToken)
        }

        moveToNextToken()
        return TupleNode(lParenToken, numbers)
    }

    private fun parseNumber(): NumberNode {
        val token = getCurrentToken()

        if (token.type != Token.Type.Number) {
            throw createException(token)
        }

        moveToNextToken()
        return NumberNode(token)
    }

    private fun parseColor(): ColorNode {
        val token = getCurrentToken()

        if (token.type !in FirstTokenType.color) {
            throw createException(token)
        }

        moveToNextToken()
        return ColorNode(token)
    }

    private fun getCurrentToken(): Token {
        if (tokenIndex > tokens.lastIndex) {
            throw createException(tokens.last())
        }

        return tokens[tokenIndex]
    }

    private fun moveToNextToken() {
        tokenIndex++
    }

    private fun createException(token: Token) =
            GrammarException(
                    token.lineIndex,
                    "Wrong syntax at \"${token.value}\"!"
            )
}
