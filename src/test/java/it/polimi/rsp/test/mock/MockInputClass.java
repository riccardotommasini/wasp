package it.polimi.rsp.test.mock;

import com.google.gson.Gson;

public class MockInputClass implements Mockterface {

    public String id;
    public String body;

    public MockInputClass(String a, String b) {
        id = a;
        body = b;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this, MockInputClass.class);
    }

}
