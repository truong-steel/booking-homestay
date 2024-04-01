package com.vti.hotelbooking.exception;

public class HomestayNotFoundException extends RuntimeException {
    public HomestayNotFoundException(String message) {
        super(message);
    }
}
