package server;

import dataAccess.DataAccessException;
import model.AuthData;
import request.LoginRequest;
import service.LoginService;
import spark.Request;
import spark.Response;

public class LoginHandler extends Handler {
    /** Login endpoint handler */
    public static Object login(Request req, Response res) {
        // parse the login request
        LoginRequest logindata = getBody(req, LoginRequest.class);
        try {
            // get an authToken for this user
            AuthData authData = LoginService.login(logindata);

//            // eject if the user is already logged in
//            if (LoginService.isLoggedIn(logindata.username())) {
//                return errorHandler("unauthorized", 401, res);
//            }

            // handle an incorrect password case (the authdata was null)
            if (authData == null) {
                return errorHandler("unauthorized", 401, res);
            }

            System.out.println("Logged In! " + logindata.username() + " " + authData.authToken());

            // send back the authData
            return successHandler(authData, res);

        // handle a data access exception if one occurs
        } catch (DataAccessException exception) {
            return errorHandler(exception.getMessage(), 500, res);
        }
    }
}
