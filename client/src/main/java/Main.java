import ui.REPL;

public class Main {
    public static void main(String[] args) {
        // default url
        var serverUrl = "http://localhost:8080";

        // handle alternative port numbers, if provided
        if (args.length == 1) {
            serverUrl = args[0];
        }

        // construct and open up a new server on port 8080
        // Server server = new Server();
        // server.run(serverPort);

        // open up the main menu
        new REPL(serverUrl).run();

        // stop the server
        // server.stop();
    }
}