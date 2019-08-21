package com.snox.parser.expr

import com.snox.token.Token

/**
 * Auto generated AST for parsing SNOX statements
 */
abstract class Stmt {
    abstract fun <T> accept(visitor: Visitor<T>): T


    interface Visitor<T> {
        fun visitBlockStmt(stmt: Block): T
        fun visitExpressionStmt(stmt: Expression): T
        fun visitPrintStmt(stmt: Print): T
        fun visitVarStmt(stmt:Var):T
        fun visitFunctionStmt(stmt:Function):T
        fun visitIfStmt(stmt:If):T
        fun visitWhileStmt(stmt:While):T
        fun visitReturnStmt(stmt:Return):T
    }
}

class Block(val statements: List<Stmt?>) : Stmt() {
    override fun <T> accept(visitor: Visitor<T>) = visitor.visitBlockStmt(this)
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

class Function( val name:Token, val params: List<Token>, val body:List<Stmt?>):Stmt(){
    override fun <T> accept(visitor:Visitor<T>) = visitor.visitFunctionStmt(this)
}

class If( val condition:Expr, val thenBranch:Stmt, val elseBranch:Stmt?):Stmt(){
    override fun <T> accept(visitor:Visitor<T>) = visitor.visitIfStmt(this)
}

class While(val condition: Expr, val body:Stmt):Stmt(){
    override fun <T> accept(visitor:Visitor<T>) = visitor.visitWhileStmt(this)
}
class Return( val keyword:Token, val value:Expr?):Stmt(){
    override fun <T> accept(visitor:Visitor<T>) = visitor.visitReturnStmt(this)
}

