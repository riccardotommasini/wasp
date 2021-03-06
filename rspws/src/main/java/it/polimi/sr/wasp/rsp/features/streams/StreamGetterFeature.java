package it.polimi.sr.wasp.rsp.features.streams;


import it.polimi.rsp.vocals.core.annotations.features.Feature;
import it.polimi.rsp.vocals.core.annotations.features.Param;
import it.polimi.rsp.vocals.core.annotations.features.RSPService;
import it.polimi.sr.wasp.server.model.concept.Channel;

@Feature(name = "StreamGetterFeature")
public interface StreamGetterFeature {

    @RSPService(endpoint = "/streams")
    Channel get_stream(@Param(name = "stream", uri = true) String id);

}
