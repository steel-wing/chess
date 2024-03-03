package server;

import dataAccess.DataAccessException;
import dataAccess.ErrorException;
import service.LogoutService;
import spark.Request;
import spark.Response;

public class LogoutHandler extends Handler {
    /** Logout endpoint handler */
    public static Object logout(Request req, Response res) {
        // load the logout request
        String authToken = req.headers("authorization");

        // initializes to false
        boolean loggedout;

        try {
            LogoutService.logout(authToken);
        } catch (DataAccessException | ErrorException exception) {
            // handle errors
            return errorHandler(exception, res);
        }

        System.out.println("Logged Out! " + authToken);
        return successHandler(null, res);
    }
}
