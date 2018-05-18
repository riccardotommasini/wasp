package it.polimi.rsp.server;

public enum HttpMethod {

    GET("GET"), POST("POST"), PUT("PUT"), DELETE("DELETE"), OPTIONS("OPTIONS"), HEAD("HEAD"), PATCH("PATCH");

    private final String method;

    HttpMethod(String get) {
        method = get;
    }
}

