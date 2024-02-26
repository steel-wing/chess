package server;

import service.ClearApplicationService;
import spark.Request;
import spark.Response;

public class ClearHandler extends Handler {
    /** Clear endpoint handler */
    public static Object clear(Request req, Response res) {
        // wipe out everything and return success
        ClearApplicationService.clear();

        System.out.println("Databases Cleared!");

        return successHandler(null, res);
    }
}
