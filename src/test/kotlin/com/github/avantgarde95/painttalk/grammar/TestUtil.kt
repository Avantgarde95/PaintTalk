package com.github.avantgarde95.painttalk.grammar

import org.junit.Assert

object TestUtil {
    fun <T> assertListEquals(expected: List<T>, actual: List<T>) {
        Assert.assertArrayEquals(
            expected.map { it as Any }.toTypedArray(),
            actual.map { it as Any }.toTypedArray()
        )
    }
}
