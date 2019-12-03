package com.github.avantgarde95.painttalk.grammar

/**
 * <Input> ::= <Sentence> "." (<Sentence> ".")*
 *
 * <Sentence> ::= <BasicSentence> ("and" <BasicSentence>)*
 *
 * <BasicSentence> ::= <Name> "is" <Shape>
 *                   | <Name> "is" <Order> <Name>
 *                   | <Target> "is" <Value>
 *
 * <Target> ::= "its" <Attribute>
 *            | <Attribute> "of" "its" <Area>
 *            | <Attribute> "of" <Area> "of" <Object>
 *            | <Attribute> "of" <Object>
 *
 * <Object> ::= <Name> | <Canvas>
 * <Value> ::= <Number> | <Tuple> | <Color>
 *
 * <Canvas> ::= "canvas"
 * <Name> ::= [a-zA-Z_][a-zA-Z0-9_]*
 * <Shape> ::= "Circle" | "Square" | ...
 * <Order> ::= "in" "front" "of" | "behind"
 * <Attribute> ::= "position" | "size" | ...
 * <Area> ::= "border" | "inside"
 * <Tuple> ::= "(" <Number> ("," <Number>)* ")"
 * <Number> ::= "3" | "15" | "230" | ...
 * <Color> ::= "red" | "green" | ...
 */

class AST(
    val root: InputNode
) {
    fun toPrettyString() = root.toPrettyString(0)
}

sealed class ASTNode(
    val token: Token,
    val childs: List<ASTNode>
) {
    fun toPrettyString(depth: Int): String {
        val prefix = when (depth) {
            0 -> ""
            else -> "${" ".repeat(3 * (depth - 1))}+- "
        }

        val thisString = "${prefix}${getName()} at $token"

        return when {
            childs.isEmpty() -> thisString
            else -> thisString + "\n" + childs.joinToString("\n") {
                it.toPrettyString(depth + 1)
            }
        }
    }

    protected open fun getName() = this::class.simpleName!!
}

class InputNode(
    token: Token,
    val sentences: List<SentenceNode>
) : ASTNode(token, sentences)

class SentenceNode(
    token: Token,
    val sentences: List<BasicSentenceNode>
) : ASTNode(token, sentences)

class BasicSentenceNode(
    token: Token,
    val type: Type,
    val firstName: NameNode? = null,
    val shape: ShapeNode? = null,
    val order: OrderNode? = null,
    val secondName: NameNode? = null,
    val target: TargetNode? = null,
    val value: ValueNode? = null
) : ASTNode(
    token,
    listOfNotNull(
        firstName,
        shape,
        order,
        secondName,
        target,
        value
    )
) {
    enum class Type {
        Shape,
        Order,
        Value
    }

    override fun getName() = "${super.getName()}.$type"
}

class TargetNode(
    token: Token,
    val type: Type,
    val attribute: AttributeNode,
    val area: AreaNode? = null,
    val obj: ObjectNode? = null
) : ASTNode(token, listOfNotNull(attribute, area, obj)) {
    enum class Type {
        Attribute,
        AreaAttribute,
        IndirectAttribute,
        IndirectAreaAttribute
    }

    override fun getName() = "${super.getName()}.$type"
}

class ObjectNode(
    token: Token,
    val type: Type,
    val obj: ASTNode
) : ASTNode(token, listOf(obj)) {
    enum class Type {
        Name,
        Canvas
    }

    override fun getName() = "${super.getName()}.$type"
}

class ValueNode(
    token: Token,
    val type: Type,
    val value: ASTNode
) : ASTNode(token, listOf(value)) {
    enum class Type {
        Number,
        Tuple,
        Color
    }

    override fun getName() = "${super.getName()}.$type"
}

class CanvasNode(
    token: Token
) : ASTNode(token, emptyList())

class NameNode(
    token: Token
) : ASTNode(token, emptyList())

class ShapeNode(
    token: Token,
    val type: Type
) : ASTNode(token, emptyList()) {
    enum class Type {
        Circle,
        Square,
        Ellipse,
        Rectangle
    }

    override fun getName() = "${super.getName()}.$type"
}

class OrderNode(
    token: Token,
    val type: Type
) : ASTNode(token, emptyList()) {
    enum class Type {
        Front,
        Behind
    }

    override fun getName() = "${super.getName()}.$type"
}

class AttributeNode(
    token: Token,
    val type: Type
) : ASTNode(token, emptyList()) {
    enum class Type {
        Position,
        Size,
        Color
    }

    override fun getName() = "${super.getName()}.$type"
}

class AreaNode(
    token: Token,
    val type: Type
) : ASTNode(token, emptyList()) {
    enum class Type {
        Border,
        Inside
    }

    override fun getName() = "${super.getName()}.$type"
}

class TupleNode(
    token: Token,
    val numbers: List<NumberNode>
) : ASTNode(token, numbers)

class NumberNode(
    token: Token
) : ASTNode(token, emptyList())

class ColorNode(
    token: Token,
    val type: Type
) : ASTNode(token, emptyList()) {
    enum class Type {
        Red,
        Blue,
        Green,
        White,
        Black
    }

    override fun getName() = "${super.getName()}.$type"
}
