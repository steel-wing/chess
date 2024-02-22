package server;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import java.util.Map;

public class Handler {
    /** Returns the body of a JSON as the object it represents */
    protected static <T> T getBody(Request req, Class<T> tClass) {
        var body = new Gson().fromJson(req.body(), tClass);
        if (body == null) {
            throw new RuntimeException("missing required body");
        }
        return body;
    }

    /** This is an error handler provided by the class code. I'll look at it more in the future */
    protected static Object errorHandler(String explanation, int status, Request req, Response res) {
        var body = new Gson().toJson(Map.of("message", String.format("Error: %s", explanation), "success", false));
        res.type("application/json");
        res.status(status);
        res.body(body);
        return body;
    }
}
