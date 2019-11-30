package com.github.avantgarde95.painttalk.grammar

class Token(
    val type: Type,
    val value: String
) {
    enum class Type(val pattern: Pattern) {
        Canvas(StringPattern("canvas")),
        Circle(StringPattern("circle")),
        Square(StringPattern("square")),
        Ellipse(StringPattern("ellipse")),
        Rectangle(StringPattern("rectangle")),

        Red(StringPattern("red")),
        Blue(StringPattern("blue")),
        Green(StringPattern("green")),
        White(StringPattern("white")),
        Black(StringPattern("black")),

        Border(StringPattern("border")),
        Inside(StringPattern("inside")),

        Position(StringPattern("position")),
        Size(StringPattern("size")),
        Color(StringPattern("color")),

        Of(StringPattern("of")),
        Is(StringPattern("is")),
        In(StringPattern("in")),
        Its(StringPattern("its")),
        And(StringPattern("and")),
        Front(StringPattern("front")),
        Behind(StringPattern("behind")),

        LParen(StringPattern("(")),
        RParen(StringPattern(")")),
        Period(StringPattern(".")),
        Comma(StringPattern(",")),

        Number(RegexPattern("""^[0-9]+""".toRegex())),
        Name(RegexPattern("""^[a-zA-Z_][a-zA-Z0-9_]*""".toRegex())),
        Ignore(RegexPattern("""^\s|\n|\t""".toRegex()))
    }

    override fun toString() = "<$type \"$value\">"
}