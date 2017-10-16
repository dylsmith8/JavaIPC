/*
    Author: Dylan Smith
    Date: 16 October 2017
*/
import java.io.*;
import java.net.*;

public class JavaSockets {

    public static byte[] createJavaSocketServer(int portNumber) {

        // not initialised 
        if (portNumber == 0) {
            throw new IllegalArgumentException("Invalid port."); 
        }

        byte[] messageRcv = null;
        try {
            ServerSocket serverSocket = new ServerSocket(portNumber);
            Socket server = serverSocket.accept();
            DataInputStream in = new DataInputStream(server.getInputStream());

            int length = in.readInt();
            messageRcv = new byte[length];

            if (length > 0) {
                in.readFully(messageRcv, 0, messageRcv.length);
            }

            server.close();
        } 
        catch(IOException e) {
            e.printStackTrace();
        }

        return messageRcv;
    }

    public static void createJavaSocketClient(String host, int port, byte[] message) {

        if (Helpers.isNullOrEmpty(host)) {
            throw new IllegalArgumentException("Invalid host name.");
        }

        if (port == 0) {
            throw new IllegalArgumentException("Invalid port number.");
        }

        if (message == null || message.length == 0) {
            throw new IllegalArgumentException("Valid data not specified.");
        }

        try {

            Socket client = new Socket(host, port);

            OutputStream outToServer = client.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToServer);
            out.writeInt(message.length);
            out.write(message);

            client.close();

        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}