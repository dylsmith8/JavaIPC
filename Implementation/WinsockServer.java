/*
  Author: Dylan Smith
  Date: 30 June 2016

  Simple class to test Windows Sockets 2.
  This is the server class
*/

public class WinsockServer {
  public static void main (String[] args) {
    WindowsIPC winIPC = new WindowsIPC();

    if (winIPC.openWinsock() == 0)
      System.out.println("Java: Winsock Server Created");
    else
      System.out.println("JavaL Winsock Server creation failed");
  }
}
