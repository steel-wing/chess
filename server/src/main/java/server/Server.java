package server;

import dataAccess.*;
import dataAccess.DatabaseDAO.DatabaseAuthDAO;
import dataAccess.DatabaseDAO.DatabaseGameDAO;
import dataAccess.DatabaseDAO.DatabaseUserDAO;
import spark.Spark;
import websocket.WebSocketHandler;

public class Server {
    public static final AuthDAO authDAO = new DatabaseAuthDAO();
    public static final GameDAO gameDAO = new DatabaseGameDAO();
    public static final UserDAO userDAO = new DatabaseUserDAO();
    public final WebSocketHandler webSocketHandler;

    public Server() {
        // establish SQL connection for storage
        try{
            DatabaseManager.configureDatabase();
        } catch (DataAccessException exception){
            System.out.println("Exception on creating database manager: " + exception);
        }
        this.webSocketHandler = new WebSocketHandler();
    }

    /** Starts the server */
    public int run(int port) {
        // Spark really the MVP out here frfr
        Spark.port(port);

        Spark.staticFiles.location("web");

        Spark.webSocket("/connect", webSocketHandler);

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