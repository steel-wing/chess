package server;

import dataAccess.DataAccessException;
import service.LogoutService;
import spark.Request;
import spark.Response;

public class LogoutHandler extends Handler {
    /** Logout endpoint handler */
    public static Object logout(Request req, Response res) {
        // load the logout request
        String authToken = req.headers("authorization");

        // bad request if no authToken provided
        if (authToken == null) {
            return errorHandler("bad logout request", 400, res);
        }

        // initializes to false
        boolean loggedout;

        try {
            // actually try to log out, and throw error if you couldn't
            loggedout = LogoutService.logout(authToken);

        } catch (DataAccessException exception) {
            // handle the lack of authtoken exception
            if (exception.getMessage().equals("No such AuthToken")) {
                return errorHandler("unauthorized", 401, res);
            }

            // handle any other exceptions
            return errorHandler(exception.getMessage(),500, res);
        }

        // if the logout failed it's cause you ain't him
        if (!loggedout) {
            return errorHandler("unauthorized",401, res);
        }

        System.out.println("Logged Out! " + authToken);
        return successHandler(null, res);
    }
}
