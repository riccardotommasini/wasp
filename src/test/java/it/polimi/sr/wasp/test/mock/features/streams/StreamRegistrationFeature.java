package it.polimi.sr.wasp.test.mock.features.streams;


import it.polimi.sr.wasp.server.enums.HttpMethod;
import it.polimi.sr.wasp.test.mock.model.InStream;
import it.polimi.sr.wasp.vocals.annotations.features.Feature;
import it.polimi.sr.wasp.vocals.annotations.features.Param;
import it.polimi.sr.wasp.vocals.annotations.features.RSPService;

@Feature(name = "StreamRegistrationFeature")
public interface StreamRegistrationFeature {

    @RSPService(endpoint = "/streams", method = HttpMethod.POST)
    InStream register_stream(@Param(name = "stream", uri = true) String id,
                             @Param(name = "uri") String uri);

}

//TODO fix the return element to a particular kind of class that wraps up the answer for the server
