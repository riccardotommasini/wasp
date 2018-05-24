package it.polimi.sr.wasp.server.exceptions;

public class ResourceNotFound extends Throwable {
    public ResourceNotFound(String id) {
        super("Not Found Resource with id [" + id + "]");
    }

}
