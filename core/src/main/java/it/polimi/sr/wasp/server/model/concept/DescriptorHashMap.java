package it.polimi.sr.wasp.server.model.concept;

import java.util.LinkedHashMap;

public class DescriptorHashMap extends LinkedHashMap<String, Object> implements Descriptor {


    @Override
    public boolean empty() {
        return isEmpty();
    }
}
