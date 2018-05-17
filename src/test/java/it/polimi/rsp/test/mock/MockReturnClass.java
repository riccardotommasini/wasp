package it.polimi.rsp.test.mock;

import com.google.gson.Gson;

public class MockReturnClass implements Mockterface {

    public String id;
    public String body;

    public MockReturnClass(String a, String b) {
        id = a;
        body = b;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public String body() {
        return body;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this, MockReturnClass.class);
    }
}
