package com.snox.parser.expr
import com.snox.token.Token

/**
* Auto generated AST for parsing SNOX expressions (Superclass Expr for clarification only) 
*/
abstract class Expr{
abstract fun <T> accept(visitor: Visitor<T>):T
}

interface Visitor <T> { 
fun visitAssignExpr(expr:Assign):T
fun visitBinaryExpr(expr:Binary):T
fun visitGroupingExpr(expr:Grouping):T
fun visitLiteralExpr(expr:Literal):T
fun visitLogicalExpr(expr:Logical):T
fun visitUnaryExpr(expr:Unary):T
fun visitVariableExpr(expr:Variable):T
}

class Assign( val name:Token, val value:Expr):Expr(){
override fun <T> accept(visitor:Visitor<T>) = visitor.visitAssignExpr(this)
}

class Binary( val left:Expr, val operator:Token, val right:Expr):Expr(){
override fun <T> accept(visitor:Visitor<T>) = visitor.visitBinaryExpr(this)
}

class Grouping( val expression:Expr):Expr(){
override fun <T> accept(visitor:Visitor<T>) = visitor.visitGroupingExpr(this)
}

class Literal( val value:Any?):Expr(){
override fun <T> accept(visitor:Visitor<T>) = visitor.visitLiteralExpr(this)
}

class Logical( val left:Expr, val operator:Token, val right:Expr):Expr(){
override fun <T> accept(visitor:Visitor<T>) = visitor.visitLogicalExpr(this)
}

class Unary( val operator:Token, val right:Expr):Expr(){
override fun <T> accept(visitor:Visitor<T>) = visitor.visitUnaryExpr(this)
}

class Variable( val name:Token):Expr(){
override fun <T> accept(visitor:Visitor<T>) = visitor.visitVariableExpr(this)
}

