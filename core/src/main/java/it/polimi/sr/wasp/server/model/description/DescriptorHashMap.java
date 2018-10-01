package it.polimi.sr.wasp.server.model.description;

import java.util.LinkedHashMap;

public abstract class DescriptorHashMap extends LinkedHashMap<String, Object> implements Descriptor {

    @Override
    public boolean empty() {
        return isEmpty();
    }


}
