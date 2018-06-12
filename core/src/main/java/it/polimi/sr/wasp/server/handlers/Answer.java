package it.polimi.sr.wasp.server.handlers;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Answer {

    private final int code;
    private final Object body;

    @Override
    public String toString() {
        return body.toString();
    }
}
