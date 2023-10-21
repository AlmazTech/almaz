package com.almaztech.almaz.backend.loader

class ByteArrayClassLoader : ClassLoader() {
    fun defineClass(name: String, bytecode: ByteArray): Class<*> {
        return defineClass(name, bytecode, 0, bytecode.size)
    }
}