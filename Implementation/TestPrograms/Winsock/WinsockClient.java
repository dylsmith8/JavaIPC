/*
  Author: Dylan Smith
  Date: 30 June 2016

  Simple class to test Windows Sockets 2.
  This is the client class
*/

public class WinsockClient {
  public static void main (String[] args) {
    WindowsIPC winIPC = new WindowsIPC();

    long time = System.nanoTime();
    if (winIPC.createWinsockClient("awCGvx8YTc9HCgdovcDWawCGvx8YTc9HCgdovcDW") == 0)
      System.out.println("Java: Client connected correctly");
    else
      System.out.println("Java: Client connection failed");
    System.out.println("Time to send message: "+ ((System.nanoTime() - time))+ "ns");
  } // main
} // class