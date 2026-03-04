package com.manas.exception;

import org.hibernate.ResourceClosedException;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message){
        super(message);
    }
}
