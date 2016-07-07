/*
  Author: Dylan Smith
  Date: 7 July 2016

  Simple class to test memmory mapped files
*/

public class MemMapProc2 {
  public static void main (String[] args) {
    WindowsIPC winIPC = new WindowsIPC();

    long time = System.nanoTime();
    if (winIPC.openFileMapping() == 0) {
      System.out.println("File mapping OPENED");
      System.out.println("Time to send message: "+ ((System.nanoTime() - time))+ "ns");
    }
    else
      System.out.println("File mapping open failed");
  }
}
