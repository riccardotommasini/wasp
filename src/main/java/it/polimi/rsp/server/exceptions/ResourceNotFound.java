package it.polimi.rsp.server.exceptions;

public class ResourceNotFound extends Throwable {
    public ResourceNotFound(String id) {
        super("Not Found Resource with id [" + id + "]");
    }

}
