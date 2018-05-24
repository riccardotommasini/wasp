package it.polimi.sr.wasp.rsp.features.streams;


import it.polimi.sr.wasp.rsp.model.InStream;
import it.polimi.sr.wasp.vocals.annotations.features.Feature;
import it.polimi.sr.wasp.vocals.annotations.features.Param;
import it.polimi.sr.wasp.vocals.annotations.features.RSPService;

@Feature(name = "StreamGetterFeature")
public interface StreamGetterFeature {

    @RSPService(endpoint = "/streams")
    InStream get_stream(@Param(name = "stream", uri = true) String id);

}
