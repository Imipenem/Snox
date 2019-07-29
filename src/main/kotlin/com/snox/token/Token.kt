package com.snox.token

/**
 * This class represents a token with a type, it´s representation in the source code (called the snoxeme), a (possible)
 * literal and the line, where it occurs (at least useful for error messages).
 *
 * TODO: Calculate an offset for better error reporting functionality!
 */

data class Token(val type: TokenType, val snoxeme:String, val literal:Any?, val line:Int) {

    override fun toString() = "$type + $snoxeme + $literal"
}