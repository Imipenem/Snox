package com.snox.parser

import com.snox.parser.expr.*
import com.snox.parser.expr.Function
import com.snox.error as err
import com.snox.token.Token
import com.snox.token.TokenType
import java.lang.RuntimeException
import kotlin.collections.ArrayList

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
     * This function decides whether a declaration is a variable declaration/ function statement or another statement.
     */
    private fun declaration():Stmt? {
        try {
            if(match(TokenType.FUN)) return function("function")
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
    private fun statement():Stmt {
        if(match(TokenType.IF)) return ifStatement()
        if(match(TokenType.WHILE)) return whileStatement()
        if(match(TokenType.FOR)) return forStatement()
        if(match(TokenType.PRINT)) return printStatement()
        if(match(TokenType.LEFT_BRACE)) return Block(block())
        if(match(TokenType.RETURN)) return returnStatement()
        return expressionStatement()
    }

    /**
     * This function is called in the case of an if statement with a if then branch and an else branch.
     * The else-branch is optional and thus can be null!
     *
     * Note: Else - statement is bound to the nearest If statement!!!
     */

    private fun ifStatement():Stmt {
        consume(TokenType.LEFT_PAREN, "Expected a '(' before if condition")
        val condition = expression()
        consume(TokenType.RIGHT_PAREN, "Expected enclosing ')' after if condition")

        val thenBranch = statement()
        var elseBranch:Stmt? = null

        if(match(TokenType.ELSE)) elseBranch = statement()

        return If(condition, thenBranch, elseBranch)
    }

    /**
     * This function is called in case of a while statement.
     * It repeatly checks whether the condition is true and then (if true) examines the statement in the while loop´s body
     */

    private fun whileStatement():Stmt {
        consume(TokenType.LEFT_PAREN, "Expected a '(' before while condition")
        val condition = expression()
        consume(TokenType.RIGHT_PAREN, "Expected enclosing ')' after while condition")
        val body = statement()

        return While(condition, body)
    }

    /**
     * This function is called when a for statement is used.
     * Note that each of the three components of a common for loop could be null.
     */
    private fun forStatement():Stmt {
        consume(TokenType.LEFT_PAREN, "Expected a '(' in for loop")

        val initializer = when {
            match(TokenType.SEMI_COL) -> null
            match(TokenType.VAR) -> varDeclaration()
            else -> expressionStatement()
        }

        var condition = if(!check(TokenType.SEMI_COL)) expression()
                        else null
        consume(TokenType.SEMI_COL, "Expected ; after loop condition")

        val increment = if(!check(TokenType.SEMI_COL)) expression()
                        else null
        consume(TokenType.RIGHT_PAREN, "Expected ')' after for loop")

        var body = statement()

        if(increment != null) body = Block(listOf(body, Expression(increment)))
        if(condition == null) condition = Literal(true)
        body = While(condition, body)
        if(initializer != null) body = Block(listOf(initializer,body))


        return body
    }

    /**
     * This function is called when a print statement is called.
     */
    private fun printStatement():Stmt {
        val value = expression()
        consume(TokenType.SEMI_COL, "Expected ; after statement.")

        return Print(value)
    }

    /**
     * This function is called when a return statement has been encountered.
     * Note that the initial value is set to null so in cases where no explicit return value is specified it will return
     * the default value, SNOX´s nil.
     */
    private fun returnStatement():Stmt {
        val keyword = previous()
        var value:Expr? = null

        if(!check(TokenType.SEMI_COL)) value = expression()

        consume(TokenType.SEMI_COL, "Expected ; after return statement.")
        return Return(keyword, value)
    }

    /**
     * This function checks, if an expression always ends with a ; and then parses the expression.
     */
    private fun expressionStatement():Stmt {
        val value = expression()
        consume(TokenType.SEMI_COL, "Expected ; after statement.")

        return Expression(value)
    }

    /**
     * This function is called when we encountered a function in SNOX source code.
     * The parameters are collected one after another and then the functions body a "block" will be examined.
     *
     * Note: Max parameters amount is 255 (like in Java for example)
     */
    private fun function(kind:String):Function {
        val name:Token = consume(TokenType.IDENTIFIER, "Expect $kind name")
        consume(TokenType.LEFT_PAREN, "Expect ( after $kind name")
        val parameters = ArrayList<Token>()

        if(!check(TokenType.RIGHT_PAREN)) {
            do {
                if (parameters.size >= 255) error(peek(),"No more than 255 arguments")
                parameters.add(consume(TokenType.IDENTIFIER, "Expect parameter name"))
            }
            while (match(TokenType.COMMA))
        }
        consume(TokenType.RIGHT_PAREN, "Expected ) after parameters")
        consume(TokenType.LEFT_BRACE, "Expected { before $kind body")
        val body = block()
        return Function(name, parameters, body)
    }

    /**
     * This function is called when we encountered a block, enclosed by {}.
     * It simply collects all statements one after another and stores them into a list and returns those statements.
     */
    private fun block():List<Stmt?> {

        val statements = ArrayList<Stmt?>()

        while(!(check(TokenType.RIGHT_BRACE) && !isAtEnd())) {
            statements.add(declaration())
        }

        consume(TokenType.RIGHT_BRACE, "Expected closing }")
        return statements
    }

    private fun expression() = assignement()

    private fun assignement():Expr {

        val expr = or()

        if(match(TokenType.EQUAL)) {
            val equals = previous()
            val value = assignement()

            if(expr is Variable) {
                val name = expr.name
                return Assign(name, value)
            }

            error(equals, "Invalid assignement!")
        }
        return expr
    }

    private fun or():Expr {
        var expr = and()

        while (match(TokenType.OR)) {
            val operator:Token = previous()
            val right:Expr = and()
            expr = Logical(expr, operator, right)
        }
        return expr
    }

    private fun and():Expr {
        var expr = equality()

        while (match(TokenType.AND)) {
            val operator:Token = previous()
            val right =equality()
            expr = Logical(expr, operator, right)
        }
        return expr
    }

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
        return call()
    }

    private fun call():Expr {

        var expr = primary()

        while (true) {
            if(match(TokenType.LEFT_PAREN)) expr = finishCall(expr)

            else break
        }
        return expr
    }

    private fun finishCall(callee:Expr):Expr {
        val arguments = ArrayList<Expr>()

        if(!check(TokenType.RIGHT_PAREN)) {
            do {
                if(arguments.size > 255) error(peek(), "No more than 255 arguments per function are currently supported!")
                arguments.add(expression())
            }
            while (match(TokenType.COMMA))
        }
        val paren = consume(TokenType.RIGHT_PAREN, "Expected ) on function call!")

        return Call(callee, paren, arguments)
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