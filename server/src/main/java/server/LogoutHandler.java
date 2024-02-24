package server;

import dataAccess.DataAccessException;
import request.LogoutRequest;
import service.LogoutService;
import spark.Request;
import spark.Response;

public class LogoutHandler extends Handler {
    /** Logout endpoint handler */
    public static Object logout(Request req, Response res) {
        // initializes to false
        boolean loggedout;

        // parse the logout request
        LogoutRequest logoutdata = new LogoutRequest(req.headers("authorization"));

        // bad request if no authToken provided
        if (logoutdata.authToken() == null) {
            return errorHandler("bad request", 400, res);
        }

        try {
            // actually try to log out, and throw error if you couldn't
            loggedout = LogoutService.logout(logoutdata);

        } catch (DataAccessException exception) {
            return errorHandler(exception.getMessage(),500, res);
        }

        if (!loggedout) {
            return errorHandler("unauthorized",401, res);
        }

        System.out.println("Logged Out! " + logoutdata.authToken());

        return successHandler(logoutdata, res);
    }
}
