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

@Throws (IOException::class)
private fun runFile(path:String){
    val bytes = Files.readAllBytes(Paths.get(path))
    run(String(bytes, Charset.defaultCharset()))
}

@Throws (IOException::class)
private fun runPrompt(){
    val input = InputStreamReader(System.`in`)
    val reader = BufferedReader(input)

    while(true){
        println("> ")
        run(reader.readLine())
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
    val scanner = Scanner(source) //Scanner class will be created asap
    val tokens = scanner.scanTokens()

    for (e in tokens){
        println(e)
    }
}