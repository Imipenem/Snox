package com.snox.variables

import com.snox.error.RuntimeError
import com.snox.token.Token

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