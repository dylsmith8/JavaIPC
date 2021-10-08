package winipc.junit.tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import windowsipc.JavaSocket;
import testutils.TestHelper;

public class JavaSocketsTest {
    private final int BYTE_SIZE = 424;
    private final String LOCALHOST = "127.0.0.1";
    private final int PORT = 4000;
    
    private void init_happy() {
        try {
            JavaSocket socket = new JavaSocket();
            socket.initServer(LOCALHOST, PORT);
            socket.close();
        } catch (Exception e) {
            fail("Should not have failed. See stacktrace");
        }        
    }
    
    @SuppressWarnings("resource")
    private void init_validation() {
        try {
            new JavaSocket().initServer(LOCALHOST, -1);;
            fail("Should have failed with an exception");                        
        } catch (Exception e) {
            assertEquals("Invalid port", e.getMessage());
        }
        
        try {
            new JavaSocket().initServer(null, PORT);;
            fail("Should have failed with an exception");                        
        } catch (Exception e) {
            assertEquals("Invalid host", e.getMessage());
        }        
    }
    
    private void read_happy() {
        try {
            JavaSocket socket = new JavaSocket();
            socket.initServer(LOCALHOST, 4000);
            
            byte[] testData = TestHelper.getTestData(BYTE_SIZE);            
            socket.write(LOCALHOST, PORT, testData);
            byte[] readData = socket.read(BYTE_SIZE);
            assertNotNull(readData);
            assertEquals(BYTE_SIZE, readData.length);
            assertTrue(TestHelper.compareBytes(testData, readData));
            
            socket.close();
        } catch (Exception e) {
            fail("Should not have failed. See stacktrace");
        }        
    }
    
    private void read_non_existent_socket() {
        try {
            JavaSocket socket = new JavaSocket();
            socket.initServer(LOCALHOST, 4000);
            socket.close();
            byte[] read = socket.read(BYTE_SIZE);
            assertNull(read);            
        } catch (Exception e) {
            e.printStackTrace();
            fail("Should not have failed. See stacktrace");            
        }        
    }
    
    @SuppressWarnings("resource")
    private void read_validation() {
        try {
            new JavaSocket().read(-1);
            fail("Should have failed with an exception");        
        } catch (Exception e) {
            assertEquals("Invalid buffer size", e.getMessage());
        }
    }
    
    private void write_happy() {
        // essentially an identical test to reading
        read_happy();
    }
    
    @SuppressWarnings("resource")
    private void write_non_existent_socket() {
        try {
            new JavaSocket().write(LOCALHOST, PORT, new byte[] {0x01});
        } catch (Exception e) {
            e.printStackTrace();
            fail("Should not have failed. See stacktrace");            
        }              
    }
    
    @SuppressWarnings("resource")
    private void write_validation() {
        try {
            new JavaSocket().write(null, PORT, new byte[] {0x01});
            fail("Should have failed with an exception");
        } catch (Exception e) {
            assertEquals("Invalid host", e.getMessage());
        }
        
        try {
            new JavaSocket().write(LOCALHOST, -1, new byte[] {0x01});
            fail("Should have failed with an exception");
        } catch (Exception e) {
            assertEquals("Invalid port", e.getMessage());
        }      
    }
    
    @Test
    public void javasocket_testInit() {
        init_validation();
        init_happy();
    }    

    @Test
    public void javasocket_testRead() {
        read_validation();
        read_non_existent_socket();
        read_happy();
    }    
    
    @Test
    public void javasocket_testWrite() {
        write_validation();
        write_non_existent_socket();
        write_happy();
    }
}
