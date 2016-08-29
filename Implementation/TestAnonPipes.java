public class TestAnonPipes {
	public static void main (String[] args) {
		WindowsIPC winIPC = new WindowsIPC();

		byte [] data = new byte[40];
		for (int i = 0; i < data.length; i++) data[i] = 0x02;

		int x = winIPC.createAnonPipe(data);
		System.out.println("read handle received in Java: " + x);
		Thread t = new Thread(new MyThread(x));
		t.start();	 
	}

	private static class MyThread implements Runnable {
		private int handle;
		private byte [] data = null;
		WindowsIPC winIPC = new WindowsIPC();
		public MyThread(int handle) {
			this.handle = handle;
		}

		public void run() {
			data = winIPC.getAnonPipeMessage(handle);
			for (int i = 0; i < data.length; i++) System.out.println("Data at " + i + ": " + data[i]);
		}
	}
}