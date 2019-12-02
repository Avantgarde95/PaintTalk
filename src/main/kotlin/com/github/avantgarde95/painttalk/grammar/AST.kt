package com.github.avantgarde95.painttalk.grammar

/**
 * <Input> ::= <Sentence> ("." <Sentence>)* "."
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

sealed class AST(
    val token: Token,
    val childs: List<AST>
) {
    class Input(
        token: Token,
        val sentences: List<Sentence>
    ) : AST(token, sentences)

    class Sentence(
        token: Token,
        val sentences: List<BasicSentence>
    ) : AST(token, sentences)

    class BasicSentence(
        token: Token,
        val type: Type,
        val firstName: Name? = null,
        val shape: Shape? = null,
        val order: Order? = null,
        val secondName: Name? = null,
        val target: Target? = null,
        val value: Value? = null
    ) : AST(
        token, listOfNotNull(
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

    class ShapeSentence(
        token: Token,
        val name: Name,
        val shape: Shape
    ) : AST(token, listOf(name, shape))

    class OrderSentence(
        token: Token,
        val firstName: Name,
        val order: Order,
        val secondName: Name
    ) : AST(token, listOf(firstName, order, secondName))

    class ValueSentence(
        token: Token,
        val target: Target,
        val value: Value
    ) : AST(token, listOf(target, value))

    class Target(
        token: Token,
        val type: Type,
        val attribute: Attribute,
        val area: Area? = null,
        val obj: Object? = null
    ) : AST(token, listOfNotNull(attribute, area, obj)) {
        enum class Type {
            Attribute,
            AreaAttribute,
            IndirectAttribute,
            IndirectAreaAttribute
        }

        override fun getName() = "${super.getName()}.$type"
    }

    class Object(
        token: Token,
        val type: Type,
        val obj: AST
    ) : AST(token, listOf(obj)) {
        enum class Type {
            Name,
            Canvas
        }

        override fun getName() = "${super.getName()}.$type"
    }

    class Value(
        token: Token,
        val type: Type,
        val value: AST
    ) : AST(token, listOf(value)) {
        enum class Type {
            Number,
            Tuple,
            Color
        }

        override fun getName() = "${super.getName()}.$type"
    }

    class Canvas(
        token: Token
    ) : AST(token, emptyList())

    class Name(
        token: Token
    ) : AST(token, emptyList())

    class Shape(
        token: Token,
        val type: Type
    ) : AST(token, emptyList()) {
        enum class Type {
            Circle,
            Square,
            Ellipse,
            Rectangle
        }

        override fun getName() = "${super.getName()}.$type"
    }

    class Order(
        token: Token,
        val type: Type
    ) : AST(token, emptyList()) {
        enum class Type {
            Front,
            Behind
        }

        override fun getName() = "${super.getName()}.$type"
    }

    class Attribute(
        token: Token,
        val type: Type
    ) : AST(token, emptyList()) {
        enum class Type {
            Position,
            Size,
            Color
        }

        override fun getName() = "${super.getName()}.$type"
    }

    class Area(
        token: Token,
        val type: Type
    ) : AST(token, emptyList()) {
        enum class Type {
            Border,
            Inside
        }

        override fun getName() = "${super.getName()}.$type"
    }

    class Tuple(
        token: Token,
        val numbers: List<Number>
    ) : AST(token, numbers)

    class Number(
        token: Token
    ) : AST(token, emptyList())

    class Color(
        token: Token,
        val type: Type
    ) : AST(token, emptyList()) {
        enum class Type {
            Red,
            Blue,
            Green,
            White,
            Black
        }

        override fun getName() = "${super.getName()}.$type"
    }

    override fun toString(): String {
        return toStringWithDepth(0)
    }

    private fun toStringWithDepth(depth: Int): String {
        val prefix = when (depth) {
            0 -> ""
            else -> "${" ".repeat(3 * (depth - 1))}+- "
        }

        val thisString = "${prefix}Node(${getName()} at $token)"

        return when {
            childs.isEmpty() -> thisString
            else -> thisString + "\n" + childs.joinToString("\n") {
                it.toStringWithDepth(depth + 1)
            }
        }
    }

    open fun getName() = this::class.simpleName!!
}
