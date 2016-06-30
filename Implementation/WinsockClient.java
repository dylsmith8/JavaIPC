/*
  Author: Dylan Smith
  Date: 30 June 2016

  Simple class to test Windows Sockets 2.
  This is the client class
*/

public class WinsockClient {
  public static void main (String[] args) {
    WindowsIPC winIPC = new WindowsIPC();

    if (winIPC.createWinsockClient() == 0)
      System.out.println("Java: Client connected correctly");
    else
      System.out.println("Java: Client connection failed");
  } // main
} // class
