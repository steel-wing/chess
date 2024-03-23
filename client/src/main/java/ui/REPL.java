package ui;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class REPL {
    private final ChessClient client;

    public REPL(int serverPort) {
        client = new ChessClient(serverPort, this);
    }

    public void run() {
        System.out.println("Welcome to the CS 240 Chess Client, by Davis Wing\n");

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(SET_TEXT_COLOR_WHITE + SET_TEXT_BOLD + result);
            } catch (Throwable e) {
                System.out.print(e.getMessage());
            }
        }
        System.out.println();
    }


    private void printPrompt() {
        System.out.print("\n" + SET_TEXT_COLOR_WHITE + ">>> ");
    }

}
