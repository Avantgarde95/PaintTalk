package com.github.avantgarde95.painttalk.grammar

import com.github.avantgarde95.painttalk.Logger

object Parser {
    fun toAST(tokens: List<Token>): AST {
        return Instance(tokens).parse()
    }

    private class Instance(
        private val tokens: List<Token>
    ) {
        private var tokenIndex = 0

        fun parse(): AST {
            val root = parseInput()
            Logger.addLog(root.toString())

            if (tokenIndex <= tokens.lastIndex) {
                moveToNextToken()
                throw createException(getCurrentToken())
            }

            return root
        }

        private fun parseInput(): AST.Input {
            val firstToken = getCurrentToken()
            val sentences = mutableListOf(parseSentence())

            while (true) {
                val periodToken = getCurrentToken()

                if (periodToken.type != Token.Type.Period) {
                    break
                }

                moveToNextToken()
                sentences.add(parseSentence())
            }

            val periodToken = getCurrentToken()

            if (periodToken.type != Token.Type.Period) {
                throw createException(periodToken)
            }

            moveToNextToken()
            return AST.Input(firstToken, sentences)
        }

        private fun parseSentence(): AST.Sentence {
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

            return AST.Sentence(token, basicSentences)
        }

        private fun parseBasicSentence(): AST.BasicSentence {
            val firstToken = getCurrentToken()

            if (firstToken.type == Token.Type.Name) {
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

                        return AST.BasicSentence(
                            firstToken,
                            AST.BasicSentence.Type.Order,
                            firstName = firstName,
                            order = order,
                            secondName = secondName
                        )
                    }
                    else -> {
                        val shape = parseShape()

                        return AST.BasicSentence(
                            firstToken,
                            AST.BasicSentence.Type.Shape,
                            firstName = firstName,
                            shape = shape
                        )
                    }
                }
            }

            val target = parseTarget()
            val isToken = getCurrentToken()

            if (isToken.type != Token.Type.Is) {
                throw createException(isToken)
            }

            moveToNextToken()
            val value = parseValue()

            return AST.BasicSentence(
                firstToken,
                AST.BasicSentence.Type.Value,
                target = target,
                value = value
            )
        }

        private fun parseTarget(): AST.Target {
            val firstToken = getCurrentToken()

            if (firstToken.type == Token.Type.Its) {
                moveToNextToken()
                val attribute = parseAttribute()
                return AST.Target(
                    firstToken,
                    AST.Target.Type.IndirectAttribute,
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
                    return AST.Target(
                        firstToken,
                        AST.Target.Type.IndirectAreaAttribute,
                        attribute = attribute,
                        area = area
                    )
                }
                Token.Type.Border,
                Token.Type.Inside -> {
                    val area = parseArea()
                    val ofToken2 = getCurrentToken()

                    if (ofToken2.type != Token.Type.Of) {
                        throw createException(ofToken2)
                    }

                    moveToNextToken()
                    val obj = parseObject()
                    return AST.Target(
                        firstToken,
                        AST.Target.Type.AreaAttribute,
                        attribute = attribute,
                        area = area,
                        obj = obj
                    )
                }
                else -> {
                    val obj = parseObject()
                    return AST.Target(
                        firstToken,
                        AST.Target.Type.Attribute,
                        attribute = attribute,
                        obj = obj
                    )
                }
            }
        }

        private fun parseObject(): AST.Object {
            val token = getCurrentToken()

            return when (token.type) {
                Token.Type.Name ->
                    AST.Object(token, AST.Object.Type.Name, parseName())
                else ->
                    AST.Object(token, AST.Object.Type.Canvas, parseCanvas())
            }
        }

        private fun parseValue(): AST.Value {
            val token = getCurrentToken()

            return when (token.type) {
                Token.Type.Number ->
                    AST.Value(token, AST.Value.Type.Number, parseNumber())
                Token.Type.LParen ->
                    AST.Value(token, AST.Value.Type.Tuple, parseTuple())
                else ->
                    AST.Value(token, AST.Value.Type.Color, parseColor())
            }
        }

        private fun parseCanvas(): AST.Canvas {
            val token = getCurrentToken()

            if (token.type != Token.Type.Canvas) {
                throw createException(token)
            }

            moveToNextToken()
            return AST.Canvas(token)
        }

        private fun parseName(): AST.Name {
            val token = getCurrentToken()

            if (token.type != Token.Type.Name) {
                throw createException(token)
            }

            moveToNextToken()
            return AST.Name(token)
        }

        private fun parseShape(): AST.Shape {
            val token = getCurrentToken()

            when (token.type) {
                Token.Type.Circle,
                Token.Type.Square,
                Token.Type.Ellipse,
                Token.Type.Rectangle -> {
                    moveToNextToken()
                    val type = AST.Shape.Type.values().find { it.name == token.type.name }!!
                    return AST.Shape(token, type)
                }
                else -> {
                    throw createException(token)
                }
            }
        }

        private fun parseOrder(): AST.Order {
            val firstToken = getCurrentToken()

            if (firstToken.type == Token.Type.Behind) {
                moveToNextToken()
                return AST.Order(firstToken, AST.Order.Type.Behind)
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
            return AST.Order(firstToken, AST.Order.Type.Front)
        }

        private fun parseAttribute(): AST.Attribute {
            val token = getCurrentToken()

            when (token.type) {
                Token.Type.Position,
                Token.Type.Size,
                Token.Type.Color -> {
                    moveToNextToken()
                    val type = AST.Attribute.Type.values().find { it.name == token.type.name }!!
                    return AST.Attribute(token, type)
                }
                else -> {
                    throw createException(token)
                }
            }
        }

        private fun parseArea(): AST.Area {
            val token = getCurrentToken()

            when (token.type) {
                Token.Type.Border,
                Token.Type.Inside -> {
                    moveToNextToken()
                    val type = AST.Area.Type.values().find { it.name == token.type.name }!!
                    return AST.Area(token, type)
                }
                else -> {
                    throw createException(token)
                }
            }
        }

        private fun parseTuple(): AST.Tuple {
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
            return AST.Tuple(lParenToken, numbers)
        }

        private fun parseNumber(): AST.Number {
            val token = getCurrentToken()

            if (token.type != Token.Type.Number) {
                throw createException(token)
            }

            moveToNextToken()
            return AST.Number(token)
        }

        private fun parseColor(): AST.Color {
            val token = getCurrentToken()

            when (token.type) {
                Token.Type.Red,
                Token.Type.Blue,
                Token.Type.Green,
                Token.Type.White,
                Token.Type.Black -> {
                    moveToNextToken()
                    val type = AST.Color.Type.values().find { it.name == token.type.name }!!
                    return AST.Color(token, type)
                }
                else -> {
                    throw createException(token)
                }
            }
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
}
