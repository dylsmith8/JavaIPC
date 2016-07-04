/*
  Author: Dylan Smith
  Date: 30 June 2016

  Simple class to test Windows Sockets 2 using a thread to act as the client
*/

public class WinsockWithThread {
  public static void main (String[] args) {
    WindowsIPC winIPC = new WindowsIPC();

    Thread t = new Thread(new WinsockThread());
    t.start();

    if (winIPC.openWinsock() == 0)
      System.out.println("Java: Winsock Server Created");
    else
      System.out.println("JavaL Winsock Server creation failed");
  } // main

  private static class WinsockThread implements Runnable {
    WindowsIPC winIPC = new WindowsIPC();

    public void run() {
      long time = System.nanoTime();
      if (winIPC.createWinsockClient("Hello Windows IPC") == 0)
        System.out.println("Java: Client connected correctly");
      else
        System.out.println("Java: Client connection failed");
      System.out.println("Time to send message: "+ ((System.nanoTime() - time))+ "ns");
    } // run
  } // Winsock
} // class
