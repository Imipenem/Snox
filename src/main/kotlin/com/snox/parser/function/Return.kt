package com.snox.parser.function

/**
 * This class represents a "Return - Eception".
 *
 * This is a desription of a commonly used feature in programming languages. A return statement will immediatley
 * stop currents code execution and breaking the current statement and returning a expression.
 *
 * That´s why it is handeld as an exception in SNOX.
 */

class Return(val value:Any?):RuntimeException(null,null,false,false)