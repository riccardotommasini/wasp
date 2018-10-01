package it.polimi.sr.wasp.rsp;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class RSPActor {
    protected final String name;
    protected final String base;

    public String name() {
        return name;
    }

    public String base() {
        return base;
    }
}
