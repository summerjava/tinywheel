package com.summer.dsconnpool.exception;

public class ConnPoolException extends RuntimeException {

    public ConnPoolException() {
    }

    public ConnPoolException(String message) {
        super(message);
    }

    public ConnPoolException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConnPoolException(Throwable cause) {
        super(cause);
    }

    public ConnPoolException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
