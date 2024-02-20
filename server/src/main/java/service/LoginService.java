package service;

import dataAccess.AuthDAO;
import dataAccess.UserDAO;
import model.UserData;

public class LoginService {
    public UserData getUser(String username) {
        return UserDAO.SELECT(username);
    }

    public void createUser(String username, String password, String email) {
        UserDAO.INSERT(username, password, email);
    }

    public String createAuth(String username) {
        // terrible horrible needs to be replaced
        String authToken = Integer.toString(username.hashCode() + (int)System.currentTimeMillis());
        AuthDAO.INSERT(authToken, username);
        return authToken;
    }
}
