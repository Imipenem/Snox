package com.snox.tools

import java.io.IOException
import java.io.PrintWriter

/**
 * A little "script" that generates an abstract syntax tree ("scripting the syntax trees classes)
 */

@Throws (IOException::class)
fun main(args:Array<String>){
    //if(args.size != 1){
      //  error("Script Usage: Generate_AST <output directory>")
    //}
    val outputDir = "path/to/exprAndStmt"
    defineAst(outputDir, "Expr", arrayListOf(
            "Binary   ; val left:Expr, val operator:Token, val right:Expr",
            "Grouping ; val expression:Expr",
            "Literal  ; val value:Any?",
            "Unary    ; val operator:Token, val right:Expr"
    ))
    defineAst(outputDir, "Stmt", arrayListOf(
            "Expression   ; val expression:Expr",
            "Print       ; val expression:Expr"
    ))

}

/**
 * This function writes the basic AST setup class file with an Expression class as the superclass and the different expressions
 * derived from them (for example binary expressions like "a + b").
 *
 * New types of expressions can be simply added by adding them into the list above in main()!
 */

@Throws (IOException::class)
private fun defineAst(outputDir:String, baseName:String, types: ArrayList<String>){

    val path = "$outputDir/$baseName.kt"
    val writer = PrintWriter(path, "UTF-8")

    writer.println("package com.snox.parser.expr")
    writer.println("import com.snox.token.Token")
    writer.println()
    writer.println("/**")
    writer.println("* Auto generated AST for parsing SNOX expressions (Superclass Expr for clarification only) ")
    writer.println("*/")
    writer.println("abstract class Expr{")
    writer.println("abstract fun <T> accept(visitor: Visitor<T>):T")
    writer.println("}")
    writer.println()

    defineVisitor(writer,baseName,types)
    writer.println()



    for(e in types){
        val descr = e.trim().split(";")
        writer.println("class ${descr[0].trim()}(${descr[1]}):Expr(){")
        writer.println("override fun <T> accept(visitor:Visitor<T>) = visitor.visit${descr[0].trim()}$baseName(this)")
        writer.println("}")
        writer.println()
    }
    writer.close()
}

/**
 * This function defines the visitor interface with a visit() for each expression (subclass) and prints them.
 */
private fun defineVisitor(writer: PrintWriter, baseName: String, types: ArrayList<String>){

    writer.println("interface Visitor <T> { ")
    for (e in types){
        val descr = e.trim().split(";")
        val typeName = descr[0].trim()
        writer.println("fun visit$typeName$baseName(${baseName.toLowerCase()}:$typeName):T")
    }
    writer.println("}")
}