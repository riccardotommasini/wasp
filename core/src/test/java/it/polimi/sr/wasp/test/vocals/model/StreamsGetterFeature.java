package it.polimi.sr.wasp.test.vocals.model;


import it.polimi.rsp.vocals.core.annotations.features.Feature;
import it.polimi.rsp.vocals.core.annotations.features.RSPService;
import it.polimi.sr.wasp.server.model.concept.Channel;

import java.util.List;

@Feature(name = "StreamsGetterFeature")
public interface StreamsGetterFeature {

    @RSPService(endpoint = "/streams")
    List<Channel> get_streams();

}
