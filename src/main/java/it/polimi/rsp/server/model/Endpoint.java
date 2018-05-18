package it.polimi.rsp.server.model;

import it.polimi.rsp.server.HttpMethod;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@RequiredArgsConstructor
public class Endpoint {

    public final String name, uri;
    public final HttpMethod method;
    public final String feature;
    public final Par[] params;

    public static class Par {
        public final int index;
        public final String name;
        public final boolean uri;
        public Class<?> type;

        public Par(String name, int index, boolean uri) {
            this.index = index;
            this.name = name;
            this.uri = uri;
        }
    }
}
