/*
  Author: Dylan Smith
  Date: 18 July 2016

  Simple class that sends a string message using Windowss DATACOPY
*/
public class DataCopySender {
  public static void main (String[] args) {
    WindowsIPC winIPC = new WindowsIPC();
    byte [] data = null;
    // get the message here
    long time = System.nanoTime();
    data = winIPC.createDataCopyWindow();
   
    for (int i = 0; i < data.length; i++) {
        System.out.println("Message @ elem " + i + ": " + data[i]);
    }

  } // main
} // class
