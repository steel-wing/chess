package server;

import dataAccess.*;
import dataAccess.DatabaseDAO.DatabaseUserDAO;
import dataAccess.MemoryDAO.MemoryAuthDAO;
import dataAccess.MemoryDAO.MemoryGameDAO;
import spark.Spark;

public class Server {
    public static final AuthDAO authDAO = new MemoryAuthDAO();
    public static final GameDAO gameDAO = new MemoryGameDAO();
    public static final UserDAO userDAO = new DatabaseUserDAO();

    public Server() {
        try{
            DatabaseManager.configureDatabase();
        }catch (DataAccessException exception){
            System.out.println("Exception on creating database manager: " + exception);
        }
    }

    /** Starts the server */
    public int run(int port) {
        Spark.port(port);

        Spark.staticFiles.location("web");

        // Endpoint registration and error handling
        Spark.delete("/db", ClearHandler::clear);
        Spark.post("/user", RegisterHandler::register);
        Spark.post("/session", LoginHandler::login);
        Spark.delete("/session", LogoutHandler::logout);
        Spark.get("/game", ListHandler::list);
        Spark.post("/game", CreateHandler::create);
        Spark.put("/game", JoinHandler::join);

        Spark.awaitInitialization();
        return Spark.port();
    }

    /** Stops the server */
    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}