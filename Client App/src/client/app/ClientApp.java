package client.app;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

public class ClientApp {

    private static final String LOGIN_PREFIX = "LOGIN ";
    private static final String LOGOUT_PREFIX = "LOGOUT ";
    public static final String MESSAGE_PREFIX = "MSG ";
    private static DataOutputStream dataOutputStream;
    public static String nickname;

    public static void main(String[] args) throws Exception {

        try {
            UIManager.setLookAndFeel(new NimbusLookAndFeel());
        } catch (Exception e) {
        }
        // show the nickname dialog
        nickname = JOptionPane.showInputDialog("Enter nickname");
        if (nickname == null) {
            return;
        }
        
        String ip = JOptionPane.showInputDialog("Enter ip","localhost");
        if (ip == null) {
            return;
        }

        try {
            // establish a connection to the server
            Socket socket = new Socket(ip, 1234);
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());

            // send login command
            send(LOGIN_PREFIX + nickname);
            // show main frame
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
            mainFrame.onUserLogined(nickname);

            // wait for all incoming messags from the server
            while (true) {
                String command = dataInputStream.readUTF();
                if (command.startsWith(LOGIN_PREFIX)) { // login command
                    String nicknamesString = command.substring(LOGIN_PREFIX.length());
                    String[] nicknames = nicknamesString.split(",");
                    for (String name : nicknames) {
                        mainFrame.onUserLogined(name);
                    }
                } else if (command.startsWith(MESSAGE_PREFIX)) { // login command
                    String newMessage = command.substring(MESSAGE_PREFIX.length());
                    mainFrame.onMessageRecieved(newMessage);
                } else if (command.startsWith(LOGOUT_PREFIX)) { // logout command
                    String nickname = command.substring(LOGOUT_PREFIX.length());
                    mainFrame.onUserLoggedOut(nickname);
                }
            }
        } catch (Exception e) {
        }


    }

    public static void send(String command) {
        try {
            dataOutputStream.writeUTF(command);
            dataOutputStream.flush();
        } catch (Exception e) {
        }
    }
}