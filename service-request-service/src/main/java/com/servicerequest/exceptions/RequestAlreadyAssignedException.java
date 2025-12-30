package com.servicerequest.exceptions;

public class RequestAlreadyAssignedException extends RuntimeException {

    public RequestAlreadyAssignedException(String message) {
        super(message);
    }
}
