/*
	Author: Dylan Smith
	Date: 19 August 2016
	Last Modified: 19 October 2016
*/
public class TestSem {
	public static void main (String[] args) {

		WindowsIPC winIPC = new WindowsIPC();
		final String SEM_NAME = "mySem";
		final int INIT_COUNT = 1;
		final int MAX_COUNT = 1;

		int x = winIPC.createSemaphore(SEM_NAME, INIT_COUNT, MAX_COUNT);

		if (x != -1) {
			// create some threads...
			for (int i = 0; i < 3; i++) {
		        Thread t = new Thread(new Task(SEM_NAME));
		        t.start();
	    }
		} else System.out.println("Windows semaphore failed to create");
	}

	public static void printStuff() {
		for (int i = 0; i < 5; i++) System.out.println("Some values..." + i);
	}

	private static class Task implements Runnable {
		String semName;
		WindowsIPC winIPC = new WindowsIPC();
		public Task(String semName) {
			this.semName = semName;
		}

	  public void run() {
			int y = winIPC.openSemaphore(semName);
			int z = winIPC.waitForSingleObject(y);

			if (z == 0) {
				printStuff();
				int a = winIPC.releaseSemaphore(y, 1);
				if (a == 0) System.out.println("Semaphore released successfully");
				else System.out.println("Semaphore release failed");
			}
			else {
			 //print some error
			 System.out.println("An error occured..");
			}
		}
  }
}
