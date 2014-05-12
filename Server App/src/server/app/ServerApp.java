package server.app;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerApp {

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();

    public static void main(String[] args) {
        try {
            // listen to a port
            ServerSocket serverSocket = new ServerSocket(1234);
            // loop forever to accept all incoming connections
            while (true) {
                // accept a new incoming connection
                Socket socket = serverSocket.accept();
                // start a new thread to handle the new connection
                ClientHandler clientHandler = new ClientHandler(socket);
                // add the client to an array list
                clientHandlers.add(clientHandler);
                // start the new thread
                clientHandler.start();
            }
        } catch (IOException ex) {
        }
    }
}