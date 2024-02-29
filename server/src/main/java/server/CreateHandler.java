package server;

import dataAccess.DataAccessException;
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

        // check the input
        if (gameName == null || authToken == null) {
            return errorHandler("bad create request", 400, res);
        }

        // initialize the output
        int gameID;

        try {
            gameID = CreateService.create(createRequest);
        } catch (DataAccessException exception) {
            // handle the lack of authtoken exception
            if (exception.getMessage().equals("No such AuthToken")) {
                return errorHandler("unauthorized", 401, res);
            }

            // handle any other exceptions
            return errorHandler(exception.getMessage(),500, res);
        }

        System.out.println("Game Created! ID:" + successHandler(new CreateResponse(gameID), res));
        return successHandler(new CreateResponse(gameID), res);
    }
}
