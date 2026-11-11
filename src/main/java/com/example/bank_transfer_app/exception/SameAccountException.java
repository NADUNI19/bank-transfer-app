package com.example.bank_transfer_app.exception;

public class SameAccountException extends RuntimeException {

    public SameAccountException(String message) {
        super(message);
    }
}