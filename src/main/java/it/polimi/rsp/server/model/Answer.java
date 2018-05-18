package it.polimi.rsp.server.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Answer {

    private final int code;
    private final Object body;

    public Answer(int code) {
        this(code, "");
    }
}
