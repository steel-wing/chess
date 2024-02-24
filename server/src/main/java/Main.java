import dataAccess.*;
import model.UserData;
import server.Server;

public class Main {
    public static void main(String[] args) throws DataAccessException {
        UserDAO access = new MemoryUserDAO();
        access.createUser("davis", new UserData("davis", "password", "email"));

        // construct and open up a new server on port 8080
        Server server = new Server();
        server.run(8080);
    }
}