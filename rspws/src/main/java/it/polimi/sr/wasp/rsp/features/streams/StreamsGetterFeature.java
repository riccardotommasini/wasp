package it.polimi.sr.wasp.rsp.features.streams;


import it.polimi.rsp.vocals.core.annotations.features.Feature;
import it.polimi.rsp.vocals.core.annotations.features.RSPService;

import java.util.List;

@Feature(name = "StreamsGetterFeature")
public interface StreamsGetterFeature {

    @RSPService(endpoint = "/streams")
    List<String> get_streams();

}
