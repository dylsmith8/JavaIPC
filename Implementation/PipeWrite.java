public class PipeWrite {
	public static void main (String[] args) {
		WindowsIPC winIPC = new WindowsIPC();
		if (winIPC.createNamedPipeServer("\\\\.\\Pipe\\JavaPipe") == 0) {
			System.out.println("Server created");
		}
		else System.out.println("Error creating server");
	}
}