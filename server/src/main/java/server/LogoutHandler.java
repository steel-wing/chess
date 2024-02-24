package server;

import dataAccess.DataAccessException;
import request.LogoutRequest;
import service.LogoutService;
import spark.Request;
import spark.Response;

public class LogoutHandler extends Handler {
    /** Logout endpoint handler */
    public static Object logout(Request req, Response res) {
        // parse the logout request
        LogoutRequest logoutdata = new LogoutRequest(req.headers("authorization"));

        // initializes to false
        boolean loggedout;

        try {
            loggedout = LogoutService.logout(logoutdata);
        } catch (DataAccessException exception) {
            return errorHandler(exception.getMessage(),500, res);
        }

        if (!loggedout) {
            return errorHandler("unauthorized",401, res);
        }

        System.out.println("Logged Out! " + logoutdata);

        return successHandler(logoutdata, res);
    }
}
