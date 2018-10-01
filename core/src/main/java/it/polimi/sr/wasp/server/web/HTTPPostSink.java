package it.polimi.sr.wasp.server.web;

import it.polimi.sr.wasp.VOCABS;
import it.polimi.sr.wasp.server.model.concept.Sink;
import it.polimi.sr.wasp.server.model.description.Descriptor;
import it.polimi.sr.wasp.server.model.description.DescriptorHashMap;
import lombok.extern.log4j.Log4j2;
import okhttp3.*;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@Log4j2
public class HTTPPostSink implements Sink {

    private String callback;
    private OkHttpClient client = new OkHttpClient();

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public HTTPPostSink(String callback) {
        this.callback = callback;
    }

    private Response response(String message) throws IOException, InterruptedException {
        RequestBody body = RequestBody.create(JSON, message);
        Request request = new Request.Builder()
                .url(this.callback)
                .post(body)
                .addHeader("content-type", "application/json")
                .build();

        return client.newCall(request).execute();
    }

    public String toString() {
        return "{ \"response\":\"" + callback + "\",\"type\":\"WebSocketSource\" }";
    }

    @Override
    public Descriptor describe() {
        LinkedHashMap<String, Object> context = new LinkedHashMap<>();
        context.put(VOCABS.DCAT.prefix, VOCABS.DCAT.uri);
        context.put(VOCABS.VOCALS.prefix, VOCABS.VOCALS.uri);

        return new DescriptorHashMap() {
            {
                put("@type", "vocals:StreamEndpoint");
                put("dcat:accessURL", "http://");
                //TODO
            }

            @Override
            public Map<String, Object> context() {

                return context;
            }
        };
    }


    @Override
    public void await(String m) {
        try {
            response(m).message();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}