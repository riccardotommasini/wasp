package it.polimi.sr.wasp.rsp.features.queries;


import it.polimi.rsp.vocals.core.annotations.features.Feature;
import it.polimi.rsp.vocals.core.annotations.features.RSPService;
import it.polimi.sr.wasp.rsp.model.Query;

import java.util.List;

@Feature(name = "QueriesGetterFeature")
public interface QueriesGetterFeature {

    @RSPService(endpoint = "/queries")
    List<Query> get_queries();

}
