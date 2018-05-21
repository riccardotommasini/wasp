package it.polimi.rsp.server.handlers.rsp;

import com.google.gson.Gson;
import it.polimi.rsp.server.Protocols;
import it.polimi.rsp.server.handlers.RequestHandler;
import it.polimi.rsp.server.model.Answer;
import it.polimi.rsp.server.model.Endpoint;
import it.polimi.rsp.server.model.KeyFactory;
import it.polimi.rsp.server.model.Status;
import it.polimi.rsp.utils.URIUtils;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import spark.Request;
import spark.Response;

import java.util.Optional;

import static spark.Spark.get;
import static spark.Spark.webSocket;

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
@AllArgsConstructor
public class ObserverRequestHandler implements RequestHandler {

    private static final Gson gson = new Gson();
    private static final int HTTP_BAD_REQUEST = 400;

    private static final Endpoint endpoint = new Endpoint("observers", "observers", new Endpoint.Par[]{
            new Endpoint.Par(0, "id", true, String.class),
            new Endpoint.Par(1, "protocol", false, Protocols.class)
    });

    private String name;

    @Override
    public void call() {
        log.info("Endpoint POST: [" + endpoint.uri + "] Ready");
        get(endpoint.uri, this);
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        ObserverRequestBody body = gson.fromJson(request.body(), ObserverRequestBody.class);

        Optional<Answer> answer = Status.getProxy(KeyFactory.create(body.id)).map(proxy -> {
            StreamObserver observer = new StreamObserver(proxy);
            String uri = name + URIUtils.SLASH + body.id;
            switch (body.protocol) {
                case EVENTS:
                case WEBSOCKET:
                    webSocket(uri, observer);
                    break;
                case MTTQ:
                case HTTP:
                default:
                    get(uri, (request1, response1) -> proxy.read());
            }
            return new Answer(200, "Observer Successfully Created at [" + uri + "]");
        });

        return answer.orElse(new Answer(HTTP_BAD_REQUEST, "Observer NOT Successfully Created at "));
    }

    private class ObserverRequestBody {

        public String id;
        public Protocols protocol;

    }
}

