package it.polimi.rsp.test.mock.features;


import it.polimi.rsp.server.enums.HttpMethod;
import it.polimi.rsp.server.model.Stream;
import it.polimi.rsp.vocals.annotations.features.Feature;
import it.polimi.rsp.vocals.annotations.features.Param;
import it.polimi.rsp.vocals.annotations.features.RSPService;

@Feature(name = "StreamDeletionFeature")
public interface StreamDeletionFeature {

    @RSPService(endpoint = "/streams", method = HttpMethod.DELETE)
    Stream delete_stream(@Param(name = "stream", uri = true) String id);

}
