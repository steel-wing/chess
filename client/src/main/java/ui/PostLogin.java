package ui;

public class PostLogin {
    public static void menuUI(String[] args) {
        String username = "dummy";
        System.out.println("Welcome " + username + " to the Chess Game Menu\n");
        System.out.println("Please select one of the following options:");
        System.out.println("[H] : Help for understanding functions and commands");
        System.out.println("[L] : List the current running chess games");
        System.out.println("[C] : Create a new game of chess");
        System.out.println("[J] : Join an existing game of chess");
        System.out.println("[W] : Watch an existing game of chess");
        System.out.println("[X] : Logout and return to the Chess Game Client");


    }
}
