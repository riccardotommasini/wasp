package it.polimi.sr.wasp.test.vocals.model;


import it.polimi.rsp.vocals.core.annotations.HttpMethod;
import it.polimi.rsp.vocals.core.annotations.features.Feature;
import it.polimi.rsp.vocals.core.annotations.features.Param;
import it.polimi.rsp.vocals.core.annotations.features.RSPService;
import it.polimi.sr.wasp.server.model.concept.Channel;

@Feature(name = "StreamRegistrationFeature")
public interface StreamRegistrationFeature {

    @RSPService(endpoint = "/channels", method = HttpMethod.POST)
    Channel register_stream(@Param(name = "stream", uri = true) String id,
                            @Param(name = "uri") String uri);

}

//TODO fix the return element to a particular kind of class that wraps up the answer for the server
