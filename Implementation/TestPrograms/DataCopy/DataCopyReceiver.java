/*
  Author: Dylan Smith
  Date: 18 July 2016

  Simple class that sends a string message using Windowss DATACOPY
*/
public class DataCopyReceiver {
  public static void main (String [] args) {
    WindowsIPC winIPC = new WindowsIPC();
    if (winIPC.sendDataCopyMessage("awCGvx8YTc9HCgdovcDWawCGvx8YTc9HCgdovcDW") == 0)
      System.out.println("Got data copy message successfully");
    else
      System.out.println("Failed to get data copy message");
  } // main
} // class
