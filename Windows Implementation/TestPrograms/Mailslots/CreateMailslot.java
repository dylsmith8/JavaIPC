/*
    Author: Dylan Smith 
    Date: 31 August 2016
*/
public class CreateMailslot {
    public static void main (String [] args) {
       WindowsIPC winIPC = new WindowsIPC();
	   final String MAILSLOT_NAME = "\\\\.\\mailslot\\javaMailslot";
       
       // create the mailslot
        byte[] data = winIPC.createMailslot(MAILSLOT_NAME);
		
        for (int i =0; i < data.length; i++) {
            System.out.println("Message at elem " + i + ": " + data[i]);
        }
    }
}