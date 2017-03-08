/*
  Author: Dylan Smith
  Date: 7 July 2016
  Modified: 17 August 2016

  Class that tests Windows Memory Mapped Files using a threading mechanism
  Tests that they are synchronised correctly
*/
public class MemMapsWithThread {
  public static void main (String[] args) {

    WindowsIPC winIPC = new WindowsIPC();

    for (int i = 0; i < 100; i++) {
        Thread t = new Thread(new MemMapThread());
        t.start();
    }

    final byte[]data = new byte[40];
    if (winIPC.createFileMapping(data) == 0)
      System.out.println("File mapping successfully created");
    else
      System.out.println("File mapping failed");

  } // main

  private static class MemMapThread implements Runnable {
    WindowsIPC winIPC = new WindowsIPC();

    public void run() {
      long time = System.nanoTime();
      byte[] x = winIPC.openFileMapping();
      long timeTaken = System.nanoTime() - time;
      for (int i = 0; i < x.length; i++) System.out.println("Message in Java " + x[i]);
      System.out.println("Time to send message: "+ timeTaken + "ns");
    }
  }
} // class
