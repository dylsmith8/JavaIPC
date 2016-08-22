/*
  Author: Dylan Smith
  Date: 7 July 2016

  Simple class to test memmory mapped files
  Memory maps a byte array
*/

public class MemMapProc1 {
  public static void main (String[] args) {
    WindowsIPC winIPC = new WindowsIPC();
    byte [] data = new byte[40000];
    data[0] = 0x01;
    if (winIPC.createFileMapping(data) == 0)
      System.out.println("File mapping of byte array successfully created");
    else
      System.out.println("File mapping failed");
  } // main
} // class

