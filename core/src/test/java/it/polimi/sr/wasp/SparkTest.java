package it.polimi.sr.wasp;

import lombok.extern.java.Log;
import spark.Request;
import spark.Response;
import spark.Spark;

@Log
public class SparkTest {


    public static void main(String[] args) {
        final Obj o = new Obj();
        Spark.port(8080);
        Spark.get("/test", (Request request, Response response) -> {
            log.info("/new " + o.get());
            Spark.get("/new" + o.get(), (request1, response1) -> "Second " + o.get());
            return "first " + o.plus();
        });
    }

    private static class Obj {

        int i = 0;

        public int get() {
            return i;
        }

        public int plus() {
            return i++;
        }
    }
}
