package com.snox.parser.expr
import com.snox.token.Token

/**
* Auto generated AST for parsing SNOX expressions (Superclass Expr for clarification only) 
*/
abstract class Expr{
abstract fun <T> accept(visitor: Visitor<T>):T
}

interface Visitor <T> { 
fun visitBinaryExpr(expr:Binary):T
fun visitGroupingExpr(expr:Grouping):T
fun visitLiteralExpr(expr:Literal):T
fun visitUnaryExpr(expr:Unary):T
}

class Binary( left:Expr, operator:Token, right:Expr):Expr(){
override fun <T> accept(visitor:Visitor<T>) = visitor.visitBinaryExpr(this)
}

class Grouping( expression:Expr):Expr(){
override fun <T> accept(visitor:Visitor<T>) = visitor.visitGroupingExpr(this)
}

class Literal( value:Any):Expr(){
override fun <T> accept(visitor:Visitor<T>) = visitor.visitLiteralExpr(this)
}

class Unary( operator:Token, right:Expr):Expr(){
override fun <T> accept(visitor:Visitor<T>) = visitor.visitUnaryExpr(this)
}

