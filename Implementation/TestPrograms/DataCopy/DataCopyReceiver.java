/*
  Author: Dylan Smith
  Date: 18 July 2016

  Simple class that sends a string message using Windowss DATACOPY
*/
public class DataCopyReceiver {
  public static void main (String [] args) {
    WindowsIPC winIPC = new WindowsIPC();
    byte[] data = new byte[40000];
    if (winIPC.sendDataCopyMessage(data) == 0)
      System.out.println("Got data copy message successfully");
    else
      System.out.println("Failed to get data copy message");
  } // main
} // class
