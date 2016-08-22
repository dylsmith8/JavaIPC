/*
  Author: Dylan Smith
  Date: 30 June 2016

  Simple class to test Windows Sockets 2.
  This is the server class
*/

public class WinsockServer {
  public static void main (String[] args) {
    WindowsIPC winIPC = new WindowsIPC();
    byte[] data = null;
    data = winIPC.openWinsock();
    System.out.println(data[0]);
    for (int i = 0; i < data.length; i++) {
        System.out.println("Message @ elem " + i + ": " + data[i]);    
    }
  }
}
