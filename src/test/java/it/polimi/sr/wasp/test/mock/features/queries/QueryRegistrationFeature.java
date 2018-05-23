package it.polimi.sr.wasp.test.mock.features.queries;


import it.polimi.sr.wasp.server.enums.HttpMethod;
import it.polimi.sr.wasp.test.mock.model.Query;
import it.polimi.sr.wasp.test.mock.model.QueryBody;
import it.polimi.sr.wasp.vocals.annotations.features.Feature;
import it.polimi.sr.wasp.vocals.annotations.features.Param;
import it.polimi.sr.wasp.vocals.annotations.features.RSPService;

@Feature(name = "QueryRegistrationFeature")
public interface QueryRegistrationFeature {

    @RSPService(endpoint = "/queries", method = HttpMethod.POST)
    Query register_query(@Param(name = "body") QueryBody body);

}

//TODO fix the return element to a particular kind of class that wraps up the answer for the server
