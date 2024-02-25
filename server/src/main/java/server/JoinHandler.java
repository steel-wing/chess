package server;

import dataAccess.DataAccessException;
import request.JoinRequest;
import service.JoinGameService;
import spark.Request;
import spark.Response;

public class JoinHandler extends Handler {
    /** Join endpoint handler */
    public static Object join(Request req, Response res) {
        // parse the incoming request info
        JoinRequest joinRequest = getBody(req, JoinRequest.class);

        String authToken = req.headers("authorization");
        String playerColor = joinRequest.playerColor();
        int gameID = joinRequest.gameID();
        boolean loggedIn;

        if (authToken == null || gameID == 0) {
            return errorHandler("bad request", 400, res);
        }

        try {
            loggedIn = JoinGameService.join(joinRequest, authToken);
        } catch (DataAccessException exception) {
            // handle the "already taken" exception
            if (exception.getMessage().equals("already taken")) {
                return errorHandler("already taken", 403, res);
            }

            // handle the "unauthorized" exception
            if (exception.getMessage().equals("No such AuthToken")) {
                return errorHandler("unauthorized", 401, res);
            }
            // handle any other exceptions
            return errorHandler(exception.getMessage(), 500, res);
        }

        // sort of redundant but it handles whatever cases I haven't thought of
        if (loggedIn) {
            return true;
        }
        return false;
    }
}
