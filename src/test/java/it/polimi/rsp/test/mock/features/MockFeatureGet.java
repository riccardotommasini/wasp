package it.polimi.rsp.test.mock.features;


import it.polimi.rsp.test.mock.model.InStream;
import it.polimi.rsp.vocals.annotations.features.Feature;
import it.polimi.rsp.vocals.annotations.features.Param;
import it.polimi.rsp.vocals.annotations.features.RSPService;

@Feature(name = "MockFeatureGet", ns = "http://example.org/")
public interface MockFeatureGet {

    @RSPService(endpoint = "/customgetmethod")
    InStream customGetMethod(@Param(name = "uri_param1", uri = true) String id);

}