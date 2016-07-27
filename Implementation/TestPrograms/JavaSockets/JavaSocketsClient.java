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
      WindowsIPC winIPC = new WindowsIPC();
      byte[] data = new byte[40000];
      long time = System.nanoTime();
      if (winIPC.createJavaSocketClient("127.0.0.1", 5060, data) == 0)
        System.out.println("Client connected and sent message successfully");
      else
        System.out.println("An error occured whilst connecting");
      System.out.println("Time to send message: "+ ((System.nanoTime() - time))+ "ns");
      }
  }
