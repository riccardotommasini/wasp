package it.polimi.rsp.server;

public class EmptyPayload implements Validable{
    @Override
    public boolean isValid() {
        return true;
    }
}
