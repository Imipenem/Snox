package com.snox.parser

import com.snox.error as err
import com.snox.parser.expr.Binary
import com.snox.parser.expr.Expr
import com.snox.parser.expr.Grouping
import com.snox.parser.expr.Literal
import com.snox.parser.expr.Unary
import com.snox.token.Token
import com.snox.token.TokenType
import java.lang.RuntimeException

/**
 *
 */

class Parser(private val tokens: List<Token>) {

    class ParseError:RuntimeException()

    private var current = 0

    /**
     * This is the actual "parse" main function.
     *
     * As for now, it can only parse one line expressions and otherwise it returns null, in
     * case of any Parse Errors thrown.
     */
    fun parse():Expr?{
        return try {
            expression()
        }
        catch (e:ParseError){
            null
        }
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

        while(match(TokenType.GREATER, TokenType.GREATER_EQ, TokenType.SMALLER, TokenType.SMALLER_EQ)){
            val operator = previous()
            val right =  addition()

            expr = Binary(expr, operator, right)
        }
        return expr
    }

    /**
     * @see equality
     */
    private fun addition():Expr{
        var expr = multiplication()

        while(match(TokenType.PLUS, TokenType.MINUS)){
            val operator = previous()
            val right = multiplication()
            expr = Binary(expr, operator, right)
        }
        return expr
    }

    /**
     * @see equality
     */
    private fun multiplication():Expr{
        var expr = unary()

        while (match(TokenType.SLASH, TokenType.STAR)){
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
    private fun unary():Expr{
        if(match(TokenType.MINUS, TokenType.BANG)){
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
    private fun primary():Expr{
        when{
            match(TokenType.FALSE) -> return Literal(false)
            match(TokenType.TRUE) -> return Literal(true)
            match(TokenType.NIL) -> return Literal(null)

            match(TokenType.STRING, TokenType.NUMBER) -> return Literal(previous().literal)

            match(TokenType.LEFT_PAREN) -> {
                val expr = expression()
                consume(TokenType.RIGHT_PAREN, "Expect closing ')' after expression.")
                return Grouping(expr)
            }
        }
        throw error(peek(),"Expect expression")
    }

    private fun consume(type:TokenType, message:String):Token{
        if(check(type)) return advance()

        throw error(peek(), message)
    }

    private fun error(token: Token, message: String):ParseError{
        err(token,message)
        return ParseError()
    }

    private fun synchronize(){
        advance()

        while(!isAtEnd()){
            if (previous().type == TokenType.SEMI_COL) return

            when(peek().type) {
                TokenType.CLASS, TokenType.VAR, TokenType.FUN, TokenType.IF, TokenType.WHILE,
                TokenType.PRINT, TokenType.RETURN, TokenType.FOR -> return
            }
            advance()
        }
    }



    private fun match(vararg types: TokenType): Boolean {
        for (type in types) {
            if (check(type)) {
                advance()
                return true
            }
        }
        return false
    }

    private fun check(type: TokenType) = if (isAtEnd()) false else peek().type == type

    private fun advance(): Token {
        if (!isAtEnd()) current++
        return previous()
    }

    private fun peek() = tokens[current]

    private fun isAtEnd() = peek().type == TokenType.EOF

    private fun previous() = tokens[current - 1]
}