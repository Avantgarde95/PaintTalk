package com.github.avantgarde95.painttalk.interpret

import com.github.avantgarde95.painttalk.grammar.*

object Interpreter {
    fun toPicture(ast: AST) = InterpreterInstance().interpret(ast)
}

private class InterpreterInstance {
    private val canvas = Canvas()
    private val shapes = mutableListOf<Shape>()
    private var lastUsedName: String? = null

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

        checkNameIsNew(nameNode)

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
        lastUsedName = name
    }

    private fun interpretOrderSentence(basicSentenceNode: BasicSentenceNode) {
        val firstNameNode = basicSentenceNode.firstName!!
        val orderNode = basicSentenceNode.order!!
        val secondNameNode = basicSentenceNode.secondName!!

        val firstName = firstNameNode.token.value
        val secondName = secondNameNode.token.value

        if (firstName == secondName) {
            throw InterpretException(
                    firstNameNode,
                    "We can't set the order of the same shapes!"
            )
        }

        val (firstIndex, firstShape) = getShapeByName(firstNameNode)
        val (secondIndex, secondShape) = getShapeByName(secondNameNode)

        when (orderNode.type) {
            OrderNode.Type.Front -> {
                if (firstIndex > secondIndex) {
                    shapes.removeAt(firstIndex)
                    shapes.add(secondIndex, firstShape)
                }
            }
            OrderNode.Type.Behind -> {
                if (firstIndex < secondIndex) {
                    shapes.removeAt(secondIndex)
                    shapes.add(firstIndex, secondShape)
                }
            }
        }

        lastUsedName = firstName
    }

    private fun interpretValueSentence(basicSentenceNode: BasicSentenceNode) {
        val targetNode = basicSentenceNode.target!!
        val valueNode = basicSentenceNode.value!!

        when (targetNode.type) {
            TargetNode.Type.Attribute -> {

            }
            TargetNode.Type.AreaAttribute -> {

            }
            TargetNode.Type.IndirectAttribute -> {

            }
            TargetNode.Type.IndirectAreaAttribute -> {

            }
        }
    }

    private fun setShapeAttribute(
            nameNode: NameNode,
            attributeNode: AttributeNode
    ) {
        val shape = getShapeByName(nameNode)
    }

    private fun setCanvasAttribute(
            attributeNode: AttributeNode
    ) {

    }

    private fun checkNameIsNew(nameNode: NameNode) {
        val name = nameNode.token.value

        if (shapes.find { name == it.name } != null) {
            throw InterpretException(
                    nameNode.token.lineIndex,
                    "Shape \"$name\" already exists!"
            )
        }
    }

    private fun getShapeByName(nameNode: NameNode): Pair<Int, Shape> {
        val name = nameNode.token.value
        val index = shapes.indexOfFirst { name == it.name }

        if (index < 0) {
            throw InterpretException(
                    nameNode.token.lineIndex,
                    "Shape \"$name\" doesn't exist!"
            )
        }

        return index to shapes[index]
    }

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
