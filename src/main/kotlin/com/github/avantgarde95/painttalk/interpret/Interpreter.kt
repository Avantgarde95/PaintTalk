package com.github.avantgarde95.painttalk.interpret

import com.github.avantgarde95.painttalk.grammar.*

object Interpreter {
    fun toPicture(ast: AST) = InterpreterInstance().interpret(ast)
}

private class InterpreterInstance {
    private val canvas = Canvas()
    private val shapes = mutableListOf<Shape>()

    fun interpret(ast: AST): Picture {
        val inputNode = ast.root
        val sentenceNodes = inputNode.sentences

        sentenceNodes.forEach { sentenceNode ->
            sentenceNode.basicSentences.forEach { basicSentenceNode ->
                interpretBasicSentence(basicSentenceNode)
            }
        }

        return Picture(canvas, shapes)
    }

    private fun interpretBasicSentence(basicSentenceNode: BasicSentenceNode) {
        when (basicSentenceNode.type) {
            BasicSentenceNode.Type.Shape ->
                interpretShapeSentence(basicSentenceNode)
            BasicSentenceNode.Type.Order ->
                interpretOrderSentence(basicSentenceNode)
            BasicSentenceNode.Type.Value ->
                interpretValueSentence(basicSentenceNode)
        }
    }

    private fun interpretShapeSentence(basicSentenceNode: BasicSentenceNode) {
        val nameNode = basicSentenceNode.firstName!!
        val shapeNode = basicSentenceNode.shape!!
        val name = nameNode.token.value

        if (shapeWithNameExists(nameNode)) {
            throw InterpretException(nameNode, "Shape \"$name\" already exists!")
        }

        val type = when (shapeNode.token.type) {
            Token.Type.Circle -> Shape.Type.Circle
            Token.Type.Square -> Shape.Type.Square
            Token.Type.Ellipse -> Shape.Type.Ellipse
            Token.Type.Rectangle -> Shape.Type.Rectangle
            else -> {
                throw InterpretException(shapeNode, "Wrong shape!")
            }
        }

        shapes.add(Shape(type, name))
    }

    private fun interpretOrderSentence(basicSentenceNode: BasicSentenceNode) {
        val firstNameNode = basicSentenceNode.firstName!!
        val orderNode = basicSentenceNode.order!!
        val secondNameNode = basicSentenceNode.secondName!!
        val firstName = firstNameNode.token.value
        val secondName = secondNameNode.token.value
    }

    private fun interpretValueSentence(basicSentenceNode: BasicSentenceNode) {
        val targetNode = basicSentenceNode.target!!
        val valueNode = basicSentenceNode.value!!
    }

    private fun setShapeAttribute(
            nameNode: NameNode,
            shapeNode: ShapeNode
    ) {
        val shape = getShapeByName(nameNode)


    }

    private fun shapeWithNameExists(nameNode: NameNode) =
            shapes.find { nameNode.token.value == it.name } != null

    private fun getShapeByName(nameNode: NameNode) =
            shapes.find { nameNode.token.value == it.name }!!

    private fun tupleNodeToValue(tupleNode: TupleNode) =
            Value(tupleNode.numbers.map { it.token.value.toInt() })

    private fun numberNodeToValue(numberNode: NumberNode) =
            Value(listOf(numberNode.token.value.toInt()))

    private fun colorNodeToValue(colorNode: ColorNode) =
            when (colorNode.token.type) {
                Token.Type.Red -> Value(listOf(255, 0, 0))
                Token.Type.Blue -> Value(listOf(0, 0, 255))
                Token.Type.Green -> Value(listOf(0, 255, 0))
                Token.Type.White -> Value(listOf(255, 255, 255))
                Token.Type.Black -> Value(listOf(0, 0, 0))
                else -> {
                    throw InterpretException(colorNode, "Wrong color!")
                }
            }
}
