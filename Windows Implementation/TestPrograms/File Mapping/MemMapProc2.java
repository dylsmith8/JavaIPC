/*
  Author: Dylan Smith
  Date: 7 July 2016

  Simple class to test memmory mapped files
  fetches the contents of shared memory
*/

public class MemMapProc2 {
  public static void main (String[] args) {
    WindowsIPC winIPC = new WindowsIPC();
    byte [] data = null;
    long time = System.nanoTime();
    data = winIPC.openFileMapping();
    System.out.println("Time to get message from file mapping: "+ ((System.nanoTime() - time))+ "ns");
    for (int i = 0; i < data.length; i++) {
        System.out.println("Message @ elem " + i + ": " + data[i]);
    }
    
  }
}
