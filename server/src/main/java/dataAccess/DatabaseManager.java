package dataAccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseManager {
    private static final String databaseName;
    private static final String user;
    private static final String password;
    private static final String connectionUrl;

    /*
     * Load the database information for the db.properties file.
     */
    static {
        try {
            try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
                if (propStream == null) throw new Exception("Unable to laod db.properties");
                Properties props = new Properties();
                props.load(propStream);
                databaseName = props.getProperty("db.name");
                user = props.getProperty("db.user");
                password = props.getProperty("db.password");

                var host = props.getProperty("db.host");
                var port = Integer.parseInt(props.getProperty("db.port"));
                connectionUrl = String.format("jdbc:mysql://%s:%d", host, port);
            }
        } catch (Exception exception) {
            throw new RuntimeException("unable to process db.properties. " + exception.getMessage());
        }
    }

    /**
     * stolen from petshop, creates a database and establishes a connection to it using the functions below
     * @throws DataAccessException in the event of... issues
     */
    public static void configureDatabase() throws DataAccessException {
        // see below
        createDatabase();
        // establish a connection with good practice
        try (var connect = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = connect.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        // if there are complications, handle them
        } catch (SQLException exception) {
            throw new DataAccessException(String.format("Unable to configure database: %s", exception.getMessage()));
        }
    }

    // creates three databases, matching the maps we made in the memoryDAO implementation
    private static final String[] createStatements = {

            // AUTH: table of authToken (primary) to username
            """
            CREATE TABLE IF NOT EXISTS AUTH (
              `authToken` varchar(256) NOT NULL,
              `username` varchar(256) NOT NULL,
              PRIMARY KEY (`authToken`)
            )
            """,
            // GAME: table of gameID (primary) to gameData
            """
            CREATE TABLE IF NOT EXISTS GAME (
              `gameID` int NOT NULL,
              `gameData` TEXT DEFAULT NULL,
              PRIMARY KEY (`gameID`)
            )""",
            // USER: table of id (primary) to username to userData
            """
            CREATE TABLE IF NOT EXISTS USER (
              `id` int NOT NULL AUTO_INCREMENT,
              `username` varchar(256) NOT NULL,
              `userData` TEXT DEFAULT NULL,
              PRIMARY KEY (`id`)
            )"""
    };

    /**
     * Creates the database if it does not already exist.
     */
    static void createDatabase() throws DataAccessException {
        try {
            var statement = "CREATE DATABASE IF NOT EXISTS " + databaseName;
            var connect = DriverManager.getConnection(connectionUrl, user, password);
            try (var preparedStatement = connect.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException exception) {
            throw new DataAccessException(exception.getMessage());
        }
        System.out.println("database " + databaseName + " created");
    }

    /**
     * Create a connection to the database and sets the catalog based upon the
     * properties specified in db.properties. Connections to the database should
     * be short-lived, and you must close the connection when you are done with it.
     * The easiest way to do that is with a try-with-resource block.
     * <br/>
     * <code>
     * try (var conn = DbInfo.getConnection(databaseName)) {
     * // execute SQL statements.
     * }
     * </code>
     */
    public static Connection getConnection() throws DataAccessException {
        try {
            var connect = DriverManager.getConnection(connectionUrl, user, password);
            connect.setCatalog(databaseName);
            System.out.println(connect);
            return connect;
        } catch (SQLException exception) {
            throw new DataAccessException(exception.getMessage());
        }
    }
}
