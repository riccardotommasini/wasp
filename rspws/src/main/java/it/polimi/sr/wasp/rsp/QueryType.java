package it.polimi.sr.wasp.rsp;

public enum QueryType {

    ASK("ASK"), CONSTRUCT("CONSTRUCT"), DESCRIBE("DESCRIBE"), SELECT("SELECT");

    private final String name;

    private QueryType(String name) {
        this.name = name;
    }
}
