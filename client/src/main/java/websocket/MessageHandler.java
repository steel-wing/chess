package websocket;

import webSocketMessages.serverMessages.ServerMessage;

public interface MessageHandler {
    void notify(ServerMessage message);
}
