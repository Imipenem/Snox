package com.snox

private val tokens = ArrayList<Token>()

data class Scanner(val source:String) {

    private var start = 0
    private var current = 0
    private var line = 1

    fun scanTokens(): List<Token>{

        while(!isAtEnd()){
            start = current
            scanToken()
        }
        tokens.add(Token(TokenType.EOF,"",null, line))
        return tokens
    }

    /**
     *
     *
     * TODO: Coalescing the single error messages into one (in case of many unexpected characters)
     */

    private fun scanToken(){
        val c = advance()

        when(c) {
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
                if(match('/')){
                    while (peek() != '\n' && !isAtEnd()){
                        advance()
                    }
                }
                else addToken(TokenType.SLASH)
            }
            '\n' -> line++
            in '0'..'9' -> number()
            else -> if(c != ' ' && c != '\r' && c != '\t') error(line, "Unexpected Character.")
        }

    }

    private fun isAtEnd() = current >= source.length

    private fun advance():Char {
        current++
        return source[current - 1]
    }

    private fun addToken(type:TokenType){
        addToken(type,null)
    }

    private fun addToken(type: TokenType, literal:Any?){
        val snoxeme = source.substring(start,current)
        tokens.add(Token(type, snoxeme, literal, line))
    }

    private fun string() {
        while(!isAtEnd() && peek() != '"'){
            if(peek() == '\n') line++
            advance()
        }

        if(isAtEnd()) error(line, "Unterminated String.")

        advance()

        val value = source.substring(start + 1, current - 1)
        addToken(TokenType.STRING,value)
    }

    private fun number(){
        while (isDigit(peek())){
            advance()
        }
        if(peek() == '.' && isDigit(peekNext())) advance()

        while (isDigit(peek())){
            advance()
        }

        addToken(TokenType.NUMBER, source.substring(start, current).toDouble())

    }

    private fun isDigit(c:Char) = c in '0'..'9'

    private fun match(expected:Char): Boolean{
        if(isAtEnd()) return false
        if(source[current] == expected) return true

        current++
        return false
    }

    private fun peek()  = if(isAtEnd()) '\u0000' else source[current]

    private fun peekNext() = if(current + 1 >= source.length) '\u0000' else source[current + 1]
}