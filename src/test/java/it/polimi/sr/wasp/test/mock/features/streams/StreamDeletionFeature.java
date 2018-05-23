package it.polimi.sr.wasp.test.mock.features.streams;


import it.polimi.sr.wasp.server.enums.HttpMethod;
import it.polimi.sr.wasp.server.model.Stream;
import it.polimi.sr.wasp.vocals.annotations.features.Feature;
import it.polimi.sr.wasp.vocals.annotations.features.Param;
import it.polimi.sr.wasp.vocals.annotations.features.RSPService;

@Feature(name = "StreamDeletionFeature")
public interface StreamDeletionFeature {

    @RSPService(endpoint = "/streams", method = HttpMethod.DELETE)
    Stream delete_stream(@Param(name = "stream", uri = true) String id);

}
