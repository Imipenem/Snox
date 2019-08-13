package com.snox.tools

import com.snox.parser.expr.*
import com.snox.token.Token
import com.snox.token.TokenType

fun main(){
    val expression = Binary((Unary(Token(TokenType.MINUS, "-", null, 1), Literal(123))),
            Token(TokenType.STAR, "*", null, 1),Grouping(Literal(45.67)))
    // the expression above is the same as -123 * (45.67)
    println(AstPrinter().print(expression))
}

/**
 * This is just a test class for stating where I am and "visualising" the syntax tree (parsing) structure
 */

class AstPrinter : Visitor<String>{
    override fun visitVariableExpr(expr: Variable): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun print(expr:Expr) = expr.accept(this)



    override fun visitBinaryExpr(expr:Binary) = parenthesize(expr.operator.snoxeme, expr.left, expr.right)

    override fun visitGroupingExpr(expr:Grouping) =  parenthesize("group", expr.expression)

    override fun  visitLiteralExpr(expr:Literal):String {
        if (expr.value == null) return "nil"
        return expr.value.toString()
    }

    override fun visitUnaryExpr(expr:Unary) = parenthesize(expr.operator.snoxeme, expr.right)


    private fun parenthesize(name:String, vararg exprs:Expr):String{

        val builder = StringBuilder()

        builder.append("(").append(name)

        for(e in exprs){
            builder.append(" ")
            builder.append(e.accept(this))
        }
        builder.append(")")
        return builder.toString()
    }
}