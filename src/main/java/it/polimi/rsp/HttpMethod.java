package it.polimi.rsp;

public enum HttpMethod {

    GET("GET"), POST("POST"), PUT("PUT"), DELETE("DELETE"), HEAD("HEAD"), PATCH("PATCH");

    private final String method;

    HttpMethod(String get) {
        method = get;
    }
}

