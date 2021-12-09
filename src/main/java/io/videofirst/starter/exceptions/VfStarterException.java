package io.videofirst.starter.exceptions;

public class VfStarterException extends RuntimeException {

    public VfStarterException(String message) {
        super(message);
    }

    public VfStarterException(String message, Throwable cause) {
        super(message, cause);
    }

}
