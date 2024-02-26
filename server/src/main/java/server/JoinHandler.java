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
        JoinRequest partial = getBody(req, JoinRequest.class);
        String playerColor = partial.playerColor();
        int gameID = partial.gameID();
        String authToken = req.headers("authorization");
        JoinRequest joinRequest = new JoinRequest(playerColor, gameID, authToken);

        boolean loggedIn;

        // eject if we got a bad input
        if (!playerColor.isEmpty() && !playerColor.equals("WHITE") && !playerColor.equals("BLACK")) {
            return errorHandler("bad request: only 'WHITE' or 'BLACK' accepted", 400, res);
        }

        // eject if we get an empty input
        if (authToken == null || gameID == 0) {
            return errorHandler("bad request", 400, res);
        }

        try {
            loggedIn = JoinGameService.join(joinRequest);
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
            return successHandler(null, res);
        }
        return  errorHandler("could not log in", 500, res);
    }
}
