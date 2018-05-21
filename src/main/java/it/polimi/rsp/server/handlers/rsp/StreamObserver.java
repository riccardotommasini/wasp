package it.polimi.rsp.server.handlers.rsp;

import it.polimi.rsp.server.handlers.WebSocketHandler;
import it.polimi.rsp.server.model.Endpoint;
import it.polimi.rsp.server.model.Item;
import it.polimi.rsp.server.model.Observer;
import it.polimi.rsp.server.model.Proxy;
import lombok.extern.java.Log;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

@Log
@WebSocket
public class StreamObserver extends WebSocketHandler implements Observer {

    public StreamObserver(Proxy proxy) {
        proxy.addObserver(this);
    }

    @Override
    protected void handleMessage(Session user, String message) {
        log.info("Read Only");
    }

    @Override
    public void update(Object observed, Item msg) {
        broadcast(msg);
    }

}
