package server;

import com.google.gson.Gson;
import spark.Request;

public class Handler {
    /** Returns the body of a JSON as the object it represents */
    protected static <T> T getBody(Request req, Class<T> tClass) {
        var body = new Gson().fromJson(req.body(), tClass);
        if (body == null) {
            throw new RuntimeException("missing required body");
        }
        return body;
    }
}
