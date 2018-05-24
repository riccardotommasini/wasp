package it.polimi.sr.wasp.server.handlers.std;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.rsp.vocals.core.annotations.Endpoint;
import it.polimi.rsp.vocals.core.annotations.HttpMethod;
import it.polimi.sr.wasp.server.enums.Protocols;
import it.polimi.sr.wasp.server.exceptions.DuplicateException;
import it.polimi.sr.wasp.server.handlers.RequestHandler;
import it.polimi.sr.wasp.server.model.*;
import it.polimi.sr.wasp.server.web.FlushStreamTask;
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
    private final Service http;
    private static final Endpoint endpoint = new Endpoint("observers", "/observers/:stream", HttpMethod.POST, "observers", new Endpoint.Par[]{
            new Endpoint.Par("stream", 0, true, String.class),
            new Endpoint.Par("protocol", 1, false, Protocols.class)
    });

    private final String host;
    private final int port;
    private String name;

    public ObserverRequestHandler(String name, String host, int port) {
        this.name = StringUtils.removeLeadingAndTrailingSlashesFrom(name);
        this.host = StringUtils.removeLeadingAndTrailingSlashesFrom(host);
        this.port = port;
        this.http = Service.ignite().port(port).threadPool(10);
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
        Key k = KeyFactory.create(stream);
        Optional<Answer> answer = StatusManager.getStream(k).map(s -> {
            try {
                String path = URIUtils.SLASH + name + URIUtils.SLASH + "streams" + URIUtils.SLASH + stream;
                String url = "ws://" + host + URIUtils.COLON + port + path;
                WebSocketSink observer = new WebSocketSink(path, url, http);
                FlushStreamTask t = new FlushStreamTask(s, observer);
                //TODO there is any task associated with this stream?
                Key key = KeyFactory.create(k);
                StatusManager.commit(key, t);
                switch (protocol) {
                    case EVENTS:
                    case WEBSOCKET:
                        http.path(name, observer::call);
                        break;
                    case MTTQ:
                    case HTTP:
                    default:
                }
                return new Answer(200, "Observer Successfully Created at [ " + url + "]");
            } catch (DuplicateException e) {
                e.printStackTrace();
                return new Answer(409, "Duplicate Resource " + k);

            }


        });

        return answer.orElse(new Answer(HTTP_BAD_REQUEST, "Observer NOT Successfully Created"));

    }

    private class ObserverModel {

        public String stream;
        public Protocols protocol; //TODO this could be a resource to dereference

    }
}

