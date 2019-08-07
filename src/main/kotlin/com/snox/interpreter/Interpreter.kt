package com.snox.interpreter

import com.snox.runtimeError
import com.snox.error.RuntimeError
import com.snox.parser.expr.*
import com.snox.token.Token
import com.snox.token.TokenType

class Interpreter : Visitor<Any?>{

    fun interpret(expression:Expr){
        try {
            val value = evaluate(expression)
            println(stringify(value))
        }
        catch (error:RuntimeError){
            runtimeError(error)
        }
    }

    override fun visitBinaryExpr(expr: Binary): Any? {
        val right = evaluate(expr.right)
        val left = evaluate(expr.left)
        when(expr.operator.type){
            TokenType.MINUS -> {
                checkBinaryOperandsAreNumbers(expr.operator, left, right)
                return left as Double - right as Double
            }
            TokenType.SLASH -> {
                checkBinaryOperandsAreNumbers(expr.operator, left, right)
                return left as Double / right as Double
            }
            TokenType.STAR -> {
                checkBinaryOperandsAreNumbers(expr.operator, left, right)
                return  left as Double * right as Double
            }
            TokenType.PLUS -> {
                if((left is Double && right is String) || (left is String && right is Double)) return left.toString() + right
                else if(left is Double && right is Double) return left + right //using kotlin smart casts here
                else if(left is String && right is String) return left + right //using kotlin smart casts here

                throw RuntimeError(expr.operator, "Unsupported type for binary operator ${expr.operator}. " +
                        "Neither String or Double encountered.")
            }
            TokenType.GREATER -> {
                checkBinaryOperandsAreNumbers(expr.operator, left, right)
                return left as Double > right as Double
            }
            TokenType.GREATER_EQ -> {
                checkBinaryOperandsAreNumbers(expr.operator, left, right)
                return left as Double >= right as Double
            }
            TokenType.SMALLER -> {
                checkBinaryOperandsAreNumbers(expr.operator, left, right)
                return (left as Double) < (right as Double)
            }
            TokenType.SMALLER_EQ -> {
                checkBinaryOperandsAreNumbers(expr.operator, left, right)
                return left as Double <= right as Double
            }
            TokenType.BANG_EQ -> return !isEqual(left,right)
            TokenType.EQUAL_EQUAL -> return isEqual(left,right)
        }
        return null
    }

    /**
     * The value of a parenthesized expression is the (recursively) evaluated value of its expression that it surrounds
     */
    override fun visitGroupingExpr(expr: Grouping) = evaluate(expr.expression)

    /**
     * The value of a literal is (well) the literal itself (its value)
     */
    override fun visitLiteralExpr(expr: Literal) = expr.value

    /**
     * Post-order traversal
     */
    override fun visitUnaryExpr(expr: Unary): Any? {

        val right:Any? = evaluate(expr.right) //evaluate the right expressions value (recursively)

        when(expr.operator.type) {
            TokenType.MINUS -> {
                checkOperandIsNumber(expr.operator,right)
                return -(right as Double)
            }
            TokenType.BANG -> return !isTruthy(right)
        }
        return null
    }

    private fun evaluate(expr:Expr):Any? {
       return expr.accept(this)
    }

    private fun isTruthy(obj:Any?):Boolean{
        if(obj == null) return false
        else if(obj is Boolean) return obj.toString().toBoolean()
        return true
    }

    private fun isEqual(left:Any?, right:Any?):Boolean{

        if(left == null && right == null) return true

        else if(left == null) return false

        return left == right

    }

    private fun checkOperandIsNumber(operator:Token, right:Any?){
        if(right is Double) return
        throw RuntimeError(operator,"Expected a Number after operator $operator")
    }

    private fun checkBinaryOperandsAreNumbers(operator: Token, left:Any?, right:Any?){
        if(left is Double && right is Double) return
        throw RuntimeError(operator, "Expected numbers with binary operator ${operator.snoxeme}")
    }

    private fun stringify(value:Any?):String{

        if(value == null) return "nil"

        else if (value.toString().endsWith(".0")){
            val text = value.toString()
            return text.substring(0,text.length - 2)
        }
        return value.toString()
    }

}
