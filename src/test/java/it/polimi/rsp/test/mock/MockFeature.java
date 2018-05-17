package it.polimi.rsp.test.mock;


import it.polimi.rsp.vocals.annotations.Param;
import it.polimi.rsp.vocals.annotations.RSPService;
import it.polimi.rsp.HttpMethod;
import it.polimi.yasper.core.annotations.Feature;

@Feature(vocals = "MockFeature")
public interface MockFeature {

    @RSPService(endpoint = "/custom", method = HttpMethod.POST)
    MockReturnClass customMethod(@Param(name = "id", uri = true) String id,
                                 @Param(name = "body") MockInputClass body);

}
