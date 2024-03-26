import ui.REPL;

public class Main {
    public static void main(String[] args) {
        // default port
        int serverPort = 8080;

        // handle alternative port numbers, if provided
        if (args.length == 1) {
            serverPort = Integer.parseInt(args[0]);
        }

        // construct and open up a new server on port 8080
//        Server server = new Server();
//        server.run(serverPort);

        // open up the main menu
        new REPL(serverPort).run();

        // stop the server
//        server.stop();
    }
}