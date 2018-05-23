package it.polimi.rsp.server.exceptions;

public class ServiceException extends RuntimeException {
    public ServiceException(String message) {
        super(message);
    }

    @Override
    public String toString() {
        return getMessage();
    }
}
