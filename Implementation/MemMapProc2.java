/*
  Author: Dylan Smith
  Date: 7 July 2016

  Simple class to test memmory mapped files
*/

public class MemMapProc2 {
  public static void main (String[] args) {
    WindowsIPC winIPC = new WindowsIPC();

    if (winIPC.openFileMapping() == 0)
      System.out.println("File mapping OPENED");
    else
      System.out.println("File mapping open failed");
  }
}
