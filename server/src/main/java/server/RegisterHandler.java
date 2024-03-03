package server;

import dataAccess.DataAccessException;
import dataAccess.ErrorException;
import model.AuthData;
import request.RegisterRequest;
import result.RegisterResponse;
import service.RegisterService;
import spark.Request;
import spark.Response;

public class RegisterHandler extends Handler {
    /** Register endpoint handler */
    public static Object register(Request req, Response res) {
        // parse incoming information
        RegisterRequest register = getBody(req, RegisterRequest.class);

        // initialize the output
        AuthData authData;

        try {
            authData = RegisterService.register(register);
        } catch (DataAccessException | ErrorException exception) {
            // handle errors
            return errorHandler(exception, res);
        }

        RegisterResponse registerResponse = new RegisterResponse(register.username(), authData.authToken());
        System.out.println("Registered and logged in! " + successHandler(registerResponse, res));
        return successHandler(registerResponse, res);
    }
}
