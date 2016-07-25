/*
  Author: Dylan Smith
  Date: 18 July 2016

  Simple class that sends a string message using Windowss DATACOPY
*/
public class DataCopySender {
  public static void main (String[] args) {
    WindowsIPC winIPC = new WindowsIPC();

    // get the message here
    long time = System.nanoTime();
    String x = winIPC.createDataCopyWindow();
    System.out.println("Time to send message: "+ ((System.nanoTime() - time))+ "ns");
    System.out.println("Message in Java: " + x);
  } // main
} // class
