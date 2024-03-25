package server;

import dataAccess.DataAccessException;
import dataAccess.ErrorException;
import request.CreateRequest;
import result.CreateResponse;
import service.CreateService;
import spark.Request;
import spark.Response;

public class CreateHandler extends Handler {
    /** Create endpoint handler */
    public static Object create(Request req, Response res) {
        // parse the incoming request info
        String gameName = getBody(req, CreateRequest.class).gameName();
        String authToken = req.headers("authorization");
        CreateRequest createRequest = new CreateRequest(gameName, authToken);

        // initialize the output
        int gameID;

        try {
            gameID = CreateService.create(createRequest);
        } catch (DataAccessException | ErrorException exception) {
            // handle errors
            return errorHandler(exception, res);
        }

        //System.out.println("Game Created! ID:" + successHandler(new CreateResponse(gameID), res));
        return successHandler(new CreateResponse(gameID), res);
    }
}
