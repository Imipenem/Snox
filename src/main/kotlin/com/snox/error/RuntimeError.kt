package com.snox.error

import com.snox.token.Token
import java.lang.RuntimeException

class RuntimeError(val token: Token, message: String) : RuntimeException(message)