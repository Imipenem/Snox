package com.snox

import com.snox.parser.Parser
import com.snox.scanner.Scanner
import com.snox.token.Token
import com.snox.token.TokenType
import com.snox.tools.AstPrinter
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.exitProcess

class Snox

var hadError = false

@Throws(IOException::class)
fun main(args: Array<String>) {
    when {
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

@Throws(IOException::class)
private fun runFile(path: String) {
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
@Throws(IOException::class)
private fun runPrompt() {
    val input = InputStreamReader(System.`in`)
    val reader = BufferedReader(input)

    while (true) {
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
 */

private fun run(source: String) {
    if (hadError) exitProcess(65)
    val scanner = Scanner(source)
    val tokens = scanner.scanTokens()

    val parser = Parser(tokens)
    val expression = parser.parse()

    if (hadError) return

    println(AstPrinter().print(expression!!))
}

fun error(line: Int, message: String) {
    report(line, "", message)
}

private fun report(line: Int, where: String, message: String) {
    System.err.println((
            "[line $line] Error$where: $message"))
    hadError = true
}

fun error(token: Token, message: String) {
    if (token.type == TokenType.EOF) report(token.line, "at end", message)
    else report(token.line, " at '${token.snoxeme}'", message)
}