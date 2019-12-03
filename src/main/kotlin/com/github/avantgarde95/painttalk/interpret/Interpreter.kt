package com.github.avantgarde95.painttalk.interpret

import com.github.avantgarde95.painttalk.grammar.*

object Interpreter {
    fun toPicture(ast: AST) = InterpreterInstance().interpret(ast)
}

private class InterpreterInstance {
    private val picture = Picture()

    fun interpret(ast: AST): Picture {
        interpretInput(ast.root)
        return picture
    }

    private fun interpretInput(inputNode: InputNode) {

    }

    private fun interpretSentence(sentenceNode: SentenceNode) {

    }

    private fun interpretBasicSentence(basicSentenceNode: BasicSentenceNode) {

    }

    private fun interpretTarget(targetNode: TargetNode) {

    }

    private fun interpretObject(objectNode: ObjectNode) {

    }

    private fun interpretValue(valueNode: ValueNode) {

    }

    private fun interpretCanvas(canvasNode: CanvasNode) {

    }

    private fun interpretOrder(orderNode: OrderNode) {

    }

    private fun interpretAttribute(attributeNode: AttributeNode) {

    }

    private fun interpretArea(areaNode: AreaNode) {

    }

    private fun interpretTuple(tupleNode: TupleNode) {

    }

    private fun interpretNumber(numberNode: NumberNode) {

    }

    private fun interpretColor(colorNode: ColorNode) {

    }
}
