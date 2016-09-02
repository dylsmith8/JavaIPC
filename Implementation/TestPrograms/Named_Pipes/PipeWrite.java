/*
	Author: Dylan Smith
	Date: 28 June 2016

	Test program that tests named pipes
*/
public class PipeWrite {
	public static void main (String[] args) {
		WindowsIPC winIPC = new WindowsIPC();
		byte [] data = new byte[40000];
        for (int i = 0; i < data.length; i++) data[i] = 0x02;
		// create a client that sends a message along an existing named pipe
		long time = System.nanoTime();
		int x = winIPC.createNamedPipeClient(data);
        long y = System.nanoTime() - time;
		System.out.println("Time to send message: "+ y + "ns");
	}
}
