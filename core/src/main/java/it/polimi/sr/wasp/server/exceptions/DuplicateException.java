package it.polimi.sr.wasp.server.exceptions;

public class DuplicateException extends Exception {
    public DuplicateException(String id) {
        super("Duplicate Resource: " + id
        );
    }
}