package it.polimi.rsp.test.mock;


import it.polimi.rsp.server.enums.HttpMethod;
import it.polimi.rsp.vocals.annotations.features.Feature;
import it.polimi.rsp.vocals.annotations.features.Param;
import it.polimi.rsp.vocals.annotations.features.RSPService;

@Feature(name = "StreamRegistrationFeature")
public interface StreamRegistrationFeature {

    @RSPService(endpoint = "/streams", method = HttpMethod.POST)
    InStream register_stream(@Param(name = "stream", uri = true) String id,
                             @Param(name = "uri") String uri);

}

//TODO fix the return element to a particular kind of class that wraps up the answer for the server
