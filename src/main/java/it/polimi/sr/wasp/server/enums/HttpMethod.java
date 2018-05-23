package it.polimi.sr.wasp.server.enums;

public enum HttpMethod {

    GET("GET"), POST("POST"), PUT("PUT"), DELETE("DELETE"), OPTIONS("OPTIONS"), HEAD("HEAD"), PATCH("PATCH");

    private final String method;

    HttpMethod(String get) {
        method = get;
    }
}

