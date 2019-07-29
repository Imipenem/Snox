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

    writer.println("import com.snox.token.Token")
    writer.println()
    writer.println("abstract class Expr")
    writer.println()

    for(e in types){
        val descr = e.trim().split(";")
        writer.println("class ${descr[0].trim()}(${descr[1]}):Expr()")
        writer.println()
    }
    writer.close()
}