package it.polimi.sr.wasp.server.web;

import it.polimi.sr.wasp.server.model.concept.*;
import lombok.extern.log4j.Log4j2;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.io.IOException;

@Log4j2
public class HTTPPostSink implements Sink {

    private String callback;
    private OkHttpClient client = new OkHttpClient();

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public HTTPPostSink(String callback) {
        this.callback = callback;
    }

    private void callback(String message) throws IOException, InterruptedException {
        RequestBody body = RequestBody.create(JSON, message);
        Request request = new Request.Builder()
                .url(this.callback)
                .post(body)
                .addHeader("content-type", "application/json")
                .build();

        client.newCall(request).execute();
    }

    public String toString() {
        return "{ \"callback\":\"" + callback + "\",\"type\":\"WebSocketSource\" }";
    }

    @Override
    public void await(Source s, String m) {
        try {
            callback(m);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void await(Channel c, String m) {
        try {
            callback(m);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Descriptor describe() {
        return new DescriptorHashMap() {
            {
                put("@type", "vocals:StreamEndpoint");
                put("dcat:accessURL", "http://");
                //TODO
            }
        };
    }


}