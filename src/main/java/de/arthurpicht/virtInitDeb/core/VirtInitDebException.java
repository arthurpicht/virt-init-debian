package de.arthurpicht.virtInitDeb.core;

public class VirtInitDebException extends RuntimeException {

    public VirtInitDebException() {
    }

    public VirtInitDebException(String message) {
        super(message);
    }

    public VirtInitDebException(String message, Throwable cause) {
        super(message, cause);
    }

    public VirtInitDebException(Throwable cause) {
        super(cause);
    }

    public VirtInitDebException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
