package it.polimi.rsp.server.handlers;

import com.google.gson.Gson;
import it.polimi.rsp.server.model.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Log
@RequiredArgsConstructor
@WebSocket
public abstract class WebSocketHandler {

    protected static final Gson gson = new Gson();
    protected Set<Session> sessions = new HashSet<>();

    @OnWebSocketConnect
    public void onConnect(Session user) throws Exception {
        log.info("New User [" + user + "] connected");
        sessions.add(user);
    }

    @OnWebSocketMessage
    public void onMessage(Session user, String message) {
        handleMessage(user, message);
    }


    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {
        log.info("User [" + user + "] disconnected. Status [" + statusCode + "] Reason: " + reason);
        sessions.remove(user);
    }

    protected abstract void handleMessage(Session user, String message);

    protected void broadcast(Item read) {
        sessions.forEach(session -> {
            try {
                session.getRemote().sendString(gson.toJson(read));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}
