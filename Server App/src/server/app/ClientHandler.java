package server.app;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler extends Thread {

    private Socket socket;
    private String LOGIN_PREFIX = "LOGIN ";
    private String LOGOUT_PREFIX = "LOGOUT ";
    private static final String MESSAGE_PREFIX = "MSG ";
    private DataOutputStream dataOutputStream;
    private String nickname;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());

            // 1. read a login command
            String command = dataInputStream.readUTF();

            if (!command.startsWith(LOGIN_PREFIX)) {
                socket.close();
                return;
            }
            nickname = command.substring(LOGIN_PREFIX.length());

            // - Broadcase the command to others
            broadcastToOthers(command);

            // - Send other names to the client
            String nicknames = "";
            for (ClientHandler clientHandler : ServerApp.clientHandlers) {
                if (clientHandler != this) {
                    nicknames = nicknames + clientHandler.getNickname() + ",";
                }
            }
            System.out.println("OLD NICKNAMES " + nicknames);
            // remove tailing comma
            if (nicknames.length() > 0) {
                nicknames = nicknames.substring(0, nicknames.length() - 1);
                send(LOGIN_PREFIX + nicknames);
            }

            // 2. wait for message commands
            while (true) {
                command = dataInputStream.readUTF();
                if (command.startsWith(MESSAGE_PREFIX)) {
                    broadcastToOthers(command);
                }
            }


        } catch (IOException ex) {
            // connection lost
            broadcastToOthers(LOGOUT_PREFIX+nickname);
            
            ServerApp.clientHandlers.remove(this);
        }
    }

    public void send(String command) {
        try {
            dataOutputStream.writeUTF(command);
            dataOutputStream.flush();
        } catch (Exception e) {
        }
    }

    private void broadcastToOthers(String command) {
        for (ClientHandler clientHandler : ServerApp.clientHandlers) {
            if (clientHandler != this) {
                clientHandler.send(command);
            }
        }
    }

    public String getNickname() {
        return nickname;
    }
}