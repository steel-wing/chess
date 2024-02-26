package server;

import dataAccess.DataAccessException;
import model.AuthData;
import request.RegisterRequest;
import result.RegisterResponse;
import service.RegistrationService;
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
        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            return errorHandler("bad request", 400, res);
        }

        // initialize the output
        AuthData authData;

        try {
            // go get some authData for this User
            authData = RegistrationService.register(register);

        } catch (DataAccessException exception) {
            return errorHandler(exception.getMessage(), 500, res);
        }

        // error if the person is already registered
        if (authData == null) {
            return errorHandler("forbidden", 403, res);
        }


        System.out.println("Registered and logged in! " + register.username() + " " + authData.authToken());

        RegisterResponse registerResponse = new RegisterResponse(username, authData.authToken());
        return successHandler(registerResponse, res);
    }
}
