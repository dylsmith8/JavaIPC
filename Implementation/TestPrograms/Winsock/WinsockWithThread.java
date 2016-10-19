/*
  Author: Dylan Smith
  Date: 30 June 2016
  Last modified: 19 October 2016

  Simple class to test Windows Sockets 2 using a thread to act as the client
*/

public class WinsockWithThread {
  public static void main (String[] args) {
    WindowsIPC winIPC = new WindowsIPC();

    Thread t = new Thread(new WinsockThread());
    t.start();

    byte [] data = winIPC.openWinsock();
    for (int i = 0; i < data.length; i++)
      System.out.println("Message in Java at element " + i + " :" + data[i]);
  } // main

  private static class WinsockThread implements Runnable {
    WindowsIPC winIPC = new WindowsIPC();

    public void run() {
      byte [] data = new byte[40000];
      for (int i = 0; i < data.length; i++) data[i] = 0x02;
      long time = System.nanoTime();
      int x = winIPC.createWinsockClient(data);
      long timeTaken = System.nanoTime() - time;
      System.out.println("Time to send message: "+ timeTaken + " ns");

      if (x == 0) System.out.println("Java: Client connected correctly");
      else System.out.println("Java: Client connection failed");
    } // run
  } // Winsock
} // class
