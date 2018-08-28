package it.polimi.sr.wasp.rsp.features.queries;


import it.polimi.rsp.vocals.core.annotations.HttpMethod;
import it.polimi.rsp.vocals.core.annotations.features.Feature;
import it.polimi.rsp.vocals.core.annotations.features.Param;
import it.polimi.rsp.vocals.core.annotations.features.RSPService;
import it.polimi.sr.wasp.rsp.model.ObservableTask;
import it.polimi.sr.wasp.rsp.model.QueryBody;

@Feature(name = "QueryRegistrationFeature")
public interface QueryRegistrationFeature {

    @RSPService(endpoint = "/queries", method = HttpMethod.POST)
    ObservableTask register_query(@Param(name = "body") QueryBody body);

}

//TODO fix the return element to a particular kind of class that wraps up the answer for the server
