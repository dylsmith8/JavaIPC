/*
  Author: Dylan Smith
  Date: 29 June 2016

  Simple class to test mailslots
  WRITES TO MAILSLOT USING JNI
*/

public class MailslotClient {
  public static void main (String[] args) {
    WindowsIPC winIPC = new WindowsIPC();
    byte [] data = new byte[40000];
    for (int i = 0; i < data.length; i++) data[i] = 0x02;
    long time = System.nanoTime();
    int x = winIPC.connectToMailslot(data);
    long y =  ((System.nanoTime() - time));
    System.out.println("Time to send message: "+ y + "ns");
  } // main
} // class mailslot server
