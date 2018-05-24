package it.polimi.sr.wasp.server.enums;

public enum Protocols {

    WEBSOCKET("WEBSOCKET"), HTTP("HTTP"), EVENTS("EVENTS"), MTTQ("MTTQ");

    private final String method;

    Protocols(String get) {
        method = get;
    }
}

