package it.polimi.rsp.test.mock.features;


import it.polimi.rsp.server.enums.HttpMethod;
import it.polimi.rsp.server.model.Stream;
import it.polimi.rsp.test.mock.model.Query;
import it.polimi.rsp.vocals.annotations.features.Feature;
import it.polimi.rsp.vocals.annotations.features.Param;
import it.polimi.rsp.vocals.annotations.features.RSPService;

@Feature(name = "QueryDeletionFeature")
public interface QueryDeletionFeature {

    @RSPService(endpoint = "/queries", method = HttpMethod.DELETE)
    Query delete_query(@Param(name = "query", uri = true) String id);

}
