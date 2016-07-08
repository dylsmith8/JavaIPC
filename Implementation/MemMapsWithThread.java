/*
  Author: Dylan Smith
  Date: 7 July 2016

  Class that tests Windows Memory Mapped Files using a threading mechanism
*/
public class MemMapsWithThread {
  public static void main (String[] args) {

    WindowsIPC winIPC = new WindowsIPC();

    Thread t = new Thread(new MemMapThread());
    t.start();

    if (winIPC.createFileMapping("awCGvx8YTc9HCgdovcDWawCGvx8YTc9HCgdovcDW") == 0)
      System.out.println("File mapping successfully created");
    else
      System.out.println("File mapping failed");

  } // main

  private static class MemMapThread implements Runnable {
    WindowsIPC winIPC = new WindowsIPC();

    public void run() {
      long time = System.nanoTime();
      String x = winIPC.openFileMapping();
      System.out.println("Time to send message: "+ ((System.nanoTime() - time))+ "ns");
      System.out.println("Message in Java: " + x);
    }
  }
} // class
