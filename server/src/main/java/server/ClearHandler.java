package server;

import service.ClearService;
import spark.Request;
import spark.Response;

public class ClearHandler extends Handler {
    /** Clear endpoint handler */
    public static Object clear(Request req, Response res) {
        // wipe out everything and return success
        boolean done = ClearService.clear();

        // handles the (impossible) case of failure
        if (!done) {
            return errorHandler("could not clear", 500, res);
        }

        System.out.println("Databases Cleared!");
        return successHandler(null, res);
    }
}
