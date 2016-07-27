/*
  Author: Dylan Smith
  Date: 30 June 2016

  Simple class to test Java sockets using a thread to act as the client
*/

public class JavaSocketsWithThread {
  public static void main (String[] args) {
    WindowsIPC winIPC = new WindowsIPC();
    final int PORT = 5060;
    Thread t = new Thread (new JavaSocketsThread());
    t.start();

    String x = winIPC.createJavaSocketServer(PORT);
    System.out.println("Message in Java: " + x);
  } // main

  private static class JavaSocketsThread implements Runnable {
    WindowsIPC winIPC = new WindowsIPC();
    byte[] data = new byte[40000];
    public void run() {
      long time = System.nanoTime();
      // write a 40 byte message
      if (winIPC.createJavaSocketClient("127.0.0.1", 5060, data) == 0)
        System.out.println("Client connected and sent message successfully");
      else
        System.out.println("An error occured whilst connecting");
      System.out.println("Time to send message: "+ ((System.nanoTime() - time))+ "ns");
    } // run
  } // JavaSocketsThread
} // class
