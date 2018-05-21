package it.polimi.rsp.server.model;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Answer {

    private static final Gson gson = new Gson();
    private final int code;
    private final Object body;

    public Answer(int code) {
        this(code, "");
    }

    @Override
    public String toString() {
        return gson.toJson(body);
    }
}
