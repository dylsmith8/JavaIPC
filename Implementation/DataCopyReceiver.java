/*
  Author: Dylan Smith
  Date: 18 July 2016

  Simple class that sends a string message using Windowss DATACOPY
*/
public class DataCopyReceiver {
  public static void main (String [] args) {
    WindowsIPC winIPC = new WindowsIPC();
    byte[] data = new byte[40];
    for (int i = 0; i < data.length; i++) data[i] = 0x02;
    if (winIPC.sendDataCopyMessage(data) == 0)
      System.out.println("Sent message successflly");
    else
      System.out.println("Failed to send message");
  } // main
} // class
