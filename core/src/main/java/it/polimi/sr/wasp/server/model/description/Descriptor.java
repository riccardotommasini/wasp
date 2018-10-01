package it.polimi.sr.wasp.server.model.description;

import java.util.Map;

public interface Descriptor {

    Map<String, Object> context();

    boolean empty();

}
