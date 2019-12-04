package com.github.avantgarde95.painttalk.interpret

import com.github.avantgarde95.painttalk.grammar.ASTNode

class InterpretException(
        val lineIndex: Int,
        message: String
) : Exception(message) {
    constructor(
            node: ASTNode,
            message: String
    ) : this(node.token.lineIndex, message)
}
