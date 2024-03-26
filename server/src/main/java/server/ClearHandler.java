package server;

import dataAccess.DataAccessException;
import service.ClearService;
import spark.Request;
import spark.Response;

public class ClearHandler extends Handler {
    /** Clear endpoint handler */
    public static Object clear(Request req, Response res) {
        // wipe out everything and return success
        try {
            ClearService.clear();
        } catch (DataAccessException exception) {
            return errorHandler(exception, res);
        }
        //System.out.println("Databases Cleared!");
        return successHandler(null, res);
    }
}
