/*
  Author: Dylan Smith
  Date: 7 July 2016

  Simple class to test memmory mapped files
*/

public class MemMapProc2 {
  public static void main (String[] args) {
    WindowsIPC winIPC = new WindowsIPC();

    long time = System.nanoTime();
    String x = winIPC.openFileMapping();
    System.out.println("Time to send message: "+ ((System.nanoTime() - time))+ "ns");
    System.out.println("Message in Java: " + x);
  }
}
