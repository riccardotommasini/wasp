package it.polimi.sr.wasp.server.exceptions;

public class ServiceException extends RuntimeException {

    public ServiceException(Throwable cause) {
        super(cause.getMessage(), cause);
    }

    public ServiceException(String msg) {
        super(msg);
    }


    @Override
    public String toString() {
        return getMessage();
    }
}
