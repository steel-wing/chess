package server;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.util.Map;

public class Server {

    public int run(int port) {
        Spark.port(port);

        Spark.staticFiles.location("web");

        // Endpoint registration and error handling
        Spark.delete("/db", this::clear);
        Spark.post("/user", this::register);
        Spark.post("/session", LoginHandler::login);
        Spark.delete("/session", LogoutHandler::logout);
        Spark.get("/game", this::list);
        Spark.post("/game", this::create);
        Spark.put("/game", this::join);


        Spark.awaitInitialization();
        return Spark.port();
    }

    /** Clear endpoint handler */
    private Object clear(Request req, Response res) {
        return null;
    }

    /** Register endpoint handler */
    private Object register(Request req, Response res) {
        return null;
    }

    /** List endpoint handler */
    private Object list(Request req, Response res) {
        return null;
    }

    /** Create endpoint handler */
    private Object create(Request req, Response res) {
        return null;
    }

    /** Join endpoint handler */
    private Object join(Request req, Response res) {
        return null;
    }

    /** This is an error handler provided by the class code. I'll look at it more in the future */
    public Object errorHandler(Exception e, Request req, Response res) {
        var body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage()), "success", false));
        res.type("application/json");
        res.status(500);
        res.body(body);
        return body;
    }

    /** Stops the server */
    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}




