package it.polimi.rsp.test.mock;


import it.polimi.rsp.vocals.annotations.Feature;
import it.polimi.rsp.vocals.annotations.Param;
import it.polimi.rsp.vocals.annotations.RSPService;

@Feature(name = "MockFeatureGet", ns = "http://example.org/")
public interface MockFeatureGet {

    @RSPService(endpoint = "/customgetmethod")
    MockReturnClass customGetMethod(@Param(name = "uri_param1", uri = true) String id);

}
