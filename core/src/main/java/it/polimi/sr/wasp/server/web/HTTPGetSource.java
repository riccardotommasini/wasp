package it.polimi.sr.wasp.server.web;

import it.polimi.sr.wasp.server.model.concept.Channel;
import it.polimi.sr.wasp.server.model.concept.Source;
import lombok.extern.log4j.Log4j2;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

@Log4j2
public class HTTPGetSource implements Source {

    private final long poll;
    private Channel channel;
    private String source;
    private OkHttpClient client = new OkHttpClient();
    private Thread thread;
    private boolean stop = true;

    public HTTPGetSource(Channel source, String uri, long poll) {
        this.channel = source;
        this.source = uri;
        this.poll = poll;
    }

    private void init() throws IOException {
        stop = false;
        this.thread = new Thread(() -> {
            while (!stop) {
                try {

                    Request request = new Request.Builder()
                            .url(this.source)
                            .addHeader("Accept", "application/json")
                            .build();

                    Response response = client.newCall(request).execute();
                    channel.yield(response.body().string());
                    Thread.sleep(this.poll);

                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        this.thread.start();

    }

    @Override
    public Channel add(Channel t) {
        this.channel = t;
        return t;
    }

    @Override
    public void stop() {
        this.stop = true;
    }

    @Override
    public String toString() {
        return "{ \"source\":\"" + source + "\",\"type\":\"WebSocketSource\" }";
    }

}