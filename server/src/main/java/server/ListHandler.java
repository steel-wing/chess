package server;

import dataAccess.DataAccessException;
import model.GameData;
import result.ListResponse;
import service.ListGamesService;
import spark.Request;
import spark.Response;

import java.util.ArrayList;

public class ListHandler extends Handler {
    /** List endpoint handler */
    public static Object list(Request req, Response res) {
        // get the authToken from the header
       String authToken = req.headers("authorization");

        // initialize output
        ArrayList<GameData> gamesList;

        try {
            gamesList = ListGamesService.list(authToken);
        } catch (DataAccessException exception) {
            // handle the lack of authtoken exception
            if (exception.getMessage().equals("No such AuthToken")) {
                return errorHandler("unauthorized", 401, res);
            }

            // handle any other exceptions
            return errorHandler(exception.getMessage(),500, res);
        }

        System.out.println("List Retrieved! " + successHandler(new ListResponse(gamesList), res));
        return successHandler(new ListResponse(gamesList), res);
    }
}
