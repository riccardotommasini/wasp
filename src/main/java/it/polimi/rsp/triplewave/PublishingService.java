package it.polimi.rsp.triplewave;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Log
@RequiredArgsConstructor
@Getter
@WebSocket
public class PublishingService {

    private final int port;
    private final String name;
    private final String base;

    private Map<Session, String> map = new ConcurrentHashMap<>();
    private int clients = 0;

    private String msg1 = "<http://example.org/msg";
    private String msg2 = "> a <http://example.org/Message>";
    private boolean started = false;

    @OnWebSocketConnect
    public void onConnect(Session user) throws Exception {
        map.put(user, "Client" + TripleWave.triplewave.getClients());

        clients++;

        if (!started) {
            log.info("Connecting");
            started = true;
            new Thread(() -> {
                int i = 0;
                while (true) {
                    try {
                        String message = msg1 + i + msg2;
                        log.info(message);
                        broadcastMessage(map.get(user), message);
                        i++;
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }).start();
        }
    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {
    }

    @OnWebSocketMessage
    public void onMessage(Session user, String message) {
        broadcastMessage(map.get(user), message);
    }

    //Sends a message from one user to all users, along with a list of current usernames
    private void broadcastMessage(String sender, String message) {
        map.keySet().stream().filter(Session::isOpen).forEach(session -> {
            try {
                session.getRemote().sendString(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
