package ui;

import com.google.gson.Gson;
import exception.ResponseException;
import request.CreateRequest;
import request.JoinRequest;
import request.LoginRequest;
import request.RegisterRequest;
import result.CreateResponse;
import result.ListResponse;
import result.LoginResponse;
import result.RegisterResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

/**
 * This ServerFacade acts as an intermediary between the Client and the Server.
 * It's the one who makes HTML requests
 */
public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public LoginResponse login(String... params) throws ResponseException {
        var path = "/session";
        LoginRequest request = new LoginRequest(params[0], params[1]);

        return this.makeRequest("POST", path, request, null, LoginResponse.class);
    }

    public RegisterResponse register(String... params) throws ResponseException {
        var path = "/user";
        RegisterRequest request = new RegisterRequest(params[0], params[1], params[2]);

        return this.makeRequest("POST", path, request, null, RegisterResponse.class);
    }

    public CreateResponse create(String gamename, String authToken) throws ResponseException {
        var path = "/game";
        CreateRequest request = new CreateRequest(gamename, authToken);

        return this.makeRequest("POST", path, request, authToken, CreateResponse.class);
    }

    public void join(String playerColor, int gameID, String authToken) throws ResponseException {
        var path = "/game";
        JoinRequest request = new JoinRequest(playerColor, gameID, authToken);

        this.makeRequest("PUT", path, request, authToken, null);
    }

    public ListResponse list(String authToken) throws ResponseException {
        var path = "/game";

        return this.makeRequest("GET", path, null, authToken, ListResponse.class);
    }

    public void logout(String authToken) throws ResponseException {
        var path = "/session";

        this.makeRequest("DELETE", path, null, authToken, null);
    }

    public void clear() throws ResponseException {
        var path = "/db";

        this.makeRequest("DELETE", path, null, null, null);
    }

    private <T> T makeRequest(String method, String path, Object request, String authToken, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);
            http.setRequestProperty("authorization", authToken);

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new ResponseException(status, "failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        // they had this as status / 100 == 2. Odd.
        return status == 200;
    }
}
