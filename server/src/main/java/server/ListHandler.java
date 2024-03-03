package server;

import dataAccess.DataAccessException;
import model.GameData;
import result.ListResponse;
import service.ListService;
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
            gamesList = ListService.list(authToken);
        } catch (DataAccessException exception) {
            // handle errors
            return errorHandler(exception, res);
        }

        System.out.println("List Retrieved! " + successHandler(new ListResponse(gamesList), res));
        return successHandler(new ListResponse(gamesList), res);
    }
}
