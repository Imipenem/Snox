package com.snox.parser.expr

import com.snox.token.Token

/**
 * Auto generated AST for parsing SNOX statements
 */
abstract class Stmt {
    abstract fun <T> accept(visitor: Visitor<T>): T


    interface Visitor<T> {
        fun visitExpressionStmt(stmt: Expression): T
        fun visitPrintStmt(stmt: Print): T
        fun visitVarStmt(stmt:Var):T
    }
}

class Expression(val expression: Expr) : Stmt() {
    override fun <T> accept(visitor: Visitor<T>) = visitor.visitExpressionStmt(this)
}

class Print(val expression: Expr) : Stmt() {
    override fun <T> accept(visitor: Visitor<T>) = visitor.visitPrintStmt(this)
}

class Var(val name: Token, val initializer:Expr?):Stmt(){
    override fun <T> accept(visitor:Visitor<T>) = visitor.visitVarStmt(this)
}

