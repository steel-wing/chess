import ui.REPL;


public class Main {
    public static void main(String[] args) {
        int serverPort = 8080;

        if (args.length == 1) {
            serverPort = Integer.parseInt(args[0]);
        }

        new REPL(serverPort).run();
    }
}