package ui;

import ui.menus.Postlogin;
import ui.menus.Prelogin;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class REPL {
    private final ChessClient client;
    private State oldstate = null;

    public REPL(int serverPort) {
        client = new ChessClient(serverPort, this);
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        String result = "";

        // primary loop machine
        while (!result.equals("Exiting...")) {
            // handle extending salutations when relevant
            if (oldstate != client.state) {
                String welcomeString = switch (client.state) {
                    case LOGGEDOUT -> ERASE_SCREEN + "Welcome to the CS 240 Chess Client, by Davis Wing\n" + Prelogin.help();
                    case LOGGEDIN -> ERASE_SCREEN + "Welcome " + client.username + " \n" + Postlogin.help(client);
                    case GAMEPLAY -> "";
                };

                System.out.println(welcomeString);
                oldstate = client.state;
            }

            // print out the prompt for the next action and listen
            System.out.print(SET_TEXT_COLOR_WHITE +  ">>> " + RESET + "\u001b[0m");
            String line = scanner.nextLine();

            // switch between menus
            try {
                result = switch (client.state) {
                    case LOGGEDOUT -> client.prelogin(line);
                    case LOGGEDIN -> client.postlogin(line);
                    case GAMEPLAY -> null; //client.gameplay(line);
                };

                // print out what came from above
                System.out.print(SET_TEXT_COLOR_WHITE + SET_TEXT_BOLD + result + "\n");

            } catch (Throwable e) {
                System.out.print(e.getMessage());
            }
        }
    }
}
