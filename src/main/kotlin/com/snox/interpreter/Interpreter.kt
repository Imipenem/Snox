package com.snox.interpreter

import com.snox.runtimeError
import com.snox.error.RuntimeError
import com.snox.parser.expr.*
import com.snox.token.Token
import com.snox.token.TokenType
import com.snox.variables.Environment

class Interpreter : Visitor<Any?>, Stmt.Visitor<Unit>{

    private val environment = Environment()

    override fun visitAssignExpr(expr: Assign): Any? {
        val value = evaluate(expr.value)
        environment.assign(expr.name, value)
        return value
    }

    override fun visitVariableExpr(expr: Variable) = environment.get(expr.name)

    override fun visitVarStmt(stmt: Var) {
        var value:Any? = null

        if(stmt.initializer != null) value = evaluate(stmt.initializer)
        environment.define(stmt.name.snoxeme, value)
    }

    override fun visitExpressionStmt(stmt: Expression) {
        evaluate(stmt.expression)
    }

    override fun visitPrintStmt(stmt: Print) {
        val value = evaluate(stmt.expression)
        println(stringify(value))
    }

    /**
     * The main function of the Interpreter class.
     *
     * It will execute each statement that has been parsed and has the correct syntax.
     *
     * Otherwise it will throw a RuntimeError
     */
    fun interpret(statements:List<Stmt?>) {
        try {
            for(statement in statements) {
                execute(statement)
            }
        }
        catch (error:RuntimeError){
            runtimeError(error)
        }
    }

    /**
     * This method evaluates binary expressions depending on the operators type and the expressions values.
     *
     * Note that it will throw a RuntimeError if the dynamic type cast fail (thus an operation with unsupported
     * types (values of expressions) is performed)
     */
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
                if(right == 0.0) throw RuntimeError(expr.operator, "Arithmetic Error: Division by zero is not allowed")
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
     * This method evaluates a unary´s expression value after the principle of post order traversal:
     * In terms of the AST: The child nodes are evaluated (the expressions on the right hand) BEFORE their parents.
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

    /**
     * A helper function that actually calls the special accept method of an expression type (and so it´s
     * special visit() defined in the visitor class (HERE: The Interpreter)
     */
    private fun evaluate(expr:Expr):Any? {
       return expr.accept(this)
    }

    private fun execute(stmt:Stmt?){
        stmt?.accept(this)
    }

    /**
     * Determine whether an object is "true" or "false"
     */
    private fun isTruthy(obj:Any?):Boolean{
        if(obj == null) return false
        else if(obj is Boolean) return obj.toString().toBoolean()
        return true
    }

    /**
     * Determine whether two objects are equal.
     *
     * Note that "==" fulfills the IEEE 754 conventions regarding doubles
     */
    private fun isEqual(left:Any?, right:Any?):Boolean{

        if(left == null && right == null) return true

        else if(left == null) return false

        return left == right

    }

    /**
     * This function checks whether the requirement of certain unary expression (that is, it´s operands is a number)
     * is fulfilled
     */
    private fun checkOperandIsNumber(operator:Token, right:Any?){
        if(right is Double) return
        throw RuntimeError(operator,"Expected a Number after operator $operator")
    }

    /**
     * This function checks whether the requirement of certain binary expression (that is, it´s operands are numbers)
     * is fulfilled
     */
    private fun checkBinaryOperandsAreNumbers(operator: Token, left:Any?, right:Any?){
        if(left is Double && right is Double) return
        throw RuntimeError(operator, "Expected numbers with binary operator ${operator.snoxeme}")
    }

    /**
     * Helper function to cast a value of an expression into a string.
     *
     * Note that a .0 will be erased (since all numbers will be internally traded as Doubles)
     * to represent them as Integers
     */
    private fun stringify(value:Any?):String{

        if(value == null) return "nil"

        else if (value.toString().endsWith(".0")){
            val text = value.toString()
            return text.substring(0,text.length - 2)
        }
        return value.toString()
    }
}