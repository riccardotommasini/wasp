package it.polimi.sr.wasp.test.mock.features.streams;


import it.polimi.sr.wasp.test.mock.model.InStream;
import it.polimi.sr.wasp.vocals.annotations.features.Feature;
import it.polimi.sr.wasp.vocals.annotations.features.RSPService;

import java.util.List;

@Feature(name = "StreamsGetterFeature")
public interface StreamsGetterFeature {

    @RSPService(endpoint = "/streams")
    List<InStream> get_streams();

}
