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



    public Endpoint(String name, String feature, Par[] params) {
        this.name = name;
        this.uri = name;
        this.feature = feature;
        this.method = HttpMethod.GET;
        this.params = params;
    }

    public Endpoint(String name, String feature) {
        this(name, feature, new Endpoint.Par[]{});
    }



    public static class Par {
        public final int index;

        public Par(int index, String name, boolean uri, Class<?> type) {
            this.index = index;
            this.name = name;
            this.uri = uri;
            this.type = type;
        }

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
