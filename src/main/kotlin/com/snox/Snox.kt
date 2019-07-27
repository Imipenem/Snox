package com.snox

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.exitProcess

class Snox {

}

var hadError = false

@Throws (IOException::class)
fun main(args:Array<String>) {
    when{
        args.size > 1 -> {
            println("Usage: Snox [script]")
            exitProcess(64)
        }
        args.size == 1 -> runFile(args[0]) //run code from file
        else -> runPrompt() //run interactively step by step
    }

}

/**
 * This method is invoked when a file will be used for compilation/ the interpreter as the file containing the
 * source code.
 */

@Throws (IOException::class)
private fun runFile(path:String){
    val bytes = Files.readAllBytes(Paths.get(path))
    run(String(bytes, Charset.defaultCharset()))
}

/**
 * This method is used for an interactive "line by line" scanning from the command line.
 * Each line will be read and scanned separately.
 *
 * Note that the hadError field will be reset after each run() invocation to not crash the whole session because of
 * one error (like a missing "," or sth. similar)
 */
@Throws (IOException::class)
private fun runPrompt(){
    val input = InputStreamReader(System.`in`)
    val reader = BufferedReader(input)

    while(true){
        println("> ")
        run(reader.readLine())
        hadError = false
    }
}
/**
 * Main function for scanning the input (whether it´s a single command line line or ByteStream of a read File).
 *
 * Purpose of this function is to scan the source string and "divide" it into valid tokens that can be used for
 * further compilation.
 *
 * TODO: Current state: Simply print out the tokens for testing progress (it won´t compile as it is now)
 */

private fun run(source:String){
    if(hadError) exitProcess(65)
    val scanner = Scanner(source) //Scanner class will be created asap
    val tokens = scanner.scanTokens()

    for (e in tokens){
        println(e)
    }
}

fun error(line:Int, message:String){
    report(line,"",message)
}

private fun report (line:Int, where:String, message:String){
    System.err.println((
            "[line $line] Error$where: $message"))
    hadError = true
}