package it.polimi.rsp.server.exceptions;

public class ResourceNotFound extends Throwable {
    public ResourceNotFound(String message) {
        super(message);
    }

    @Override
    public String toString() {
        return "{}";
    }
}
