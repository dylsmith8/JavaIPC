/*
	Author: Dylan Smith
	Date: 28 June 2016

	Test program that tests named pipes
*/
public class PipeWrite {
	public static void main (String[] args) {

		WindowsIPC winIPC = new WindowsIPC();

		// create a 'server' -- a client will then connect and write a message
			 // which is stored in x
		String x = winIPC.createNamedPipeServer("\\\\.\\Pipe\\JavaPipe");
		System.out.println("In java" + x);
	}
}
