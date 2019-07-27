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
            else -> error(line, "Unexpected Character.")
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

    private fun match(expected:Char): Boolean{
        if(isAtEnd()) return false
        if(source[current] == expected) return true

        current++
        return false
    }


}