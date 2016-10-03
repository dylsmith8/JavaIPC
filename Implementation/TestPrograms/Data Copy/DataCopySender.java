/*
  Author: Dylan Smith
  Date: 18 July 2016

  Simple class that sends a string message using Windowss DATACOPY
*/
public class DataCopySender {
  public static void main (String[] args) {
    WindowsIPC winIPC = new WindowsIPC();
    byte[] data = new byte[40000];

    for (int i = 0; i < data.length; i++) data[i] = 0x02;

    long time = System.nanoTime();
    int x = winIPC.sendDataCopyMessage(data);
    long timeTaken = System.nanoTime() - time;

    if (x != 0) System.out.println("Sent message correctly");
    else System.out.println("Failed to send message");

    System.out.println("Time to send message: "+ timeTaken + " ns");
  } // main
} // class
