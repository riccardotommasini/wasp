package it.polimi.sr.wasp.rsp.features.queries;


import it.polimi.sr.wasp.rsp.model.Query;
import it.polimi.sr.wasp.vocals.annotations.features.Feature;
import it.polimi.sr.wasp.vocals.annotations.features.RSPService;

import java.util.List;

@Feature(name = "QueriesGetterFeature")
public interface QueriesGetterFeature {

    @RSPService(endpoint = "/queries")
    List<Query> get_queries();

}
