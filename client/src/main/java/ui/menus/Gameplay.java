package ui.menus;

import ui.ChessClient;

import static ui.EscapeSequences.*;

public class Gameplay {
    public static void menuUI(String[] args) {
        System.out.println("[H] : Help for understanding functions and commands");
        System.out.println("[Q] : Quit the Chess Client");
        System.out.println("[L] : Login to your Chess Client account");
        System.out.println("[R] : Register a new Chess Client account");

    }

    public static String help(ChessClient client) {
        return RESET + SET_TEXT_COLOR_WHITE + SET_TEXT_BOLD + "Joined Game";
    }
}
