package model;
import chess.game.ChessGame;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {}
