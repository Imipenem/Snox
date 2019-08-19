package com.snox.parser.function

import com.snox.interpreter.Interpreter
import com.snox.parser.expr.Function
import com.snox.variables.Environment


class SnoxFunction(val declaration:Function):SnoxCallable {
    override fun call(interpreter: Interpreter, arguments: List<Any?>): Any? {
        val environment = Environment(interpreter.globals)

        for(i in 0 until declaration.params.size) {
            environment.define(declaration.params[i].snoxeme, arguments[i])
        }
        interpreter.executeBlock(declaration.body, environment)
        return null
    }

    override fun arity() = declaration.params.size
    override fun toString() = "<fn ${declaration.name.snoxeme}>"
}