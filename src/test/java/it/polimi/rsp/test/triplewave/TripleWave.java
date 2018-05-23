package it.polimi.rsp.test.triplewave;

import it.polimi.rsp.server.Server;
import it.polimi.rsp.vocals.annotations.services.PublishingService;

@PublishingService(host = "localhost")
public class TripleWave extends Server {

    public static void main(String[] args) {

        TripleWave tripleWave = new TripleWave();
        tripleWave.start(tripleWave, args[0]);

    }


}
