/*
	Author: Dylan Smith
	Date: 28 June 2016

	Test program that tests named pipes
*/
public class PipeRead {
	public static void main (String[] args) {

		WindowsIPC winIPC = new WindowsIPC();
		byte [] data = null;
		data = winIPC.createNamedPipeServer("\\\\.\\Pipe\\JavaPipe");
		for (int i =0 ; i < data.length; i++) {
 			System.out.println("Message @ elem " + i + ": " + data[i]);
		}

		System.out.print("got here");
	}
}
