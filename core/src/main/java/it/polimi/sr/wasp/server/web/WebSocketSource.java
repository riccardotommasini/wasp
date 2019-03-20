package it.polimi.sr.wasp.server.web;

import it.polimi.sr.wasp.server.model.concept.Channel;
import it.polimi.sr.wasp.server.model.concept.Source;
import lombok.extern.log4j.Log4j2;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.io.IOException;
import java.net.URI;

@Log4j2
@WebSocket
public class WebSocketSource implements Source {

    private Channel channel;
    private URI source;
    private WebSocketClient client = new WebSocketClient();

    public WebSocketSource(Channel source, String uri) {
        this.channel = source;
        this.source = URI.create(uri);
        init();
    }

    private void init() {
        try {
            client.start();
            client.connect(this, this.source);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public WebSocketSource(String uri) {
        this.source = URI.create(uri);
    }

    @OnWebSocketConnect
    public void connected(Session session) {
    }

    @OnWebSocketClose
    public void closed(Session session, int statusCode, String reason) {
    }

    @OnWebSocketMessage
    //TODO this is the method that represents await
    public void message(Session session, String message) throws IOException {
        log.debug(message);
        channel.put(message);
    }

    @Override
    public Channel add(Channel t) {
        if (client.isStopped())
            init();
        this.channel = t;
        return t;
    }

    @Override
    public void stop() {
        try {
            client.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public String toString() {
        return "{ \"source\":\"" + source + "\",\"type\":\"WebSocketSource\" }";
    }
}