/*
 Author: Dylan Smith
 Date: 16 July 2016
 Test mailslots using Java's standard IO mechanisms
 NOTE: delibrately fails
*/

 import java.io.*;
 public class MailslotsThread {
 	public static void main (String[] args) {
		WindowsIPC winIPC = new WindowsIPC();
		final String MAILSLOT_NAME = "\\\\.\\mailslot\\javaMailslot";

		// create the client thread
		Thread t = new Thread(new MailslotThread(MAILSLOT_NAME));
	    t.start();
 	}

 	private static class MailslotThread implements Runnable {
 		private String mailslotName;
 		public MailslotThread (String mailslotName) {
 			this.mailslotName = mailslotName;
 		}

 		public void run() {
 			try {
 				// creates a client and deposits a message into the slot
        byte [] data = new byte[40000];
        long time = System.nanoTime();
			  PrintWriter pw = new PrintWriter (new FileOutputStream (mailslotName));
        System.out.println("Time to send message: "+ ((System.nanoTime() - time))+ "ns");
	    	pw.println(data);
	    	System.out.println("Wrote to mailslot ok");
	    	pw.close();
 			} catch (IOException e) {
 				e.printStackTrace();
 			}
 		}
 	}
 }
