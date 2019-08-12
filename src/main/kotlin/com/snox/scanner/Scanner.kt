package com.snox.scanner

import com.snox.token.Token
import com.snox.token.TokenType

private val tokens = ArrayList<Token>()
private val keywords = hashMapOf("and" to TokenType.AND, "or" to TokenType.OR, "if" to TokenType.IF, "else" to TokenType.ELSE,
        "nil" to TokenType.NIL, "fun" to TokenType.FUN, "while" to TokenType.WHILE, "for" to TokenType.FOR, "print" to TokenType.PRINT,
        "return" to TokenType.RETURN, "var" to TokenType.VAR, "true" to TokenType.TRUE, "false" to TokenType.FALSE, "class" to TokenType.CLASS,
        "super" to TokenType.SUPER, "this" to TokenType.THIS)

/**
 * This class represents the scanner. A scanner scans the entire source code (line by line) and defragmentates this string
 * into (multiple) valid tokens (or reports an error of its invalid).
 */

data class Scanner(val source: String) {

    private var start = 0
    private var current = 0
    private var line = 1

    /**
     * The wrapper function for scanning all tokens until the end of the source code.
     *
     * Note that this function puts a special EOF (end of file) into the list of tokens when the file has been
     * read to end.
     */

    fun scanTokens(): List<Token> {

        while (!isAtEnd()) {
            start = current
            scanToken()
        }
        tokens.add(Token(TokenType.EOF, "", null, line))
        return tokens
    }

    /**
     *  This is the main function if the scanner class.
     *  It scans the source string "character by character" and check what token is the most valid (and the most likely one).
     *
     *  It made various distinctions between single character (and two character) tokens and is able to scan string
     *  and number tokens (if they're formatted correctly).
     *
     *  Note that comments (// and whitespaces as well as \r and \t) are simply ignored.
     *  The line field is responsible for a better error reporting functionality.
     *
     * TODO: Coalescing the single error messages into one (in case of many unexpected characters)
     */

    private fun scanToken() {
        val c = advance()

        when (c) {
            '{' -> addToken(TokenType.LEFT_BRACE)
            '}' -> addToken(TokenType.RIGHT_BRACE)
            '(' -> addToken(TokenType.LEFT_PAREN)
            ')' -> addToken(TokenType.RIGHT_PAREN)
            '.' -> addToken(TokenType.DOT)
            ',' -> addToken(TokenType.COMMA)
            ';' -> addToken(TokenType.SEMI_COL)
            '+' -> addToken(TokenType.PLUS)
            '-' -> addToken(TokenType.MINUS)
            '*' -> addToken(TokenType.STAR)
            '!' -> addToken(if (match('=')) TokenType.BANG_EQ else TokenType.BANG)
            '=' -> addToken(if (match('=')) TokenType.EQUAL_EQUAL else TokenType.EQUAL)
            '>' -> addToken(if (match('=')) TokenType.GREATER_EQ else TokenType.GREATER)
            '<' -> addToken(if (match('=')) TokenType.SMALLER_EQ else TokenType.SMALLER)
            '"' -> string()
            '/' -> {
                if (match('/')) {
                    while (peek() != '\n' && !isAtEnd()) {
                        advance()
                    }
                } else addToken(TokenType.SLASH)
            }
            '\n' -> line++
            in '0'..'9' -> number()
            else -> {
                if (isAlpha(c)) identifier()
                else if(c != ' ' && c != '\r' && c != '\t') com.snox.error(line, "Unexpected Character.")
            }
        }

    }

    /**
     * This method checks whether the end of the string has been reached or not
     */
    private fun isAtEnd() = current >= source.length

    /**
     * Advance and return the current char
     */
    private fun advance(): Char {
        current++
        return source[current - 1]
    }

    /**
     * Add a token to the list without a special literal
     */
    private fun addToken(type: TokenType) {
        addToken(type, null)
    }

    /**
     * Add a token to the list WITH a special literal (for example tokens of type string)
     */
    private fun addToken(type: TokenType, literal: Any?) {
        val snoxeme = source.substring(start, current)
        tokens.add(Token(type, snoxeme, literal, line))
    }

    /**
     * Search for the end of the current string token (if there's none report an error).
     * Multiple lines strings are supported.
     *
     * The actual value of a string token is the string (but with the " omitted)
     */
    private fun string() {
        while (!isAtEnd() && peek() != '"') {
            if (peek() == '\n') line++
            advance()
        }

        if (isAtEnd()) com.snox.error(line, "Unterminated String.")

        advance()

        val value = source.substring(start + 1, current - 1)
        addToken(TokenType.STRING, value)
    }

    /**
     * Scans a number token of format [0-9]*.[0-9]*
     */
    private fun number() {
        while (isDigit(peek())) {
            advance()
        }
        if (peek() == '.' && isDigit(peekNext())) advance()

        while (isDigit(peek())) {
            advance()
        }

        addToken(TokenType.NUMBER, source.substring(start, current).toDouble())

    }

    /**
     * Determines whether a given char is a digit or not
     */
    private fun isDigit(c: Char) = c in '0'..'9'

    /**
     * Checks, if a character is an underscore or a letter (lowercae OR uppercase)
     */
    private fun isAlpha(c: Char) = c == '_' || c in 'a'..'z' || c in 'A'..'Z'

    /**
     * Checks, whether a character is a letter or a digit
     */
    private fun isAlphanumeric(c: Char) = isAlpha(c) || isDigit(c)

    /**
     * This method "generates" an identifier. We also determine whether we encountered a reserved keyword or an identifier.
     *
     * It uses the MAX MUNCH principle: The more character a string matches the more probabilistic it is that it is THIS special
     * keyword/identifier.
     */
    private fun identifier() {
        while (isAlphanumeric(peek())) {
            advance()
        }
        val identifier = source.substring(start, current)

        if (keywords.containsKey(identifier)) addToken(keywords.getValue(identifier))
        else addToken(TokenType.IDENTIFIER)
    }

    /**
     * Determines whether the next char matches the expected char (If it's at the end it returns false)
     */
    private fun match(expected: Char): Boolean {
        if (isAtEnd()) return false
        if (source[current] != expected) return false

        current++
        return true
    }

    /**
     * Peek at the current char (that's what is called a lookahead) and return if not at the end else return null char
     */
    private fun peek() = if (isAtEnd()) '\u0000' else source[current]

    /**
     * Peek at the next char and return if not at the end else return null char
     */
    private fun peekNext() = if (current + 1 >= source.length) '\u0000' else source[current + 1]
}