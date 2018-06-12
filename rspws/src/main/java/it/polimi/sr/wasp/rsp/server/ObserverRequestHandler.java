package it.polimi.sr.wasp.rsp.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.rsp.vocals.core.annotations.Endpoint;
import it.polimi.rsp.vocals.core.annotations.HttpMethod;
import it.polimi.sr.wasp.server.enums.Protocols;
import it.polimi.sr.wasp.server.exceptions.DuplicateException;
import it.polimi.sr.wasp.server.handlers.Answer;
import it.polimi.sr.wasp.server.handlers.RequestHandler;
import it.polimi.sr.wasp.server.model.concept.Channel;
import it.polimi.sr.wasp.server.model.concept.Sink;
import it.polimi.sr.wasp.server.model.persist.Key;
import it.polimi.sr.wasp.server.model.persist.KeyFactory;
import it.polimi.sr.wasp.server.model.persist.StatusManager;
import it.polimi.sr.wasp.server.web.HTTPPostSink;
import it.polimi.sr.wasp.server.web.HttpGetSink;
import it.polimi.sr.wasp.server.web.WebSocketSink;
import it.polimi.sr.wasp.utils.URIUtils;
import lombok.extern.java.Log;
import spark.Request;
import spark.Response;
import spark.Service;
import spark.utils.StringUtils;

import java.util.Locale;
import java.util.Optional;

import static spark.Spark.post;

/*This service is quite difference since as a result it spawns new APIs
*
* - a web socket API that does not accept incoming message and pushed out the results
* - a get API that allows a stream to be pulled out
* - a service that POST a call to an external service sending the data
* - other protocols?
*
*
* */
@Log
public class ObserverRequestHandler implements RequestHandler {

    private static final Gson gson = new Gson();
    private static final int HTTP_BAD_REQUEST = 400;
    private static final Endpoint endpoint = new Endpoint("observers", "/observers/:stream", HttpMethod.POST, "observers", new Endpoint.Par[]{
            new Endpoint.Par("stream", 0, true, String.class),
            new Endpoint.Par("protocol", 1, false, Protocols.class)
    });

    private final String host;
    private final int port;
    private final String base_ws;
    private final String base_query;
    private int wsport;
    private String name;
    private String base_stream;

    public ObserverRequestHandler(String name, String host, int port) {
        this.name = StringUtils.removeLeadingAndTrailingSlashesFrom(name);
        this.host = StringUtils.removeLeadingAndTrailingSlashesFrom(host);
        this.port = port;
        this.wsport = port;
        String s = this.host + URIUtils.COLON + this.port + URIUtils.SLASH + this.name + URIUtils.SLASH;
        base_stream = s + "streams" + URIUtils.SLASH;
        base_query = s + "queries" + URIUtils.SLASH;
        base_ws = URIUtils.SLASH + this.name + URIUtils.SLASH + "streams" + URIUtils.SLASH;
    }

    @Override
    public void call() {
        log.info("Endpoint POST: [" + endpoint.uri + "] Ready");
        post(endpoint.uri, this);
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        final String stream = request.params(endpoint.params[0].name);
        JsonObject jsonObject = gson.fromJson(request.body(), JsonObject.class);
        Protocols protocol = Protocols.valueOf(jsonObject.get(endpoint.params[1].name).getAsString().toUpperCase(Locale.ENGLISH));
        String surl = base_stream + stream;
        String qurl = base_query + stream;

        Key k1 = KeyFactory.get2("http://" + qurl);
        Key k2 = KeyFactory.get("http://" + surl);
        String path = base_ws + stream + "/observers/";


        return StatusManager.getChannel(k1).map(s ->
                getAnswer(stream, protocol, k1, s, path))
                .map(Optional::of)
                .orElse(StatusManager.getChannel(k2)
                        .map(s -> getAnswer(stream, protocol, k2, s, path)))
                .orElse(new Answer(HTTP_BAD_REQUEST, "Observer NOT Successfully Created"));

    }

    private Answer getAnswer(String stream, Protocols protocol, Key k, Channel s, String path) {
        try {
            Sink observer = null;
            String path1 = path + Math.abs(k.hashCode());
            switch (protocol) {
                case EVENTS:
                    observer = getEventsSink(path);
                    break;
                case HTTP:
                    observer = startHttpSink(stream, path1, this.wsport = wsport + 1);
                    break;
                case WEBSOCKET:
                default:
                    observer = startWebSocket(path1, this.wsport = wsport + 1);
                    break;

            }
            s.add(observer);
            StatusManager.commit(KeyFactory.create(k), observer);
            return new Answer(200, s);
        } catch (DuplicateException e) {
            e.printStackTrace();
            return new Answer(409, "Duplicate Resource " + stream);
        }
    }

    private Sink getEventsSink(String path) {
        return new HTTPPostSink(path);
    }

    private Sink startHttpSink(String stream, String path, int port) {
        Service ws = Service.ignite().port(port).threadPool(4);
        final HttpGetSink httpGetSink = new HttpGetSink(stream, path, 3, ws);
        ws.path(this.name, httpGetSink::call);
        return httpGetSink;
    }

    private Sink startWebSocket(String path, int p) throws DuplicateException {
        log.info("New Service Instance opened on port [" + p + "]");
        Service ws = Service.ignite().port(p).threadPool(4);
        WebSocketSink observer = new WebSocketSink(path, ws, "localhost", p, "http://" + host + ":" + port + "/" + name);
        ws.path(name, observer::call);
        return observer;
    }

}

