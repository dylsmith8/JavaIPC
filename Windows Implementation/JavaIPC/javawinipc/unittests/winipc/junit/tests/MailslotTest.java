package winipc.junit.tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import windowsipc.Mailslot;
import testutils.TestHelper;
import java.util.UUID;

public class MailslotTest {
    private final int BYTE_SIZE = 424
            ;
    private Mailslot getMailslot() throws Exception {
        UUID uuid = UUID.randomUUID();
        return new Mailslot("\\\\.\\mailslot\\javaUnitTestMailslot" + uuid.toString(), 5000);
    }
    
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
            Mailslot validSlot = getMailslot();
            
            long handle = validSlot.init();
            assertTrue(handle >= 0);
            
            validSlot.removeSlot(handle);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Should not have failed. See stacktrace.");
        }    
    }

    private void write_validation() {
        try {
            Mailslot validSlot = getMailslot();
            validSlot.init();
            
            validSlot.write(null);
            fail("Should have failed with an exception");
        } catch (Exception e) {
            assertEquals("Cannot write nothing into the mailslot", e.getMessage());
        }
    }
    
    private void write_closed_handle() {
        try {
            Mailslot validSlot = getMailslot();
            long handle = validSlot.init();
            
            validSlot.removeSlot(handle);
            
            validSlot.write(TestHelper.getTestData(BYTE_SIZE));
            fail("Should have failed with an exception");
        } catch (Exception e) {
            e.printStackTrace();
            // windows error 0x06
        }
    }
    
    private void write_happy() {
        try {
            Mailslot validSlot = getMailslot();
            long handle = validSlot.init();
            assertTrue(handle >= 0);
            
            validSlot.write(TestHelper.getTestData(BYTE_SIZE));
            validSlot.removeSlot(handle);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail("Should not have failed. See stacktrace.");
        }
    }
    
    private void read_closed_handle() {
        try {
            Mailslot validSlot = getMailslot();
            long handle = validSlot.init();
            assertTrue(handle >= 0);
            
            byte[] testData = TestHelper.getTestData(BYTE_SIZE);
            
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
        try {
            Mailslot validSlot = getMailslot();
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
        try {
            Mailslot validSlot = getMailslot();
            long handle = validSlot.init();
            assertTrue(handle >= 0);
            
            byte[] testData = TestHelper.getTestData(BYTE_SIZE);
            
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

    private void remove_validation() {
        try {
            Mailslot validSlot = getMailslot();
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
            Mailslot validSlot = getMailslot();
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
            Mailslot validSlot = getMailslot();
            long handle = validSlot.init();
            assertTrue(handle >= 0);
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
}