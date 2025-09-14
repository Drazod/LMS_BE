package com.curcus.lms.exception;

public class InactivatedUserException extends RuntimeException {
    public InactivatedUserException(String message) {
        super(message);
    }
}
