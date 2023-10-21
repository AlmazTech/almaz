package com.almaztech.almaz

import com.almaztech.almaz.lexer.functions.AlmazFunctionsBaseListener
import com.almaztech.almaz.lexer.functions.AlmazFunctionsLexer
import com.almaztech.almaz.lexer.functions.AlmazFunctionsParser
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.misc.Utils.readFile
import org.antlr.v4.runtime.tree.ParseTreeWalker
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.*

class AlmazCompilerTest : AlmazFunctionsBaseListener() {
    private val classWriter = ClassWriter(ClassWriter.COMPUTE_FRAMES)
    private var methodVisitor: MethodVisitor? = null
    private val variableTable = mutableMapOf<String, Int>()

    init {
        classWriter.visit(V1_8, ACC_PUBLIC, "Program", null, "java/lang/Object", null)
        methodVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null)
        methodVisitor?.visitCode()
    }

    override fun exitProgram(ctx: AlmazFunctionsParser.ProgramContext) {
        methodVisitor?.visitInsn(RETURN)
    }

    override fun exitPrintStatement(ctx: AlmazFunctionsParser.PrintStatementContext) {
        methodVisitor?.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
        visitExpression(ctx.expression())
        methodVisitor?.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/Object;)V", false)
    }

    private fun visitExpression(expressionContext: AlmazFunctionsParser.ExpressionContext) {
        visitTerm(expressionContext.term(0))
        for (i in 1 until expressionContext.term().size) {
            val operator = expressionContext.getChild(i * 2 - 1).text
            val term = expressionContext.term(i)
            if (operator == "+") {
                methodVisitor?.visitInsn(IADD)
            } else if (operator == "-") {
                methodVisitor?.visitInsn(ISUB)
            }
            visitTerm(term)
        }
    }

    private fun visitTerm(termContext: AlmazFunctionsParser.TermContext) {
        visitFactor(termContext.factor(0))
        for (i in 1 until termContext.factor().size) {
            val operator = termContext.getChild(i * 2 - 1).text
            val factor = termContext.factor(i)
            if (operator == "*") {
                methodVisitor?.visitInsn(IMUL)
            } else if (operator == "/") {
                methodVisitor?.visitInsn(IDIV)
            }
            visitFactor(factor)
        }
    }

    private fun visitFactor(factorContext: AlmazFunctionsParser.FactorContext) {
        val firstSubExpression = factorContext.expression()
        if (firstSubExpression != null) {
            visitExpression(firstSubExpression)
            return
        }

        if (factorContext.INT() != null) {
            val intValue = factorContext.INT().text.toInt()
            methodVisitor?.visitLdcInsn(intValue)
            return
        }

        if (factorContext.STRING() != null) {
            val stringValue = factorContext.STRING().text
            visitStringLiteral(stringValue)
            return
        }

        if (factorContext.ID() != null) {
            val variableName = factorContext.ID().text
            val variableIndex = variableTable[variableName]
            if (variableIndex != null) {
                methodVisitor?.visitVarInsn(ILOAD, variableIndex)
            } else {
                // Handle error: Variable is not declared
            }
            return
        }
    }

    // Додано новий метод для генерації коду для рядкових літералів
    private fun visitStringLiteral(stringLiteral: String) {
        methodVisitor?.visitLdcInsn(stringLiteral)
    }

    fun compile(input: String): ByteArray {
        val lexer = AlmazFunctionsLexer(CharStreams.fromString(input))
        val tokens = CommonTokenStream(lexer)
        val parser = AlmazFunctionsParser(tokens)
        val tree = parser.program()

        val walker = ParseTreeWalker()
        walker.walk(this, tree)

        methodVisitor?.visitMaxs(2, 2)
        methodVisitor?.visitEnd()
        classWriter.visitEnd()
        return classWriter.toByteArray()
    }

    fun execute(bytecode: ByteArray) {
        val classLoader = ByteArrayClassLoader()
        val clazz = classLoader.defineClass("Program", bytecode)
        val method = clazz.getDeclaredMethod("main", Array<String>::class.java)
        method.invoke(null, emptyArray<String>())
    }
}

fun main() {
    val data = readFile("$PATH\\test.almz")
    val compiler = AlmazCompilerTest()
    val bytecode = compiler.compile(data.joinToString(""))
    compiler.execute(bytecode)
}

class ByteArrayClassLoader : ClassLoader() {
    fun defineClass(name: String, bytecode: ByteArray): Class<*> {
        return defineClass(name, bytecode, 0, bytecode.size)
    }
}

const val PATH = "C:\\Programming\\Almaz\\almaz\\compiler\\src\\main\\kotlin\\com\\almaztech\\almaz"
