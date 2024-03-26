package clientTests;

import exception.ResponseException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import result.RegisterResponse;
import server.Server;
import ui.ServerFacade;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade face;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);

        face = new ServerFacade(port);
        try {
            face.clear();
        } catch (ResponseException ignored){}
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void goodRegistration() throws ResponseException {
        String[] registrations = new String[]{"davis", "wing", "email"};
        RegisterResponse response = face.register(registrations);


        Assertions.assertEquals("davis", response.username());
        Assertions.assertFalse(response.authToken().isEmpty());
    }

    @Test
    public void badRegistration() throws ResponseException {
        String[] registrations = new String[]{"davis", "wing", "email"};
        RegisterResponse response = face.register(registrations);


        Assertions.assertEquals("davis", response.username());
        Assertions.assertFalse(response.authToken().isEmpty());
    }
}
