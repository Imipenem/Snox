package com.snox.variables

import com.snox.error.RuntimeError
import com.snox.token.Token

/**
 * This class represents an environment for variables.
 *
 * Variables (for now only global variables) are represented through a key-value pair.
 * Using a variable, that is probably never declared or used before declared, results in a runtime error.
 */
class Environment {
    private val values = HashMap<String, Any?>()

    fun define(name:String, value:Any?){
        values[name] = value
    }

    fun get(name: Token):Any? {
        if(values.containsKey(name.snoxeme)) return values[name.snoxeme]

        throw RuntimeError(name, "Undefined variable ${name.snoxeme}.")
    }
}