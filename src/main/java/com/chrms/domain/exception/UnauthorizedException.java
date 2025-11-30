package com.chrms.domain.exception;

public class UnauthorizedException extends DomainException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
