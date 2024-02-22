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
        LoginRequest logindata = getBody(req, LoginRequest.class);
        try {
            AuthData authData = LoginService.login(logindata);
        } catch (DataAccessException exception) {
            return "Error 500";
        }

        if (logindata == null) {
            return "Error 401";
        }
        return logindata;
    }
}
