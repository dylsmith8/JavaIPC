/*
  Author: Dylan Smith
  Date: 18 July 2016

  Simple class that sends a string message using Windowss DATACOPY
*/
public class DataCopySender {
  public static void main (String[] args) {
    WindowsIPC winIPC = new WindowsIPC();

    if (winIPC.createDataCopyWindow() == 0)
      System.out.println("Data copy successful");
    else
      System.out.println("Data copy failed");
  } // main
} // class
