package it.polimi.sr.wasp.server.handlers.std;

import it.polimi.rsp.vocals.core.annotations.Endpoint;
import it.polimi.rsp.vocals.core.annotations.HttpMethod;
import it.polimi.sr.wasp.server.handlers.AbstractReflectiveRequestHandler;
import it.polimi.sr.wasp.server.model.persist.KeyFactory;
import it.polimi.sr.wasp.server.model.persist.StatusManager;
import it.polimi.sr.wasp.utils.URIUtils;
import lombok.extern.java.Log;
import org.apache.http.entity.ContentType;
import spark.Request;
import spark.Response;

import static spark.Spark.get;


@Log
public class ObserversHandler extends AbstractReflectiveRequestHandler {

    public ObserversHandler() {
        super(null, new Endpoint("observers", "/observers", HttpMethod.GET, "", new Endpoint.Par[]{new Endpoint.Par("observer", 0, true)}));
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        if (!request.params().containsKey(endpoint.params[0].name))
            return StatusManager.sinks.values();
        else {
            return StatusManager.sinks.get(KeyFactory.create(endpoint.params[0].name));
        }
    }

    @Override
    public void call() {
        log.info("Observers Endpoint GET: [" + endpoint.uri + "] Ready");
        get(endpoint.uri, ContentType.APPLICATION_JSON.getMimeType(), this);
        log.info("Observers Endpoint : [" + URIUtils.addParam(endpoint.uri, endpoint.params[0].name) + "] Ready");
        get(URIUtils.addParam(endpoint.uri, endpoint.params[0].name), ContentType.APPLICATION_JSON.getMimeType(), this);
    }

}
