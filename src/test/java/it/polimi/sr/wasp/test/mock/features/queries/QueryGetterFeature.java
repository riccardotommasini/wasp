package it.polimi.sr.wasp.test.mock.features.queries;


import it.polimi.sr.wasp.test.mock.model.Query;
import it.polimi.sr.wasp.vocals.annotations.features.Feature;
import it.polimi.sr.wasp.vocals.annotations.features.Param;
import it.polimi.sr.wasp.vocals.annotations.features.RSPService;

@Feature(name = "QueryGetterFeature")
public interface QueryGetterFeature {

    @RSPService(endpoint = "/queries")
    Query get_query(@Param(name = "query", uri = true) String id);

}
