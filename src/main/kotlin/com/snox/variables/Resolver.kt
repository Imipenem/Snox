package com.snox.variables

import com.snox.interpreter.Interpreter
import com.snox.parser.expr.*
import com.snox.parser.expr.Function
import com.snox.token.Token
import java.util.*
import kotlin.collections.HashMap

class Resolver (val interpreter:Interpreter, val scopes:Stack<MutableMap<String,Boolean>> = Stack()): Stmt.Visitor<Unit>, Visitor<Unit> {

    fun resolve(statements:List<Stmt?>) {
        for (statement in statements) {
            resolve(statement)
        }
    }

    private fun resolve(stmt:Stmt?) {
        stmt?.accept(this)
    }

    private fun resolve(expr:Expr?) {
        expr?.accept(this)
    }

    private fun beginScope() {
        scopes.push(HashMap())
    }

    private fun endScope() {
        scopes.pop()
    }

    private fun declare(name:Token) {
        if (scopes.isEmpty()) return

        val scope = scopes.peek()
        scope[name.snoxeme] = false
    }

    private fun define(name:Token) {
        if(scopes.isEmpty()) return
        scopes.peek()[name.snoxeme] = true
    }

    override fun visitBlockStmt(stmt: Block) {
        beginScope()
        resolve(stmt.statements)
        endScope()
    }

    override fun visitExpressionStmt(stmt: Expression) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitPrintStmt(stmt: Print) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitVarStmt(stmt: Var) {
        declare(stmt.name)
        if(stmt.initializer != null) resolve(stmt.initializer)
        define(stmt.name)
    }

    override fun visitFunctionStmt(stmt: Function) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitIfStmt(stmt: If) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitWhileStmt(stmt: While) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitReturnStmt(stmt: Return) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitAssignExpr(expr: Assign) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitBinaryExpr(expr: Binary) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitCallExpr(expr: Call) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitGroupingExpr(expr: Grouping) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitLiteralExpr(expr: Literal) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitLogicalExpr(expr: Logical) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitUnaryExpr(expr: Unary) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitVariableExpr(expr: Variable) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}