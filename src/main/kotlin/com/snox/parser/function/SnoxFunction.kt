package com.snox.parser.function

import com.snox.interpreter.Interpreter
import com.snox.parser.expr.Function
import com.snox.variables.Environment

/**
 * This class represents SNOX functions.
 *
 * For each parameter, it defines this one in the corresponding environment.
 * //MORE
 */

class SnoxFunction(val declaration:Function):SnoxCallable {
    override fun call(interpreter: Interpreter, arguments: List<Any?>): Any? {
        val environment = Environment(interpreter.globals)

        for(i in 0 until declaration.params.size) {
            environment.define(declaration.params[i].snoxeme, arguments[i])
        }
        try {
            interpreter.executeBlock(declaration.body, environment)
        }
        catch (returnvalue:Return) {
            return returnvalue.value
        }
        return null
    }

    override fun arity() = declaration.params.size
    override fun toString() = "<fn ${declaration.name.snoxeme}>"
}