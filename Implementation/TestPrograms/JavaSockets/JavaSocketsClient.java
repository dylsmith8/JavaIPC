/*
  Author: Dylan Smith
  Date: 4 July 2016
  Modified: 9 September 2016

  This class implements a Java socket server program
  and is adapted from http://www.tutorialspoint.com/java/java_networking.htm

*/

  import java.io.*;
  import java.net.*;

  public class JavaSocketsClient {
    public static void main(String[] args) throws IOException {
      final String LOCAL_HOST = "127.0.0.1";
      final int PORT = 5060;
      WindowsIPC winIPC = new WindowsIPC();
      byte[] data = new byte[40000];

      for (int i = 0; i < data.length; i++) data[i] = 0x02;

      long time = System.nanoTime();
      int result = winIPC.createJavaSocketClient(LOCAL_HOST, PORT, data);
      long timeTaken = System.nanoTime() - time;

      System.out.println("Time to send message: "+ timeTaken + "ns");
      if (result == -1) System.out.println("An error occured whilst connecting");
      else System.out.println("Client connected and sent message successfully"); 
    }
  }
