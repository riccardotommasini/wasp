package it.polimi.sr.wasp.rsp.exceptions;

public class InternalEngineException extends Exception {
    public InternalEngineException() {
    }

    public InternalEngineException(String message) {
        super(message);
    }

    public InternalEngineException(String message, Throwable cause) {
        super(message, cause);
    }

    public InternalEngineException(Throwable cause) {
        super(cause);
    }

    public InternalEngineException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
