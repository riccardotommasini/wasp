package it.polimi.rsp.test.mock;


import it.polimi.rsp.server.HttpMethod;
import it.polimi.rsp.vocals.annotations.features.Feature;
import it.polimi.rsp.vocals.annotations.features.Param;
import it.polimi.rsp.vocals.annotations.features.RSPService;

@Feature(name = "MockFeaturePost")
public interface MockFeaturePost {

    @RSPService(endpoint = "/custompostmethod", method = HttpMethod.POST)
    MockReturnClass customPostMethod(@Param(name = "uri_param1", uri = true) String id,
                                     @Param(name = "body_param2") MockInputClass body);

}

//TODO fix the return element to a particular kind of class that wraps up the answer for the server
