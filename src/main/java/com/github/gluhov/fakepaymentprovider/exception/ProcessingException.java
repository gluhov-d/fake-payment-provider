package com.github.gluhov.fakepaymentprovider.exception;

import lombok.Getter;

public class ProcessingException extends RuntimeException {
    @Getter
    protected String errorCode;

    public ProcessingException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}