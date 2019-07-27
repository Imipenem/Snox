package com.snox

/**
 * This enum class is used to specify the various types of tokens in the SNOX programming language (the very few basic ones).
 *
 * Those will be needed to specify a tokens type and thus make it possible for the parser to process the token.
 */
enum class TokenType {

    /* Single character tokens like parenthesis or square brackets*/
    LEFT_PAREN, RIGHT_PAREN, DOT, COMMA, SEMI_COL, LEFT_BRACE, RIGHT_BRACE, SLASH, PLUS, STAR, MINUS,

    /* Single or two character tokens (mainly for comparison)*/
    EQUAL, EQUAL_EQUAL, GREATER, SMALLER, GREATER_EQ, SMALLER_EQ, BANG, BANG_EQ,

    /* Literals */
    IDENTIFIER, STRING, NUMBER,

    /*Keywords*/
    AND, OR, NIL, WHILE, IF, ELSE, FOR, VAR, FUN, CLASS, TRUE, FALSE, THIS, SUPER, RETURN, PRINT,

    EOF
}