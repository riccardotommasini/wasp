package it.polimi.sr.wasp.rsp.features.queries;


import it.polimi.rsp.vocals.core.annotations.features.Feature;
import it.polimi.rsp.vocals.core.annotations.features.Param;
import it.polimi.rsp.vocals.core.annotations.features.RSPService;
import it.polimi.sr.wasp.rsp.model.ObservableTask;

@Feature(name = "QueryGetterFeature")
public interface QueryGetterFeature {

    @RSPService(endpoint = "/queries")
    ObservableTask get_query(@Param(name = "query", uri = true) String id);

}
