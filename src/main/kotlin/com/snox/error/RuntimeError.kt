package com.snox.error

import com.snox.token.Token
import java.lang.RuntimeException

/**
 * Objects of this class represent a RuntimeError in SNOX.
 * They´re mainly produced by failed dynamic cast types.
 */

class RuntimeError(val token: Token, message: String) : RuntimeException(message)