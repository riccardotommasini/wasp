package it.polimi.sr.wasp.test.vocals.model;


import it.polimi.sr.wasp.server.model.Stream;
import it.polimi.sr.wasp.vocals.annotations.features.Feature;
import it.polimi.sr.wasp.vocals.annotations.features.RSPService;

import java.util.List;

@Feature(name = "StreamsGetterFeature")
public interface StreamsGetterFeature {

    @RSPService(endpoint = "/streams")
    List<Stream> get_streams();

}
