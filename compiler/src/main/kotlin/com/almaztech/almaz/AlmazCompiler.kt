package com.almaztech.almaz

import com.almaztech.almaz.backend.Compilable
import com.almaztech.almaz.backend.Executable
import com.almaztech.almaz.lexer.core.AlmazCoreBaseListener
import com.almaztech.almaz.lexer.core.AlmazCoreLexer
import com.almaztech.almaz.lexer.core.AlmazCoreParser
import com.almaztech.almaz.lexer.functions.AlmazFunctionsParser
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.misc.Utils.readFile
import org.antlr.v4.runtime.tree.ParseTreeWalker
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class AlmazCompiler : AlmazCoreBaseListener(), Compilable, Executable {
    private val classWriter = ClassWriter(ClassWriter.COMPUTE_FRAMES)
    private var methodVisitor: MethodVisitor? = null

    init {
        classWriter.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, "Program", null, "java/lang/Object", null)
        methodVisitor = classWriter.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null)
        methodVisitor?.visitCode()
    }

    override fun exitProgram(ctx: AlmazCoreParser.ProgramContext?) {
        methodVisitor?.visitInsn(Opcodes.RETURN)
    }

    override fun compile(code: String): ByteArray {
        val lexer = AlmazCoreLexer(CharStreams.fromString(code))
        val tokens = CommonTokenStream(lexer)
        val parser = AlmazCoreParser(tokens)
        val tree = parser.program()

        val walker = ParseTreeWalker()
        walker.walk(this, tree)

        methodVisitor?.visitMaxs(2, 2)
        methodVisitor?.visitEnd()
        classWriter.visitEnd()
        return classWriter.toByteArray()
    }

    override fun execute(bytecode: ByteArray) {
        val classLoader = ByteArrayClassLoader()
        val clazz = classLoader.defineClass("Program", bytecode)
        val method = clazz.getDeclaredMethod("main", Array<String>::class.java)
        method.invoke(null, emptyArray<String>())
    }
}

fun main() {
    val data = readFile("$PATH\\test.almz").joinToString("")
    val compiler = AlmazCompiler()
    val bytecode = compiler.compile(data)
    compiler.execute(bytecode)
}