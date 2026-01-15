package com.gbsw.board.exceptions;

public class AuthorizationFailureException extends RuntimeException {
    public AuthorizationFailureException(String message) {
        super(message);
    }
}
