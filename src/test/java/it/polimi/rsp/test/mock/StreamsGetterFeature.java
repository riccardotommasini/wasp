package it.polimi.rsp.test.mock;


import it.polimi.rsp.vocals.annotations.features.Feature;
import it.polimi.rsp.vocals.annotations.features.RSPService;

import java.util.List;

@Feature(name = "StreamsGetterFeature")
public interface StreamsGetterFeature {

    @RSPService(endpoint = "/streams")
    List<InStream> get_streams();

}
