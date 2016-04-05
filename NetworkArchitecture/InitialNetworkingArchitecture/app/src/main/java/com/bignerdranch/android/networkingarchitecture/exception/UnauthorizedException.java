package com.bignerdranch.android.networkingarchitecture.exception;

public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(RuntimeException cause) {
        super(cause);
    }
}
