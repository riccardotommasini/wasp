package it.polimi.sr.wasp.rsp.features.streams;


import it.polimi.rsp.vocals.core.annotations.HttpMethod;
import it.polimi.rsp.vocals.core.annotations.features.Feature;
import it.polimi.rsp.vocals.core.annotations.features.Param;
import it.polimi.rsp.vocals.core.annotations.features.RSPService;
import it.polimi.sr.wasp.server.model.concept.Channel;

@Feature(name = "StreamDeletionFeature")
public interface StreamDeletionFeature {

    @RSPService(endpoint = "/streams", method = HttpMethod.DELETE)
    Channel delete_stream(@Param(name = "stream", uri = true) String id);

}
