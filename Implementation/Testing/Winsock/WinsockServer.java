/*
  Author: Dylan Smith
  Date: 30 June 2016

  Simple class to test Windows Sockets 2.
  This is the server class
*/

public class WinsockServer {
  public static void main (String[] args) {
    WindowsIPC winIPC = new WindowsIPC();

    String x = winIPC.openWinsock();
    System.out.println("Message in Java: " + x);
  }
}
