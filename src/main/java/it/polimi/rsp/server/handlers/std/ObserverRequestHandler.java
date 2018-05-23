package it.polimi.rsp.server.handlers.std;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.rsp.server.web.FlushStreamTask;
import it.polimi.rsp.server.enums.HttpMethod;
import it.polimi.rsp.server.enums.Protocols;
import it.polimi.rsp.server.handlers.RequestHandler;
import it.polimi.rsp.server.model.Answer;
import it.polimi.rsp.server.model.Endpoint;
import it.polimi.rsp.server.model.KeyFactory;
import it.polimi.rsp.server.model.StatusManager;
import it.polimi.rsp.server.web.Task;
import it.polimi.rsp.server.web.WebSocketSink;
import it.polimi.rsp.utils.URIUtils;
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
            new Endpoint.Par(0, "stream", true, String.class),
            new Endpoint.Par(1, "protocol", false, Protocols.class)
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
        Optional<Answer> answer = StatusManager.getStream(KeyFactory.create(stream)).map(s -> {
            String path = URIUtils.SLASH + name + URIUtils.SLASH + "streams" + URIUtils.SLASH + stream;
            String url = "ws://" + host + URIUtils.COLON + port + path;

            WebSocketSink observer = new WebSocketSink(path, url, http);

            Task t = new FlushStreamTask(s);
            t.sink(observer);
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
        });

        return answer.orElse(new Answer(HTTP_BAD_REQUEST, "Observer NOT Successfully Created"));

    }

    private class ObserverModel {

        public String stream;
        public Protocols protocol; //TODO this could be a resource to dereference

    }
}

