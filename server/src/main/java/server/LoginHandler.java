package server;

import dataAccess.DataAccessException;
import dataAccess.ErrorException;
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
        String username = loginRequest.username();
        String password = loginRequest.password();

        // initialize the output
        AuthData authData;

        try {
            // go get some authData for this User
            authData = LoginService.login(loginRequest);
        } catch (DataAccessException | ErrorException exception) {
            // handle errors
            return errorHandler(exception, res);
        }

        LoginResponse loginResponse = new LoginResponse(username, authData.authToken());
        System.out.println("Logged In! " + successHandler(loginResponse, res));
        return successHandler(loginResponse, res);
    }
}
