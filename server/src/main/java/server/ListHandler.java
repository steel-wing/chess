package server;

import dataAccess.DataAccessException;
import model.GameData;
import request.ListRequest;
import service.ListGamesService;
import spark.Request;
import spark.Response;

import java.util.ArrayList;

public class ListHandler extends Handler {
    /** List endpoint handler */
    public static Object list(Request req, Response res) {
        // get the authToken from the header
        ListRequest listRequest = new ListRequest(req.headers("authorization"));

        if (listRequest.authToken() == null) {
            return errorHandler("bad request", 400, res);
        }

        // initialize output
        ArrayList<GameData> gamesList;

        try {
            gamesList = ListGamesService.list(listRequest);
        } catch (DataAccessException exception) {
            // handle the lack of authtoken exception
            if (exception.getMessage().equals("No such AuthToken")) {
                return errorHandler("unauthorized", 401, res);
            }

            // handle any other exceptions
            return errorHandler(exception.getMessage(),500, res);
        }

        // an empty list of games is still valid
        if (gamesList == null) {
            return successHandler(null, res);
        } else {
            // but if it isn't empty, return the whole list
            return successHandler(gamesList, res);
        }
    }
}
