package it.polimi.sr.wasp.rsp.features.streams;


import it.polimi.rsp.vocals.core.annotations.HttpMethod;
import it.polimi.rsp.vocals.core.annotations.features.Feature;
import it.polimi.rsp.vocals.core.annotations.features.Param;
import it.polimi.rsp.vocals.core.annotations.features.RSPService;
import it.polimi.sr.wasp.rsp.model.TaskBody;
import it.polimi.sr.wasp.server.model.concept.Channel;

@Feature(name = "StreamFullRegistrationFeature")
public interface StreamFullRegistrationFeature {

    @RSPService(endpoint = "/streams", method = HttpMethod.POST)
    Channel register_stream(@Param(name = "query") String query);

}

//TODO fix the return element to a particular kind of class that wraps up the answer for the server
