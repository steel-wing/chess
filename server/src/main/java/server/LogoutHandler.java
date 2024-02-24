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
        LogoutRequest logoutdata = new LogoutRequest(getHeader(req));

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

        return successHandler(logoutdata, res);
    }
}
