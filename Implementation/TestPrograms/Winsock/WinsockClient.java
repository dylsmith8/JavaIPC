/*
  Author: Dylan Smith
  Date: 30 June 2016

  Simple class to test Windows Sockets 2.
  This is the client class
*/

public class WinsockClient {
  public static void main (String[] args) {
    WindowsIPC winIPC = new WindowsIPC();
    byte [] data = new byte[40000];
    for (int i = 0; i < data.length; i++) data[i] = 0x02;
    long time = System.nanoTime();
    int x = winIPC.createWinsockClient(data);
    long timeTaken = System.nanoTime() - time;
    System.out.println("Time to send message: "+ timeTaken + " ns");

    if (x == 0) System.out.println("Java: Client connected correctly");
    else System.out.println("Java: Client connection failed");
  } // main
} // class
