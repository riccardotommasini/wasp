package it.polimi.sr.wasp.server.handlers.std;

import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import static spark.Spark.*;

@WebSocket
public class StreamObserverTest {

    private static final Queue<Session> sessions = new ConcurrentLinkedQueue<>();
    boolean running = false;

    @OnWebSocketConnect
    public void connected(Session session) {
        sessions.add(session);
        List<RemoteEndpoint> endpoints =
                sessions.stream().map(Session::getRemote).collect(Collectors.toList());
        if (!running)
            new Thread(() -> {
                while (true) {
                    endpoints.forEach(e -> {
                        try {
                            e.sendString("Ciao Mamma!");

                        } catch (Exception ex) {
                        }
                    });
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
    }

    @OnWebSocketClose
    public void closed(Session session, int statusCode, String reason) {
        sessions.remove(session);
    }

    @OnWebSocketMessage
    public void message(Session session, String message) throws IOException {
        System.out.println("Got: " + message);   // Print message
        session.getRemote().sendString(message); // and send it back
    }

    public static void main(String[] args) {
        port(4000);
        webSocket("/echo", StreamObserverTest.class);
        init();
    }
}