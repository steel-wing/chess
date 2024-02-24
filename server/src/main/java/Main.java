import dataAccess.DataAccessException;
import server.Server;

public class Main {
    public static void main(String[] args) throws DataAccessException {
        // construct and open up a new server on port 8080
        Server server = new Server();
        server.run(8080);
    }
}