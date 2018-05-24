package it.polimi.sr.wasp.server.web;

import com.google.gson.Gson;
import it.polimi.sr.wasp.server.handlers.RequestHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import spark.Request;
import spark.Response;
import spark.Service;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Log
@WebSocket
@RequiredArgsConstructor
public class WebSocketSink implements Sink, RequestHandler {

    private static final Gson gson = new Gson();
    private final String path, url;
    private final Service http;
    private final Set<Session> sessions = new HashSet<>();

    @OnWebSocketConnect
    public void onConnect(Session user) throws Exception {
        log.info("New User [" + user + "] connected");
        sessions.add(user);
    }

    @OnWebSocketMessage
    public void onMessage(Session user, String message) throws IOException {
        log.info("Read Only!");
        user.getRemote().sendString("Read Only: Ban!");
        sessions.remove(user);
    }


    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {
        log.info("User [" + user + "] disconnected. StatusManager [" + statusCode + "] Reason: " + reason);
        sessions.remove(user);
    }

    protected void broadcast(Object read) {
        sessions.forEach(session -> {
            try {
                session.getRemote().sendString(gson.toJson(read));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void message(Object arg) {
        broadcast(arg);
    }

    @Override
    public void call() {
        log.info(url);
        http.webSocket(path, this);
        http.init();
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        return null;
    }
}
