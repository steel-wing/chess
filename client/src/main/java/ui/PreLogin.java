package ui;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;

public class PreLogin {
    public static void menuUI(String[] args) {
        System.out.println("Please select one of the following options:");
        System.out.println("[H] : Help for understanding functions and commands");
        System.out.println("[L] : Login to your Chess Game account");
        System.out.println("[R] : Register a new Chess Game account");
        System.out.println("[X] : Exit the Chess Client");

        // show a new chess game
        ChessGame chessGame = new ChessGame();
        chessGame.setTeamTurn(BLACK);

        System.out.println("\nplacing black queen");
        chessGame.getBoard().addPiece(new ChessPosition(2, 5), new ChessPiece(BLACK, ChessPiece.PieceType.QUEEN));
        System.out.println("placing white queen");
        chessGame.getBoard().addPiece(new ChessPosition(7, 5), new ChessPiece(WHITE, ChessPiece.PieceType.QUEEN));

        System.out.println("print game from white's perspective");
        System.out.println(chessGame.toString(WHITE));
        System.out.println("print game from black's perspective");
        System.out.println(chessGame.toString(BLACK));

        System.out.println("print game from white king's perspective (white)");
        System.out.println(chessGame.printValids(WHITE, new ChessPosition(1, 4)));
        System.out.println("print game from white king's perspective (black)");
        System.out.println(chessGame.printValids(BLACK, new ChessPosition(1, 4)));

        chessGame.setTeamTurn(WHITE);

        System.out.println("print game from white's perspective");
        System.out.println(chessGame.toString(WHITE));
        System.out.println("print game from black's perspective");
        System.out.println(chessGame.toString(BLACK));

        // gooood lawd we need to fix these prints good luck davis! I believe in you!
        System.out.println("print game from white king's perspective (white)");
        System.out.println(chessGame.printValids(WHITE, new ChessPosition(1, 4)));
        System.out.println("print game from white king's perspective (black)");
        System.out.println(chessGame.printValids(BLACK, new ChessPosition(2, 5)));
        System.out.println(chessGame.printValids(BLACK, new ChessPosition(1, 5)));
        System.out.println(chessGame.printValids(BLACK, new ChessPosition(2, 1)));
        System.out.println(chessGame.printValids(BLACK, new ChessPosition(2, 8)));

    }
}
//public class Repl implements NotificationHandler {
//    private final PetClient client;
//
//    public Repl(String serverUrl) {
//        client = new PetClient(serverUrl, this);
//    }
//
//    public void run() {
//        System.out.println("\uD83D\uDC36 Welcome to the pet store. Sign in to start.");
//        System.out.print(client.help());
//
//        Scanner scanner = new Scanner(System.in);
//        var result = "";
//        while (!result.equals("quit")) {
//            printPrompt();
//            String line = scanner.nextLine();
//
//            try {
//                result = client.eval(line);
//                System.out.print(BLUE + result);
//            } catch (Throwable e) {
//                System.out.print(e.getMessage());
//            }
//        }
//        System.out.println();
//    }
//
//    public void notify(Notification notification) {
//        System.out.println(RED + notification.message());
//        printPrompt();
//    }
//
//    private void printPrompt() {
//        System.out.print("\n" + RESET + ">>> " + GREEN);
//    }
//
//}