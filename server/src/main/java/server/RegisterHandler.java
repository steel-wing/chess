package server;

import dataAccess.DataAccessException;
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
        String username = register.username();
        String password = register.password();
        String email = register.email();

        // check the input
        if (username == null || password == null || email == null) {
            return errorHandler("bad register request", 400, res);
        }

        // initialize the output
        AuthData authData;

        try {
            // go get some authData for this User
            authData = RegisterService.register(register);

        } catch (DataAccessException exception) {
            return errorHandler(exception.getMessage(), 500, res);
        }

        // error if the person is already registered
        if (authData == null) {
            return errorHandler("forbidden", 403, res);
        }

        RegisterResponse registerResponse = new RegisterResponse(username, authData.authToken());
        System.out.println("Registered and logged in! " + successHandler(registerResponse, res));
        return successHandler(registerResponse, res);
    }
}
