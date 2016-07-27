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
    WindowsIPC winIPC = new WindowsIPC();
    String x = winIPC.createJavaSocketServer(PORT);
    System.out.println("Message in Java: " + x);
  } // main
} // class
