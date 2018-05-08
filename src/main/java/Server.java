import lombok.extern.java.Log;

import java.io.BufferedInputStream;
import java.io.InputStream;

import static spark.Spark.*;

@Log
public class Server {
    public static void main(String[] args) {
        port(8182);
        path("/csparql", () -> {
            get("", (req, res) -> Server.class.getClassLoader().getResourceAsStream("sgraph.json"));

            path("/streams", () -> {
                get("", (request, response) -> Server.class.getClassLoader().getResourceAsStream("streams.json"));
                get("/:id", (request, response) -> Server.class.getClassLoader().getResourceAsStream(request.params("id") + ".json"));
                post("/:id", (request, response) -> {
                    response.status(200);
                    return response;
                });
            });

            path("/graphs", () -> {
                get("", (request, response) -> Server.class.getClassLoader().getResourceAsStream("graphs.json"));
                get("/:id", (request, response) -> Server.class.getClassLoader().getResourceAsStream(request.params("id") + ".json"));
                post("/:id", (request, response) -> {
                    response.status(200);
                    return response;
                });
            });

            path("/queries", () -> {
                get("", (request, response) -> Server.class.getClassLoader().getResourceAsStream("queries.json"));
                get("/:id", (request, response) -> Server.class.getClassLoader().getResourceAsStream(request.params("id") + ".json"));
                post("/:id", (request, response) -> {
                    InputStream resp_body = Server.class.getClassLoader().getResourceAsStream("query.json");

                    log.info(request.params("id"));
                    log.info("query_body: " + request.body());

                    response.body(new BufferedInputStream(resp_body).toString());
                    response.status(200);          // set status code to 401
                    return resp_body;
                });
            });

        });
        log.info("Server Running on port 8182");
    }

}
