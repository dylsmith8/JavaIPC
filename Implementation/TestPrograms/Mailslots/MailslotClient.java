/*
  Author: Dylan Smith
  Date: 29 June 2016

  Simple class to test mailslots
*/

public class MailslotClient {
  public static void main (String[] args) {
    WindowsIPC winIPC = new WindowsIPC();
    byte [] data = new byte[40000];
    long time = System.nanoTime();
    if (winIPC.connectToMailslot(data) == 0) {
      System.out.println("Java: Mailslot message dumped successfully");
      System.out.println("Time to send message: "+ ((System.nanoTime() - time))+ "ns");
    }
    else
      System.out.println("Java: Mailslot message dump failed");
  } // main
} // class mailslot server
