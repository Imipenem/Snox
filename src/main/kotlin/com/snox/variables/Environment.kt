package com.snox.variables

import com.snox.error.RuntimeError
import com.snox.token.Token

/**
 * This class represents an environment for variables.
 *
 * Variables (for now only global variables) are represented through a key-value pair.
 * Using a variable, that is probably never declared or used before declared, results in a runtime error.
 */
class Environment(val enclosing: Environment? = null) {

    private val values = HashMap<String, Any?>()

    fun define(name:String, value:Any?){
        values[name] = value
    }

    fun get(name: Token):Any? {
        if(values.containsKey(name.snoxeme)) return values[name.snoxeme]

        if(enclosing != null) return enclosing.get(name)

        throw RuntimeError(name, "Undefined variable ${name.snoxeme}.")
    }

    fun assign(name:Token, value:Any?) {
        if(values.containsKey(name.snoxeme)) {
            values[name.snoxeme] = value
            return
        }

        if(enclosing != null){
            enclosing.assign(name, value)
            return
        }

        throw RuntimeError(name,"Assigned value to undefined variable ${name.snoxeme}.")
    }
}