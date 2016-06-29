/*
  Author: Dylan Smith
  Date: 29 June 2016

  Simple class to test mailslots
*/

public class MailslotServer {
  public static void main (String[] args) {
    WindowsIPC winIPC = new WindowsIPC();

    if (winIPC.createMailslot("string") == 0)
      System.out.println("Java: Mailslot created successfully");
    else
      System.out.println("Mailslot creation failed");
  } // main
} // class mailslot server
