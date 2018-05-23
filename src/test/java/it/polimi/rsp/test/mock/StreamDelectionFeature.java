package it.polimi.rsp.test.mock;


import it.polimi.rsp.server.HttpMethod;
import it.polimi.rsp.server.model.Stream;
import it.polimi.rsp.vocals.annotations.features.Feature;
import it.polimi.rsp.vocals.annotations.features.Param;
import it.polimi.rsp.vocals.annotations.features.RSPService;

@Feature(name = "StreamDelectionFeature")
public interface StreamDelectionFeature {

    @RSPService(endpoint = "/streams", method = HttpMethod.DELETE)
    Stream delete_stream(@Param(name = "stream", uri = true) String id);

}
