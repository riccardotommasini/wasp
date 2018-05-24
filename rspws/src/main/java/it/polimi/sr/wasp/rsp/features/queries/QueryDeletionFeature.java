package it.polimi.sr.wasp.rsp.features.queries;


import it.polimi.sr.wasp.server.enums.HttpMethod;
import it.polimi.sr.wasp.rsp.model.Query;
import it.polimi.sr.wasp.vocals.annotations.features.Feature;
import it.polimi.sr.wasp.vocals.annotations.features.Param;
import it.polimi.sr.wasp.vocals.annotations.features.RSPService;

@Feature(name = "QueryDeletionFeature")
public interface QueryDeletionFeature {

    @RSPService(endpoint = "/queries", method = HttpMethod.DELETE)
    Query delete_query(@Param(name = "query", uri = true) String id);

}
