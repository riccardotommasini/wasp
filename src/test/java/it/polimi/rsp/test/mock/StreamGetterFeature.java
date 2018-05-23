package it.polimi.rsp.test.mock;


import it.polimi.rsp.vocals.annotations.features.Feature;
import it.polimi.rsp.vocals.annotations.features.Param;
import it.polimi.rsp.vocals.annotations.features.RSPService;

@Feature(name = "StreamGetterFeature")
public interface StreamGetterFeature {

    @RSPService(endpoint = "/streams")
    InStream get_streams(@Param(name = "stream", uri = true) String id);

}
