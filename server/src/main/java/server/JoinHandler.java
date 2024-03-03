package server;

import dataAccess.DataAccessException;
import dataAccess.ErrorException;
import request.JoinRequest;
import service.JoinService;
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

        // initialize the output
        boolean joined;

        try {
            joined = JoinService.join(joinRequest);
        } catch (DataAccessException | ErrorException exception) {
            // handle any other exceptions
            return errorHandler(exception, res);
        }

        // sort of redundant but it handles whatever cases I haven't thought of
        if (!joined) {
            return errorHandler(new ErrorException("unable to log in"), res);
        }

        System.out.println("Game Joined! ID:" + gameID);
        return successHandler(null, res);
    }
}
