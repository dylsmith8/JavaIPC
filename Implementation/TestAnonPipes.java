/*
	Author: Dylan Smith
	Date: 2 September 2016
*/
public class TestAnonPipes {
	public static void main (String[] args) {
		WindowsIPC winIPC = new WindowsIPC();

		byte [] data = new byte[40000];
		for (int i = 0; i < data.length; i++) data[i] = 0x02;

		long time = System.nanoTime();
		int pipeHandle = winIPC.createAnonPipe(data);
		long timeTaken = System.nanoTime() - time;
		System.out.println("Time taken to create the pipe: " + timeTaken);
		if (pipeHandle != - 1) {
			Thread t = new Thread(new AnonPipeThread(pipeHandle));
			t.start();
		}
		else System.out.println("An error occurred");
	}

	private static class AnonPipeThread implements Runnable {
		private int handle;
		private byte [] data = null;
		WindowsIPC winIPC = new WindowsIPC();
		public AnonPipeThread(int handle) {
			this.handle = handle;
		}

		public void run() {
			long time = System.nanoTime();
			data = winIPC.getAnonPipeMessage(handle);
			long timeTaken = System.nanoTime() - time;
			for (int i = 0; i < data.length; i++) System.out.println("Data at " + i + ": " + data[i]);
			System.out.println("Anonymous Pipe successfully read");
			System.out.println("Time taken to fetch message: " + timeTaken + " ns");
		}
	}
}
