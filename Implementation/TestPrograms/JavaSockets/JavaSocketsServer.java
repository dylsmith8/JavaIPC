/*
  Author: Dylan Smith
  Date: 4 July 2016
  Modified: 9 September 2016

  This class implements a Java socket server program
  and is adapted from http://www.tutorialspoint.com/java/java_networking.htm
*/
import java.net.*;
import java.io.*;
public class JavaSocketsServer {
  public static void main(String[] args) {
    final int PORT = 5060;
    WindowsIPC winIPC = new WindowsIPC();
    System.out.println("Creating server...waiting for message");
    byte [] data = winIPC.createJavaSocketServer(PORT);
    for (int i = 0; i < data.length; i++)
      System.out.println("Message received via Java Sockets at elem " + i + ": " + data[i]);
  } // main
} // class
