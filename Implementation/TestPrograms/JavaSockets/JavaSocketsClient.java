/*
  Author: Dylan Smith
  Date: 4 July 2016

  This class implements a Java socket server program
  and is adapted from http://www.tutorialspoint.com/java/java_networking.htm

*/

  import java.io.*;
  import java.net.*;

  public class JavaSocketsClient {
      public static void main(String[] args) throws IOException {

        final String HOST_NAME = "127.0.0.1"; // localhost
        final int PORT = 5060; // arb port number
        byte [] data = new byte[40];
        try {
          System.out.println("Connecting to " + HOST_NAME + " on port " + PORT);
          Socket client = new Socket(HOST_NAME, PORT);

          OutputStream outToServer = client.getOutputStream();
          DataOutputStream out = new DataOutputStream(outToServer);
          out.writeUTF("awCGvx8YTc9HCgdovcDWawCGvx8YTc9HCgdovcDW"); // write a 40 byte message

          InputStream inFromServer = client.getInputStream();
          DataInputStream in = new DataInputStream(inFromServer);

          System.out.println(in.readUTF()); // print server echo
          client.close();

        } catch (IOException e) {
          e.printStackTrace();
        }
      }
  }
