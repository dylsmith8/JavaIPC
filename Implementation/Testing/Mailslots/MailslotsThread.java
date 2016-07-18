/*
 Author: Dylan Smith
 Date: 16 July 2016
 Test mailslots using Java's standard IO mechanisms
*/

 import java.io.*;
 public class MailslotsThread {
 	public static void main (String[] args) {	
		WindowsIPC winIPC = new WindowsIPC();
		final String MAILSLOT_NAME = "\\\\.\\mailslot\\javaMailslot";

		// create the client thread 
		Thread t = new Thread(new MailslotThread(MAILSLOT_NAME));
	    t.start();

	    // create the mailslot 
    	String x = winIPC.createMailslot(MAILSLOT_NAME);
		System.out.println("This is the message in Java: " + x);
 	}

 	private static class MailslotThread implements Runnable {
 		private String mailslotName;
 		public MailslotThread (String mailslotName) {
 			this.mailslotName = mailslotName;
 		}
 		
 		public void run() {
 			try {
 				// creates a client and deposits a message into the slot
				PrintWriter pw = new PrintWriter (new FileOutputStream (mailslotName));
		    	pw.println("hello mailslot");
		    	System.out.println("Wrote to mailslot ok");
		    	pw.close();
 			} catch (IOException e) {
 				e.printStackTrace();
 			}
 		}
 	}
 }