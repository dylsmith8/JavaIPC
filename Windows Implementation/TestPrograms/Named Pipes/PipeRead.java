/*
	Author: Dylan Smith
	Date: 28 June 2016

	Test program that tests named pipes
*/
public class PipeRead {
	public static void main (String[] args) {

		WindowsIPC winIPC = new WindowsIPC();
		final String PIPE_NAME = "\\\\.\\Pipe\\JavaPipe";
		
		int pipeHandle = winIPC.createNamedPipeServer(PIPE_NAME);

		System.out.print("Pipe Handle Value: " + pipeHandle);
	}
}
