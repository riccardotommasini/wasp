package it.polimi.sr.wasp.rsp.server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
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
    private static final Endpoint endpoint = new Endpoint("observers", "/observers/:stream", HttpMethod.POST, "observers",
            new Endpoint.Par[]{
                    new Endpoint.Par("stream", 0, true, String.class),
                    new Endpoint.Par("protocol", 1, false, Protocols.class),
                    new Endpoint.Par("retention", 2, false, Integer.class)
            });

    private final String host;
    private final int port;
    private final String base_ws;
    private final String base_query;
    private final String base;
    private int wsport;
    private String name;
    private String base_stream;

    public ObserverRequestHandler(String name, String host, int port) {
        this.name = StringUtils.removeLeadingAndTrailingSlashesFrom(name);
        this.host = StringUtils.removeLeadingAndTrailingSlashesFrom(host);
        this.port = port;
        this.wsport = port;
        this.base = this.host + URIUtils.COLON + this.port + URIUtils.SLASH + this.name + URIUtils.SLASH;
        base_stream = this.base + "streams" + URIUtils.SLASH;
        base_query = this.base + "queries" + URIUtils.SLASH;
        base_ws = URIUtils.SLASH + this.name + URIUtils.SLASH + "streams" + URIUtils.SLASH;
    }

    @Override
    public void call() {
        log.info("Endpoint POST: [" + endpoint.uri + "] Ready");
        post(endpoint.uri, this);
    }

    @Override
    public Object handle(Request request, Response response) {
        final String stream = request.params(endpoint.params[0].name);
        JsonObject jsonObject = gson.fromJson(request.body(), JsonObject.class);
        Protocols protocol = Protocols.valueOf(jsonObject.get(endpoint.params[1].name).getAsString().toUpperCase(Locale.ENGLISH));
        JsonElement jsonElement = jsonObject.get(endpoint.params[2].name);
        int retention = jsonElement != null ? jsonElement.getAsInt() : 1;
        String surl = base_stream + stream;
        String qurl = base_query + stream;

        Key k = KeyFactory.get("http://" + surl);
        Key k2 = KeyFactory.get2("http://" + qurl);
        String path = base_ws + stream + "/observers/";


        return StatusManager.getChannel(k2).map(s ->
                getAnswer(stream, protocol, k2, s, path, retention))
                .map(Optional::of)
                .orElse(StatusManager.getChannel(k)
                        .map(s -> getAnswer(stream, protocol, k, s, path, retention)))
                .orElse(new Answer(HTTP_BAD_REQUEST, "Observer NOT Successfully Created"));

    }

    private Answer getAnswer(String stream, Protocols protocol, Key k, Channel channel, String path, int retention) {
        try {
            Sink observer;

            Key key = getKey(k);

            String oid = path + Math.abs(key.hashCode());
            switch (protocol) {
                case EVENTS:
                    observer = getEventsSink(path);
                    break;
                case HTTP:
                    observer = startHttpSink(stream, oid, retention, this.wsport += 1);
                    break;
                case WEBSOCKET:
                default:
                    observer = startWebSocket(oid, this.wsport += 1);
                    break;

            }

            channel.add(observer);

            StatusManager.commit(key, observer);

            return new Answer(200, channel);
        } catch (DuplicateException e) {
            return new Answer(409, "Duplicate Resource " + stream);
        }
    }

    private Key getKey(Key k) throws DuplicateException {
        Key key = KeyFactory.create(k);
        if (StatusManager.exists(key)) {
            return getKey(key);
        }
        return key;
    }

    private Sink getEventsSink(String oid) {
        return new HTTPPostSink(oid);
    }

    private Sink startHttpSink(String stream, String oid, int retention, int port) {
        Service http = Service.ignite().port(port).threadPool(4);
        final HttpGetSink httpGetSink = new HttpGetSink("http://" + this.host + URIUtils.COLON + port, stream, oid, retention, http);
        http.path("", httpGetSink::call);
        return httpGetSink;
    }

    private Sink startWebSocket(String oid, int p) {
        log.info("New Service Instance opened on port [" + p + "]");
        Service ws = Service.ignite().port(p).threadPool(4);
        WebSocketSink observer = new WebSocketSink(oid, ws, host, p, "http://" + host + ":" + port + "/" + name);
        ws.path(name, observer::call);
        return observer;
    }

}

