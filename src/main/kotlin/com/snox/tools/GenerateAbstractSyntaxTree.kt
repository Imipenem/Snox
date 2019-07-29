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
    val outputDir = "path/to/parser/expr"
    defineAst(outputDir, "Expr", arrayListOf(
            "Binary   ; left:Expr, operator:Token, right:Expr",
            "Grouping ; expression:Expr",
            "Literal  ; value:Any",
            "Unary    ; operator:Token, right:Expr"
    ))

}

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

private fun defineVisitor(writer: PrintWriter, baseName: String, types: ArrayList<String>){

    writer.println("interface Visitor <T> { ")
    for (e in types){
        val descr = e.trim().split(";")
        val typeName = descr[0].trim()
        writer.println("fun visit$typeName$baseName(${baseName.toLowerCase()}:$typeName):T")
    }
    writer.println("}")
}