package com.snox.parser

import com.snox.parser.expr.*
import com.snox.error as err
import com.snox.token.Token
import com.snox.token.TokenType
import java.lang.RuntimeException

/**
 * This class represents the Parser used to parse scanned SNOX tokens into an AST besides some basic syntax error handling.
 */

class Parser(private val tokens: List<Token>) {

    /**
     * Inner class to represent a ParseError
     */
    class ParseError : RuntimeException()

    private var current = 0

    /**
     * This is the actual "parse" main function.
     *
     * For now it parses declarations (wont compile as for now)
     */
    fun parse(): List<Stmt?> {

        val statements = ArrayList<Stmt?>()

        while (!isAtEnd()) {
            statements.add(declaration())
        }
        return statements
    }

    /**
     * This function decides whether a declaration is a variable declaration or another statement.
     */
    private fun declaration():Stmt? {
        try {
            if (match(TokenType.VAR)) return varDeclaration()

            return statement()
        }
        catch (e:ParseError){
            synchronize()
            return null
        }
    }

    /**
     * This function is called when a variable is declared.
     *
     * Note that in case of no initialization, the default value is null.
     */
    private fun varDeclaration():Stmt {
        val name = consume(TokenType.IDENTIFIER, "Expected a variables name.")

        var initializer:Expr? = null
        if(match(TokenType.EQUAL)) initializer = expression()

        consume(TokenType.SEMI_COL, "Expected ; after declaration or statement.")
        return Var(name,initializer)
    }

    /**
     * This function differences between a print statement and an expression statement
     */
    private fun statement() = if(match(TokenType.PRINT)) printStatement() else expressionStatement()

    private fun printStatement():Stmt {
        val value = expression()
        consume(TokenType.SEMI_COL, "Expected ; after statement.")

        return Print(value)
    }

    /**
     * This function checks, if an expression always ends with a ; and then parses the expression.
     */
    private fun expressionStatement():Stmt {
        val value = expression()
        consume(TokenType.SEMI_COL, "Expected ; after statement.")

        return Expression(value)
    }

    private fun expression() = equality()

    /**
     * This function parses equality expressions (lowest precedence) into the AST. This works as follows:
     *
     * The left side of the equality is stored in a local var (expr). Then it performs the * operation
     * (that is how a while loop can be expressed in a CFG). It checks whether the current token matches
     * != or == as their operators and stores them (if encountered) in operator. then it calls the right
     * side of the expr and creates a new Binary Expression AST Node (Note how multiple comparisons are supported:
     * It uses the previous expr as the left hand argument for a new Binary Expression) -> left associative tree
     */

    private fun equality(): Expr {
        var expr = comparison()

        while (match(TokenType.BANG_EQ, TokenType.EQUAL_EQUAL)) {
            val operator = previous()
            val right = comparison()
            expr = Binary(expr, operator, right)
        }
        return expr
    }

    /**
     * This function is at most identical to equality.
     * The only differences are that its calling addition() for its "next higher precedence expression"
     * and the operators (>=, >, <=, <).
     */

    private fun comparison(): Expr {
        var expr = addition()

        while (match(TokenType.GREATER, TokenType.GREATER_EQ, TokenType.SMALLER, TokenType.SMALLER_EQ)) {
            val operator = previous()
            val right = addition()

            expr = Binary(expr, operator, right)
        }
        return expr
    }

    /**
     * @see equality
     */
    private fun addition(): Expr {
        var expr = multiplication()

        while (match(TokenType.PLUS, TokenType.MINUS)) {
            val operator = previous()
            val right = multiplication()
            expr = Binary(expr, operator, right)
        }
        return expr
    }

    /**
     * @see equality
     */
    private fun multiplication(): Expr {
        var expr = unary()

        while (match(TokenType.SLASH, TokenType.STAR)) {
            val operator = previous()
            val right = unary()
            expr = Binary(expr, operator, right)
        }
        return expr
    }

    /**
     * This function parses an unary expression.
     *
     * It checks whether it encounters a - or ! (unary operators) and depending on this, processes further.
     */
    private fun unary(): Expr {
        if (match(TokenType.MINUS, TokenType.BANG)) {
            val operator = previous()
            val right = unary()
            return Unary(operator, right)
        }
        return primary()
    }

    /**
     * This function handels primary expressions (highest level of precedence).
     *
     * Most of its CFG rules are nonterminals, so code is pretty much self-explaining.
     *
     * In case of a '(' (expect a ')' after) call expression() again.
     */
    private fun primary(): Expr {
        when {
            match(TokenType.FALSE) -> return Literal(false)
            match(TokenType.TRUE) -> return Literal(true)
            match(TokenType.NIL) -> return Literal(null)

            match(TokenType.STRING, TokenType.NUMBER) -> return Literal(previous().literal)

            match(TokenType.IDENTIFIER) -> return Variable(previous())

            match(TokenType.LEFT_PAREN) -> {
                val expr = expression()
                consume(TokenType.RIGHT_PAREN, "Expect closing ')' after expression.")
                return Grouping(expr)
            }
        }
        throw error(peek(), "Expect expression")
    }

    /**
     * This function checks if a specified token type is met after some other expression (like an closing ) after a (.
     *
     * Otherwise it throws an ParseError with a message indicating which kind of error occurred.
     */
    private fun consume(type: TokenType, message: String): Token {
        if (check(type)) return advance()

        throw error(peek(), message)
    }

    /**
     * Helper function that calls the error() of com/snox/Snox.kt as a ParseError
     */
    private fun error(token: Token, message: String): ParseError {
        err(token, message)
        return ParseError()
    }

    /**
     * !!!NOT IN USE ATM!!!
     *
     * Will be used for synchronizing the parser after an syntax/parse error occurred (Roughly spoken: Goto that place,
     * where a new statement begins that can be parsed)
     *
     * Useful so that syntax errors don´t crash the whole parse process.
     */
    private fun synchronize() {
        advance()

        while (!isAtEnd()) {
            if (previous().type == TokenType.SEMI_COL) return

            when (peek().type) {
                TokenType.CLASS, TokenType.VAR, TokenType.FUN, TokenType.IF, TokenType.WHILE,
                TokenType.PRINT, TokenType.RETURN, TokenType.FOR -> return
            }
            advance()
        }
    }


    /**
     * Helper function to determine whether an actual token type matches some concret ones.
     */
    private fun match(vararg types: TokenType): Boolean {
        for (type in types) {
            if (check(type)) {
                advance()
                return true
            }
        }
        return false
    }

    /**
     * When not at the end (TokenType != EOF) return if the current type matches @param type
     */
    private fun check(type: TokenType) = if (isAtEnd()) false else peek().type == type

    /**
     * This function updates the pointer to the current token and returns last token.
     */
    private fun advance(): Token {
        if (!isAtEnd()) current++
        return previous()
    }

    /**
     * @return the current token
     */
    private fun peek() = tokens[current]

    /**
     * @return If we´re at the end while parsing
     */
    private fun isAtEnd() = peek().type == TokenType.EOF

    /**
     * @return the last processed token (previous one)
     */
    private fun previous() = tokens[current - 1]
}