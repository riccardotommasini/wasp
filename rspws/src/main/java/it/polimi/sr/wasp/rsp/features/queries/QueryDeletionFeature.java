package it.polimi.sr.wasp.rsp.features.queries;


import it.polimi.rsp.vocals.core.annotations.HttpMethod;
import it.polimi.rsp.vocals.core.annotations.features.Feature;
import it.polimi.rsp.vocals.core.annotations.features.Param;
import it.polimi.rsp.vocals.core.annotations.features.RSPService;
import it.polimi.sr.wasp.rsp.model.Query;

@Feature(name = "QueryDeletionFeature")
public interface QueryDeletionFeature {

    @RSPService(endpoint = "/queries", method = HttpMethod.DELETE)
    Query delete_query(@Param(name = "query", uri = true) String id);

}
