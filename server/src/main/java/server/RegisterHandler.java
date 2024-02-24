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
        RegisterRequest register = getBody(req, RegisterRequest.class);

        // initialize useful variables
        AuthData authData;
        String username = register.username();
        String password = register.password();
        String email = register.email();

        try {
            // go get some authData for this User
            authData = RegistrationService.register(register);

        } catch (DataAccessException exception) {
            return errorHandler(exception.getMessage(), 500, res);
        }

        // check the input, and if the user already existed (authData == null)
        if (username == null || password == null || email == null || authData == null) {
            return errorHandler("bad request", 400, res);
        }

        System.out.println("Logged In! " + register.username() + " " + authData.authToken());

        RegisterResponse registerResponse = new RegisterResponse(username, authData.authToken());
        return successHandler(registerResponse, res);
    }
}
