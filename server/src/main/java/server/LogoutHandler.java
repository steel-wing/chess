package server;

import dataAccess.DataAccessException;
import request.LogoutRequest;
import service.LogoutService;
import spark.Request;
import spark.Response;

public class LogoutHandler extends Handler {
    /** Logout endpoint handler */
    public static Object logout(Request req, Response res) {
        LogoutRequest logoutdata = getBody(req, LogoutRequest.class);
        boolean loggedout;
        try {
            loggedout = LogoutService.logout(logoutdata);
        } catch (DataAccessException exception) {
            return errorHandler(exception.getMessage(),500, req, res);
        }

        if (!loggedout) {

            return "Error 401";
        }
        return logoutdata;
    }
}
