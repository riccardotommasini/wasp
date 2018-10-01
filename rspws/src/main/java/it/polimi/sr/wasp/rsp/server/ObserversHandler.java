package it.polimi.sr.wasp.rsp.server;

import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.utils.JsonUtils;
import it.polimi.rsp.vocals.core.annotations.Endpoint;
import it.polimi.rsp.vocals.core.annotations.HttpMethod;
import it.polimi.sr.wasp.server.handlers.AbstractReflectiveRequestHandler;
import it.polimi.sr.wasp.server.model.concept.Sink;
import it.polimi.sr.wasp.server.model.description.Descriptor;
import it.polimi.sr.wasp.server.model.persist.KeyFactory;
import it.polimi.sr.wasp.server.model.persist.StatusManager;
import it.polimi.sr.wasp.utils.URIUtils;
import lombok.extern.java.Log;
import org.apache.http.entity.ContentType;
import spark.Request;
import spark.Response;

import java.io.IOException;
import java.util.stream.Collectors;

import static spark.Spark.get;


@Log
public class ObserversHandler extends AbstractReflectiveRequestHandler {

    public ObserversHandler() {
        super(null, new Endpoint("observers", "/observers", HttpMethod.GET, "", new Endpoint.Par[]{new Endpoint.Par("observer", 0, true)}));
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        if (!request.params().containsKey(endpoint.params[0].name))
        return StatusManager.sinks.values().stream().map(Sink.class::cast).map(Sink::describe).map(this::getJson).collect(Collectors.toList());
        else{
            return getJson(StatusManager.sinks.get(KeyFactory.create(endpoint.params[0].name)).describe());
        }
    }

    private String getJson(Descriptor descriptor) {
        try {
            return JsonUtils.toPrettyString(JsonLdProcessor.compact(descriptor, descriptor.context(), new JsonLdOptions()));
        } catch (IOException e) {
            e.printStackTrace();
            return "";
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
