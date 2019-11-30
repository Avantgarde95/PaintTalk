package com.github.avantgarde95.painttalk

object Util {
    fun getResourceAsStream(path: String) =
        Thread.currentThread().contextClassLoader.getResourceAsStream(path)!!

    fun getResourceAsString(path: String) =
        getResourceAsStream(path).bufferedReader().use { it.readText() }
}
