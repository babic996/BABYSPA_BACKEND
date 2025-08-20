package com.backend.babyspa.v1.exceptions;

public class BuisnessException extends RuntimeException {
    public BuisnessException() {
        super();
    }

    public BuisnessException(String message) {
        super(message);
    }
}
