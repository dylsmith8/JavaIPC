package winipc.junit.tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import windowsipc.Mailslot;
import testutils.TestHelper;
import java.util.UUID;

public class MailslotTest {
	@Test
	public void mailslot_testInit() {
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
	
	@Test
	public void mailslot_testWrite() {
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
		
		// writing to handle that's been closed
		try {
			uuid = UUID.randomUUID();
			
			Mailslot validSlot = new Mailslot("\\\\.\\mailslot\\javaUnitTestMailslot" + uuid.toString(), 5000);
			long handle = validSlot.init();
			
			validSlot.removeSlot(handle);
			
			validSlot.write(TestHelper.getTestData());
			fail("Should have failed with an exception");
		} catch (Exception e) {
			e.printStackTrace();
			// windows error 0x06
		}
		
		// happy
		try {
			uuid = UUID.randomUUID();
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
	
	@Test
	public void mailslot_testRead() {
		UUID uuid;
		
		// reading from a closed handle
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
		
		// reads when no data present
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
		
		// happy 
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
	public void mailslot_testRemoveSlot() {
		System.out.println("hellow");
	}
}