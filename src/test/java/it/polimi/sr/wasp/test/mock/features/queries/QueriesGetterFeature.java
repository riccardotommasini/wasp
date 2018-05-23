package it.polimi.sr.wasp.test.mock.features.queries;


import it.polimi.sr.wasp.test.mock.model.Query;
import it.polimi.sr.wasp.vocals.annotations.features.Feature;
import it.polimi.sr.wasp.vocals.annotations.features.RSPService;

import java.util.List;

@Feature(name = "QueriesGetterFeature")
public interface QueriesGetterFeature {

    @RSPService(endpoint = "/queries")
    List<Query> get_queries();

}
