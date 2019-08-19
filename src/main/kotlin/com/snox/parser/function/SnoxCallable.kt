package com.snox.parser.function

import com.snox.interpreter.Interpreter

interface SnoxCallable {

    fun call(interpreter:Interpreter, arguments:List<Any?>):Any?
    fun arity():Int
}