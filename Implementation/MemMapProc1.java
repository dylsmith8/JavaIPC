/*
  Author: Dylan Smith
  Date: 7 July 2016

  Simple class to test memmory mapped files
*/

public class MemMapProc1 {
  public static void main (String[] args) {
    WindowsIPC winIPC = new WindowsIPC();

    if (winIPC.createFileMapping("awCGvx8YTc9HCgdovcDWawCGvx8YTc9HCgdovcDW") == 0)
      System.out.println("File mapping successfully created");
    else
      System.out.println("File mapping failed");

  } // main
} // class
