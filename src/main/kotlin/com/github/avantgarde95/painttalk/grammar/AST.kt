package com.github.avantgarde95.painttalk.grammar

/**
 * <Input> ::= (<Sentence> ".")*
 *
 * <Sentence> ::= <BasicSentence> ("and" <BasicSentence>)*
 * <BasicSentence> ::= <ShapeSentence> | <OrderSentence> | <ValueSentence>
 * <ShapeSentence> ::= <Name> "is" <Shape>
 * <OrderSentence> ::= <Name> "is" <Order> <Name>
 * <ValueSentence> ::= <Target> "is" <Value>
 *
 * <Target> ::= <AttributeTarget>
 *            | <AreaAttributeTarget>
 *            | <IndirectAttributeTarget>
 *            | <IndirectAreaAttributeTarget>
 * <AttributeTarget> ::= <Attribute> "of" <Object>
 * <AreaAttributeTarget> ::= <Attribute> "of" <Area> "of" <Object>
 * <IndirectAttributeTarget> ::= "its" <Attribute>
 * <IndirectAreaAttributeTarget> ::= <Attribute> "of" "its" <Area>
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
 * <Number> ::= [0-9]+
 * <Tuple> ::= "(" <Number> ("," <Number>)* ")"
 * <Color> ::= "red" | "green" | ...
 */

class AST(
    val type: Type,
    val token: Token,
    val childs: List<AST>
) {
    enum class Type {
        Input,
        Sentence,
        BasicSentence,
        ShapeSentence,
        OrderSentence,
        ValueSentence,
        Target,
        AttributeTarget,
        AreaAttributeTarget,
        IndirectAttributeTarget,
        IndirectAreaAttributeTarget,
        Object,
        Value,
        Canvas,
        Name,
        Shape,
        Order,
        Attribute,
        Area,
        Number,
        Tuple,
        Color
    }

    override fun toString(): String {
        return toString("")
    }

    private fun toString(prefix: String): String {
        val newPrefix = "  $prefix"

        val thisString = "$prefix${type.name}"

        return when {
            childs.isEmpty() -> {
                thisString
            }
            else -> {
                val childsString = childs.joinToString("\n") { it.toString(newPrefix) }
                "$thisString\n$childsString"
            }
        }
    }
}
