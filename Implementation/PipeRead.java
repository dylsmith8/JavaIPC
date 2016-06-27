public class PipeRead {
	public static void main (String[] args) {
		WindowsIPC winIPC = new WindowsIPC();
		if (winIPC.createNamedPipeClient("MESSAGE: HELLO PIPE") == 0) {
			System.out.println("client created successfully");
		}
		else {
			System.out.println("Error creating client");	
		}
	}
}