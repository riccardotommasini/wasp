package it.polimi.rsp.server.exceptions;

public class ServiceException extends RuntimeException {

    public ServiceException(Throwable cause) {
        super(cause.getMessage(), cause);
    }

    @Override
    public String toString() {
        return getMessage();
    }
}
