package it.polimi.sr.wasp.test.triplewave;

import it.polimi.sr.wasp.server.Server;
import it.polimi.sr.wasp.vocals.annotations.services.PublishingService;

@PublishingService(host = "localhost")
public class TripleWave extends Server {

    public static void main(String[] args) {

        TripleWave tripleWave = new TripleWave();
        tripleWave.start(tripleWave, args[0]);

    }


}
