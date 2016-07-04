/*
  Author: Dylan Smith
  Date: 4 July 2016

  This class implements a Java socket server program
  and is adapted from http://www.tutorialspoint.com/java/java_networking.htm
*/
import java.net.*;
import java.io.*;
public class JavaSocketsServer {
  public static void main(String[] args) {

    final int PORT = 5060;
    try {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Waiting for client to connect on port " + serverSocket.getLocalPort() + "...");

        Socket server = serverSocket.accept();

        DataInputStream in = new DataInputStream(server.getInputStream());
        String x = in.readUTF();
        System.out.println(x);

        DataOutputStream out = new DataOutputStream(server.getOutputStream());
        out.writeUTF("Echo: " + x);
        server.close();
    } catch(IOException e) {
        e.printStackTrace();
      }
  } // main
} // class
