/*
  Author: Dylan Smith
  Date: 18 July 2016

  Simple class that sends a string message using Windowss DATACOPY
*/
public class DataCopyReceiver {
  public static void main (String [] args) {
    WindowsIPC winIPC = new WindowsIPC();
    String data = "";
    // get the message here
    System.out.println("Creating message only-window...");
    data = winIPC.createDataCopyWindow();
    System.out.println(data);
  } // main
} // class
