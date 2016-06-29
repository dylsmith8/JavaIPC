/*
  Author: Dylan Smith
  Date: 29 June 2016

  Simple class to test mailslots
*/

public class MailslotClient {
  public static void main (String[] args) {
    WindowsIPC winIPC = new WindowsIPC();

    if (winIPC.connectToMailslot("Message dumped in slot") == 0)
      System.out.println("Java: Mailslot message dumped successfully");
    else
      System.out.println("Java: Mailslot message dump failed");
  } // main
} // class mailslot server
