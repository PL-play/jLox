package com.craftinginterpreters.lox;

public enum TokenType {
    // Single-character tokens.
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
    COMMA, DOT, MINUS, PLUS, SEMICOLON, SLASH, STAR,

    // One or two character tokens.
    BANG, BANG_EQUAL,
    EQUAL, EQUAL_EQUAL,
    GRATER, GRATER_EQUAL,
    LESS, LESS_EQUAL, COLON,

    //Literals.
    IDENTIFIER, STRING, NUMBER,

    //Keywords.
    AND, BREAK, CONTINUE, CLASS, ELSE, FALSE, FUN, FOR, IF, NIL, OR,
    PRINT, RETURN, SUPER, THIS, TRUE, VAR, WHILE, LAMBDA,

    EOF

}
