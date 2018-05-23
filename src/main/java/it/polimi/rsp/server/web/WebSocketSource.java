package it.polimi.rsp.server.web;

import it.polimi.rsp.server.model.Stream;
import lombok.extern.java.Log;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.io.IOException;
import java.net.URI;

@Log
@WebSocket
public class WebSocketSource implements Source {

    private Task task;
    private URI uri;
    private WebSocketClient client = new WebSocketClient();

    public WebSocketSource(Stream source) {
        this.uri = URI.create(source.iri());
        try {
            client.start();
            client.connect(this, this.uri);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @OnWebSocketConnect
    public void connected(Session session) {
    }

    @OnWebSocketClose
    public void closed(Session session, int statusCode, String reason) {
    }

    @OnWebSocketMessage
    public void message(Session session, String message) throws IOException {
        log.info(message);
        task.update(message);
    }

    @Override
    public void task(Task t) {
        this.task = t;
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
        return "WebSocketSource";
    }
}