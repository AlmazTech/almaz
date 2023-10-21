package com.almaztech.almaz.backend

interface Compilable {
    fun compile(code: String): ByteArray
}