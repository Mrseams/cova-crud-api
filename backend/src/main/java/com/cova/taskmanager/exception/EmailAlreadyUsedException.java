package com.cova.taskmanager.exception;

public class EmailAlreadyUsedException extends RuntimeException {

    public EmailAlreadyUsedException(String email) {
        super("an account already exists with email " + email);
    }
}
