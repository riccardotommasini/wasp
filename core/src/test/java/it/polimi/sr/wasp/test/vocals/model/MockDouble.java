package it.polimi.sr.wasp.test.vocals.model;

import it.polimi.sr.wasp.server.model.Stream;
import it.polimi.sr.wasp.vocals.annotations.services.ProcessingService;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
@ProcessingService(host = "localhost", port = 8181)
public class MockDouble implements StreamRegistrationAndStreamsGetterFeatures {

    String name, base;

    @Override
    public List<Stream> get_streams() {
        return null;
    }

    @Override
    public Stream register_stream(String id, String uri) {
        return null;
    }
}
