package com.snox.parser.expr

import com.snox.token.Token

abstract class Expr

class Binary( left:Expr, operator:Token, right:Expr):Expr()

class Grouping( expression:Expr):Expr()

class Literal( value:Any):Expr()

class Unary( operator:Token, right:Expr):Expr()

