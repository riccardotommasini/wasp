package it.polimi.rsp.test.mock.features;


import it.polimi.rsp.server.enums.HttpMethod;
import it.polimi.rsp.test.mock.model.Query;
import it.polimi.rsp.test.mock.model.QueryBody;
import it.polimi.rsp.vocals.annotations.features.Feature;
import it.polimi.rsp.vocals.annotations.features.Param;
import it.polimi.rsp.vocals.annotations.features.RSPService;

@Feature(name = "QueryRegistrationFeature")
public interface QueryRegistrationFeature {

    @RSPService(endpoint = "/queries", method = HttpMethod.POST)
    Query register_query(@Param(name = "body") QueryBody body);

}

//TODO fix the return element to a particular kind of class that wraps up the answer for the server
