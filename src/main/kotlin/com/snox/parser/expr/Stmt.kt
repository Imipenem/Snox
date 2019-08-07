package com.snox.parser.expr

/**
* Auto generated AST for parsing SNOX statements
*/
abstract class Stmt {
    abstract fun <T> accept(visitor: Visitor<T>): T


    interface Visitor<T> {
        fun visitExpressionStmt(stmt: Expression): T
        fun visitPrintStmt(stmt: Print): T
    }
}

    class Expression(val expression: Expr) : Stmt() {
        override fun <T> accept(visitor: Visitor<T>) = visitor.visitExpressionStmt(this)
    }

    class Print(val expression: Expr) : Stmt() {
        override fun <T> accept(visitor: Visitor<T>) = visitor.visitPrintStmt(this)
    }


