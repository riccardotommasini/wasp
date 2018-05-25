package it.polimi.sr.wasp.test.vocals.model;

import it.polimi.rsp.vocals.core.annotations.services.ProcessingService;
import it.polimi.sr.wasp.server.model.concept.Channel;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
@ProcessingService(host = "localhost", port = 8181)
public class MockSingles implements StreamRegistrationFeature, StreamsGetterFeature {

    String name, base;

    @Override
    public List<Channel> get_streams() {
        return null;
    }

    @Override
    public Channel register_stream(String id, String uri) {
        return null;
    }
}
