package com.github.avantgarde95.painttalk.interpret

import com.github.avantgarde95.painttalk.grammar.*

object Interpreter {
    fun toPicture(ast: AST) = InterpreterInstance().interpret(ast)
}

private class InterpreterInstance {
    private val canvas = Canvas()
    private val shapes = mutableListOf<Shape>()
    private var lastUsedObject: Any? = null

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

        val shape = Shape(type, name)
        shapes.add(shape)
        lastUsedObject = shape
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

        lastUsedObject = firstShape
    }

    private fun interpretValueSentence(basicSentenceNode: BasicSentenceNode) {
        val targetNode = basicSentenceNode.target!!
        val valueNode = basicSentenceNode.value!!
        val attributeNode = targetNode.attribute

        when (targetNode.type) {
            TargetNode.Type.Attribute -> {
                val objectNode = targetNode.obj!!

                when (objectNode.type) {
                    ObjectNode.Type.Canvas -> {
                        setCanvasAttribute(attributeNode, valueNode)
                    }
                    ObjectNode.Type.Name -> {
                        val nameNode = objectNode.obj as NameNode

                        setShapeAttribute(nameNode, attributeNode, valueNode)
                    }
                }
            }
            TargetNode.Type.AreaAttribute -> {
                val areaNode = targetNode.area!!
                val objectNode = targetNode.obj!!

                if (areaNode.type != AreaNode.Type.Border) {
                    throw InterpretException(areaNode, "Not \"border\"!")
                }

                when (objectNode.type) {
                    ObjectNode.Type.Canvas -> {
                        setCanvasBorderAttribute(attributeNode, valueNode)
                    }
                    ObjectNode.Type.Name -> {
                        val nameNode = objectNode.obj as NameNode

                        setShapeBorderAttribute(nameNode, attributeNode, valueNode)
                    }
                }
            }
            TargetNode.Type.IndirectAttribute -> {
                if (lastUsedObject == null) {
                    throw InterpretException(
                            targetNode,
                            "Can't use \"its\" since no object is defined!"
                    )
                }

                when (lastUsedObject!!) {
                    is Canvas -> {
                        setCanvasAttribute(attributeNode, valueNode)
                    }
                    is Shape -> {
                        val shape = lastUsedObject as Shape
                        setShapeAttribute(shape, attributeNode, valueNode)
                    }
                }
            }
            TargetNode.Type.IndirectAreaAttribute -> {
                val areaNode = targetNode.area!!

                if (areaNode.type != AreaNode.Type.Border) {
                    throw InterpretException(areaNode, "Not \"border\"!")
                }

                if (lastUsedObject == null) {
                    throw InterpretException(
                            targetNode,
                            "Can't use \"its\" since no object is defined!"
                    )
                }
            }
        }
    }

    private fun setCanvasAttribute(
            attributeNode: AttributeNode,
            valueNode: ValueNode
    ) {
        val value = valueNodeToValue(valueNode)
        val lineIndex = valueNode.token.lineIndex

        when (attributeNode.type) {
            AttributeNode.Type.Size -> {
                if (value.dimension != 2) {
                    throw InterpretException(
                            lineIndex,
                            "Dimension of canvas size should be 2!"
                    )
                }

                canvas.size = value
            }
            AttributeNode.Type.Color -> {
                if (value.dimension != 3) {
                    throw InterpretException(
                            lineIndex,
                            "Dimension of color should be 3!"
                    )
                }

                canvas.color = value
            }
            AttributeNode.Type.Position -> {
                throw InterpretException(
                        lineIndex,
                        "Invalid attribute for canvas!"
                )
            }
        }

        lastUsedObject = canvas
    }

    private fun setCanvasBorderAttribute(
            attributeNode: AttributeNode,
            valueNode: ValueNode
    ) {

        val value = valueNodeToValue(valueNode)
        val lineIndex = valueNode.token.lineIndex

        when (attributeNode.type) {
            AttributeNode.Type.Size -> {
                if (value.dimension != 1) {
                    throw InterpretException(
                            lineIndex,
                            "Dimension of border size should be 1!"
                    )
                }

                canvas.borderSize = value
            }
            AttributeNode.Type.Color -> {
                if (value.dimension != 3) {
                    throw InterpretException(
                            lineIndex,
                            "Dimension of color should be 3!"
                    )
                }

                canvas.borderColor = value
            }
            AttributeNode.Type.Position -> {
                throw InterpretException(
                        lineIndex,
                        "Invalid attribute for border!"
                )
            }
        }

        lastUsedObject = canvas
    }

    private fun setShapeAttribute(
            nameNode: NameNode,
            attributeNode: AttributeNode,
            valueNode: ValueNode
    ) {
        val shape = getShapeByName(nameNode).second
        setShapeAttribute(shape, attributeNode, valueNode)
    }

    private fun setShapeAttribute(
            shape: Shape,
            attributeNode: AttributeNode,
            valueNode: ValueNode
    ) {
        val value = valueNodeToValue(valueNode)
        val lineIndex = valueNode.token.lineIndex

        when (attributeNode.type) {
            AttributeNode.Type.Size -> {
                if (value.dimension != shape.type.sizeDimension) {
                    throw InterpretException(
                            lineIndex,
                            "Dimension of ${shape.type} size should be ${shape.type.sizeDimension}!"
                    )
                }

                shape.size = value
            }
            AttributeNode.Type.Color -> {
                if (value.dimension != 3) {
                    throw InterpretException(
                            lineIndex,
                            "Dimension of color should be 3!"
                    )
                }

                shape.color = value
            }
            AttributeNode.Type.Position -> {
                if (value.dimension != 2) {
                    throw InterpretException(
                            lineIndex,
                            "Dimension of position should be 2!"
                    )
                }

                shape.position = value
            }
        }

        lastUsedObject = shape
    }

    private fun setShapeBorderAttribute(
            nameNode: NameNode,
            attributeNode: AttributeNode,
            valueNode: ValueNode
    ) {
        val shape = getShapeByName(nameNode).second
        val value = valueNodeToValue(valueNode)
        val lineIndex = valueNode.token.lineIndex

        when (attributeNode.type) {
            AttributeNode.Type.Size -> {
                if (value.dimension != 1) {
                    throw InterpretException(
                            lineIndex,
                            "Dimension of border size should be 1!"
                    )
                }

                shape.borderSize = value
            }
            AttributeNode.Type.Color -> {
                if (value.dimension != 3) {
                    throw InterpretException(
                            lineIndex,
                            "Dimension of color should be 3!"
                    )
                }

                shape.borderColor = value
            }
            AttributeNode.Type.Position -> {
                throw InterpretException(
                        lineIndex,
                        "Invalid attribute for border!"
                )
            }
        }

        lastUsedObject = shape
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

    private fun valueNodeToValue(valueNode: ValueNode) =
            when (valueNode.type) {
                ValueNode.Type.Tuple ->
                    tupleNodeToValue(valueNode.value as TupleNode)
                ValueNode.Type.Number ->
                    numberNodeToValue(valueNode.value as NumberNode)
                ValueNode.Type.Color ->
                    colorNodeToValue(valueNode.value as ColorNode)
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
