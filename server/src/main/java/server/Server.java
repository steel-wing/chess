package server;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import model.AuthData;
import request.LoginRequest;
import request.LogoutRequest;
import service.LoginService;
import service.LogoutService;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.util.Map;

public class Server {

    public int run(int port) {
        Spark.port(port);

        Spark.staticFiles.location("web");

        // Endpoint registration and error handling
        Spark.delete("/db", this::clear);
        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.get("/game", this::list);
        Spark.post("/game", this::create);
        Spark.put("/game", this::join);


        Spark.awaitInitialization();
        return Spark.port();
    }

    /** Clear endpoint handler */
    private Object clear(Request req, Response res) {
        return null;
    }

    /** Register endpoint handler */
    private Object register(Request req, Response res) {
        return null;
    }

    /** Login endpoint handler */
    private Object login(Request req, Response res) {
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

    /** Logout endpoint handler */
    private Object logout(Request req, Response res) {
        LogoutRequest logoutdata = getBody(req, LogoutRequest.class);
        boolean loggedout;
        try {
            loggedout = LogoutService.logout(logoutdata);
        } catch (DataAccessException exception) {
            return "Error 500";
        }

        if (!loggedout) {
            return "Error 401";
        }
        return logoutdata;
    }

    /** List endpoint handler */
    private Object list(Request req, Response res) {
        return null;
    }

    /** Create endpoint handler */
    private Object create(Request req, Response res) {
        return null;
    }

    /** Join endpoint handler */
    private Object join(Request req, Response res) {
        return null;
    }

    /** Returns the body of a JSON as the object it represents */
    private static <T> T getBody(Request req, Class<T> tClass) {
        var body = new Gson().fromJson(req.body(), tClass);
        if (body == null) {
            throw new RuntimeException("missing required body");
        }
        return body;
    }

    /** This is an error handler provided by the class code. I'll look at it more in the future */
    public Object errorHandler(Exception e, Request req, Response res) {
        var body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage()), "success", false));
        res.type("application/json");
        res.status(500);
        res.body(body);
        return body;
    }

    /** Stops the server */
    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}




