package server;

import dataAccess.DataAccessException;
import model.AuthData;
import request.LoginRequest;
import result.LoginResponse;
import service.LoginService;
import spark.Request;
import spark.Response;

public class LoginHandler extends Handler {
    /** Login endpoint handler */
    public static Object login(Request req, Response res) {
        // parse the login request
        LoginRequest loginRequest = getBody(req, LoginRequest.class);

        // initialize useful variables
        AuthData authData;
        String username = loginRequest.username();
        String password = loginRequest.password();

        // check the input
        if (username == null || password == null) {
            return errorHandler("bad request", 400, res);
        }

        try {
            // go get some authData for this User
            authData = LoginService.login(loginRequest);

        } catch (DataAccessException exception) {
            return errorHandler(exception.getMessage(), 500, res);
        }

        // handle an incorrect password case (the authdata was null)
        if (authData == null) {
            return errorHandler("unauthorized", 401, res);
        }

        System.out.println("Logged In! " + loginRequest.username() + " " + authData.authToken());

        // send back the loginResponse
        LoginResponse loginResponse = new LoginResponse(username, authData.authToken());
        return successHandler(loginRequest, res);
    }
}
