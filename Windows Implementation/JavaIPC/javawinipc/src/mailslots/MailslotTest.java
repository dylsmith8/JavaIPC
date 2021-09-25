package mailslots;
import windowsipc.Mailslot;
import tests.TestData;

public class MailslotTest {
	public static void main(String[] args) {
		final String MAILSLOT_NAME = "\\\\.\\mailslot\\javaMailslot";
		final int BUFFER_SIZE = 50000;
		Mailslot slot = new Mailslot(MAILSLOT_NAME, BUFFER_SIZE);
		long slotHandle;
		
		try {
			slotHandle = slot.init();
		} 
		catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		if (slotHandle > 0) {
			TestData testDataHelper = new TestData();
			byte[] testData = testDataHelper.getTestData();
			
			Thread t = new Thread(new MailslotClientThread(testData, slot));
			t.start();
			
			try {
				t.join();
				
				byte[] readData = slot.read(slotHandle);
				boolean result = testDataHelper.compareBytes(testData, readData);
				
				slot.removeSlot(slotHandle);
				
				System.out.println("Mailslot result: " + result);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}			
	}
	
	private static class MailslotClientThread implements Runnable {
		private byte[] data;
		private Mailslot slot;
		
		public MailslotClientThread(byte[] data, Mailslot slot) {
			this.data = data;
			this.slot = slot;
		}

		@Override
		public void run() {
			try {
				slot.write(data);				
			}
			catch (Exception e) {
				e.printStackTrace();
			}									
		}		
	}
}