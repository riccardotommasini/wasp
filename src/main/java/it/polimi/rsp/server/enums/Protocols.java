package it.polimi.rsp.server.enums;

public enum Protocols {

    WEBSOCKET("WEBSOCKET"), HTTP("HTTP"), EVENTS("EVENTS"), MTTQ("MTTQ");

    private final String method;

    Protocols(String get) {
        method = get;
    }
}

