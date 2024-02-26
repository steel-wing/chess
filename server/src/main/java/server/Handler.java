package server;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;

import java.util.Map;

public class Handler {
    /** Returns the body of a JSON as the object it represents */
    protected static <T> T getBody (Request req, Class<T> tClass) {
        var body = new Gson().fromJson(req.body(), tClass);
        if (body == null) {
            throw new RuntimeException("missing required body");
        }
        return body;
    }

    /** This is an error handler that constructs a JSON body explaining the issue */
    protected static Object errorHandler (String explanation, int status, Response res) {
        System.out.println("Error: [" + status + "], " + explanation);

        var body = new Gson().toJson(Map.of("message", String.format("Error: %s", explanation)));
        res.type("application/json");
        res.status(status);
        res.body(body);
        return body;
    }

    /** This is a success handler that constructs a JSON object for the HTTP */
    protected static Object successHandler (Object outgoing, Response res) {
        System.out.println("Success! [200]");
        var body = new Gson().toJson(outgoing);
        if (outgoing == null) {
            body = "{}";
        }
        res.type("application/json");
        res.status(200);
        res.body(body);
        return body;
    }

}
