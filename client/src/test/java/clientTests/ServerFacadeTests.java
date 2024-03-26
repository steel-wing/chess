package clientTests;

import exception.ResponseException;
import model.GameData;
import org.junit.jupiter.api.*;
import result.CreateResponse;
import result.LoginResponse;
import result.RegisterResponse;
import server.Server;
import ui.ServerFacade;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade face;
    private static int port;

    private static final String[] user = new String[]{"davis", "wing", "email"};

    @BeforeAll
    public static void init() {
        server = new Server();
        port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void clear() {
        face = new ServerFacade(port);
        try {
            face.clear();
        } catch (ResponseException ignored){}
    }


    @Test
    public void goodRegistration() throws ResponseException {
        // register someone and show that they are registered
        RegisterResponse response = face.register(user);


        Assertions.assertEquals("davis", response.username());
        Assertions.assertFalse(response.authToken().isEmpty());
    }

    @Test
    public void badRegistration() throws ResponseException {
        // try to register the same person twice
        face.register(user);

        Assertions.assertThrows(ResponseException.class, () -> face.register(user));
    }


    @Test
    public void goodLogin() throws ResponseException {
        // register someone and log them in
        RegisterResponse responseR = face.register(user);

        // verify that the values match? and that we got an authtoken?
        String[] userlogin = new String[]{user[0], user[1]};
        LoginResponse responseL = face.login(userlogin);

        Assertions.assertEquals(responseR.username(), responseL.username());
        Assertions.assertFalse(responseL.authToken().isEmpty());
    }

    @Test
    public void badLogin() {
        // try to log in with an account that doesn't exist
        String[] userlogin = new String[]{"user[0]", "user[1]"};
        Assertions.assertThrows(ResponseException.class, () -> face.login(userlogin));
    }


    @Test
    public void goodList() throws ResponseException {
        // register and create a game
        String authToken = face.register(user).authToken();
        face.create("gamegame", authToken);

        // retrieve the list and verify that there is one game
        Assertions.assertEquals(1, face.list(authToken).games().size());
    }

    @Test
    public void badList() {
        // try to list games when you don't have authorization
        Assertions.assertThrows(ResponseException.class, () -> face.list("dummy"));
    }

    @Test
    public void goodJoin() throws ResponseException {
        // register and create a game
        String authToken = face.register(user).authToken();
        face.create("gamegame", authToken);
        int gameID = face.list(authToken).games().getFirst().gameID();

        // join the game, then verify that the username is in the WHITE position
        face.join("WHITE", gameID,authToken);
        GameData game = face.list(authToken).games().getFirst();
        Assertions.assertEquals(user[0], game.whiteUsername());
    }

    @Test
    public void badJoin() {
        // try to enter the club without an ID
        Assertions.assertThrows(ResponseException.class, () -> face.join("WHITE", 0, ""));
    }

    @Test
    public void goodLogout() throws ResponseException {
        // register someone and log them in
        String authToken = face.register(user).authToken();

        // if this doesn't throw any errors, that means we're good!
        face.logout(authToken);
    }

    @Test
    public void badLogout() throws ResponseException {
        // register someone and log them in
        String authToken = face.register(user).authToken();

        // try to log out without authorization
        Assertions.assertThrows(ResponseException.class, () -> face.logout("dummy"));
    }

    @Test
    public void goodCreate() throws ResponseException {
        // register and create a game
        String authToken = face.register(user).authToken();
        CreateResponse response = face.create("gamegame", authToken);

        // a not null check for integers
        Assertions.assertTrue(response.gameID() != 0);
    }

    @Test
    public void badCreate() {
        // try to make a game without correct authorization
        Assertions.assertThrows(ResponseException.class, () -> face.create("name", "dummy"));
    }

    @Test
    public void goodClear() throws ResponseException {
        // put a game in
        String authToken = face.register(user).authToken();
        CreateResponse response = face.create("gamegame", authToken);

        // clear it
        face.clear();

        // verify that Obama is gone
        Assertions.assertThrows(ResponseException.class, () -> face.join("WHITE", response.gameID(), authToken));
    }
}
