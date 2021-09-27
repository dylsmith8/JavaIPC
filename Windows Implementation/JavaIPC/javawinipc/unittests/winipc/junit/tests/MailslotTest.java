package winipc.junit.tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import windowsipc.Mailslot;
import testutils.TestHelper;
import java.util.UUID;

public class MailslotTest {	
	private void init_validation() {		
		try {
			new Mailslot("random name", -1);
			fail("Should have failed with an exception");						
		} catch (Exception e) {
			assertEquals("The buffer's size should be a positive integer", e.getMessage());
		}
		
		try {
			new Mailslot(null, 5000);
			fail("Should have failed with an exception");						
		} catch (Exception e) {
			assertEquals("A name must be specified", e.getMessage());
		}
	}
	
	private void init_happy() {
		try {
			UUID uuid = UUID.randomUUID();
			Mailslot validSlot = new Mailslot("\\\\.\\mailslot\\javaUnitTestMailslot" + uuid.toString(), 5000);
			
			long handle = validSlot.init();
			assertTrue(handle >= 0);
			
			validSlot.removeSlot(handle);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Should not have failed. See stacktrace.");
		}	
	}

	private void write_validation() {
		UUID uuid;
		
		// validation
		try {
			uuid = UUID.randomUUID();
			Mailslot validSlot = new Mailslot("\\\\.\\mailslot\\javaUnitTestMailslot" + uuid.toString(), 5000);
			validSlot.init();
			
			validSlot.write(null);
			fail("Should have failed with an exception");
		} catch (Exception e) {
			assertEquals("Cannot write nothing into the mailslot", e.getMessage());
		}
	}
	
	private void write_closed_handle() {
		try {
			UUID uuid = UUID.randomUUID();
			
			Mailslot validSlot = new Mailslot("\\\\.\\mailslot\\javaUnitTestMailslot" + uuid.toString(), 5000);
			long handle = validSlot.init();
			
			validSlot.removeSlot(handle);
			
			validSlot.write(TestHelper.getTestData());
			fail("Should have failed with an exception");
		} catch (Exception e) {
			e.printStackTrace();
			// windows error 0x06
		}
	}
	
	private void write_happy() {
		// happy
		try {
			UUID uuid = UUID.randomUUID();
			Mailslot validSlot = new Mailslot("\\\\.\\mailslot\\javaUnitTestMailslot" + uuid.toString(), 5000);
			long handle = validSlot.init();
			assertTrue(handle >= 0);
			
			validSlot.write(TestHelper.getTestData());
			validSlot.removeSlot(handle);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Should not have failed. See stacktrace.");
		}
	}
	
	private void read_closed_handle() {
		UUID uuid;
		try {
			uuid = UUID.randomUUID();
			Mailslot validSlot = new Mailslot("\\\\.\\mailslot\\javaUnitTestMailslot" + uuid.toString(), 5000);
			long handle = validSlot.init();
			assertTrue(handle >= 0);
			
			byte[] testData = TestHelper.getTestData();
			
			validSlot.write(testData);
			validSlot.removeSlot(handle);
			
			validSlot.read(handle);			
			fail("Should have failed with an exception");				
		} catch (Exception e) {
			e.printStackTrace();
			// windows error 0x06			
		}
	}
	
	private void read_data_not_present() {
		UUID uuid;
		try {
			uuid = UUID.randomUUID();
			Mailslot validSlot = new Mailslot("\\\\.\\mailslot\\javaUnitTestMailslot" + uuid.toString(), 5000);
			long handle = validSlot.init();
			assertTrue(handle >= 0);
			
			byte[] data = validSlot.read(handle);
			assertNull(data);		
		} catch (Exception e) {
			e.printStackTrace();
			fail("Should not have failed. See stacktrace.");
		}
	}
	
	private void read_happy() {
		UUID uuid;
		try {
			uuid = UUID.randomUUID();
			Mailslot validSlot = new Mailslot("\\\\.\\mailslot\\javaUnitTestMailslot" + uuid.toString(), 5000);
			long handle = validSlot.init();
			assertTrue(handle >= 0);
			
			byte[] testData = TestHelper.getTestData();
			
			validSlot.write(testData);
						
			byte[] read = validSlot.read(handle);
			boolean result = TestHelper.compareBytes(testData, read);
			assertTrue(result);
			
			validSlot.removeSlot(handle);
			
		} catch (Exception e) {
			e.printStackTrace();
			fail("Should not have failed. See stacktrace.");
		}
	}
		
	@Test
	public void mailslot_testInit() {
		init_validation();
		init_happy();
	}
	
	@Test
	public void mailslot_testWrite() {
		write_validation();
		write_closed_handle();		
		write_happy();
	}
		
	@Test
	public void mailslot_testRead() {
		read_closed_handle();
		read_data_not_present();
		read_happy();
	}

	@Test
	public void mailslot_testRemoveSlot() {
		remove_validation();
		remove_handle_does_not_exist();
		remove_happy();
	}
	private void remove_validation() {
		try {
			UUID uuid = UUID.randomUUID();
			Mailslot validSlot = new Mailslot("\\\\.\\mailslot\\javaUnitTestMailslot" + uuid.toString(), 5000);
			long handle = validSlot.init();
			validSlot.removeSlot(handle);
			validSlot.removeSlot(-218);
			fail("Should have failed with an exception");
		} catch (Exception e) {
			assertEquals("Invalid handle", e.getMessage());			
		}		
	}
	
	private void remove_handle_does_not_exist() {
		try {
			UUID uuid = UUID.randomUUID();
			Mailslot validSlot = new Mailslot("\\\\.\\mailslot\\javaUnitTestMailslot" + uuid.toString(), 5000);
			long handle = validSlot.init();
			assertTrue(handle >= 0);
			validSlot.removeSlot(handle);
			
			validSlot.removeSlot(12345);
			fail("Should have failed with an exception");
		} catch (Exception e) {
			e.printStackTrace();
			// 0x06
		}		
	}
	
	private void remove_happy() {
		try {
			UUID uuid = UUID.randomUUID();
			Mailslot validSlot = new Mailslot("\\\\.\\mailslot\\javaUnitTestMailslot" + uuid.toString(), 5000);
			long handle = validSlot.init();
			assertTrue(handle >= 0);
			validSlot.removeSlot(handle);			
		} catch (Exception e) {
			e.printStackTrace();
			fail("Should not have failed. See stacktrace.");			
		}	
	}
}