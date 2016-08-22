/*
	Author: Dylan Smith
	Date: 28 June 2016

	Test program that tests named pipes
*/
public class PipeRead {
	public static void main (String[] args) {
		WindowsIPC winIPC = new WindowsIPC();
		byte [] data = new byte[40];
        for (int i = 0; i < data.length; i++) data[i] = 0x02;
		// create a client that sends a message along an existing named pipe
		long time = System.nanoTime();
		if (winIPC.createNamedPipeClient(data) == 0)
			System.out.println("client created successfully");
		else
			System.out.println("Error creating client - no pipe to connect to");

		System.out.println("Time to send message: "+ ((System.nanoTime() - time))+ "ns");
	}
}
