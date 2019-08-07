package com.snox.interpreter

import com.snox.parser.expr.*
import com.snox.token.TokenType

class Interpreter : Visitor<Any?>{
    override fun visitBinaryExpr(expr: Binary): Any? {
        val right = evaluate(expr.right)
        val left = evaluate(expr.left)
        when(expr.operator.type){
            TokenType.MINUS -> return left as Double - right as Double
            TokenType.SLASH -> return left as Double / right as Double
            TokenType.STAR -> return  left as Double * right as Double
            TokenType.PLUS -> {
                if(left is Double && right is Double) return left + right //using kotlin smart casts here
                else if(left is String && right is String) return left + right //using kotlin smart casts here
            }
            TokenType.GREATER -> return left as Double > right as Double
            TokenType.GREATER_EQ -> return left as Double >= right as Double
            TokenType.SMALLER -> return left.toString().toDouble() < right.toString().toDouble()
            TokenType.SMALLER_EQ -> return left as Double <= right as Double
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
            TokenType.MINUS -> return -right.toString().toDouble() //is there any other way???
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

        return left.equals(right)

    }

}
