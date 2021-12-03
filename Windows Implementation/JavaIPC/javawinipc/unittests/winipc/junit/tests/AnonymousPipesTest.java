package winipc.junit.tests;
import windowsipc.AnonymousPipe;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import windowsipc.Pipe;

public class AnonymousPipesTest {
    private void init_validation() {        
        try {
            new AnonymousPipe(-1);
            fail("Should have failed with an exception");
        } catch (Exception e) {
            assertEquals("The buffer's size should be a positive integer", e.getMessage());            
        }
    }
    
    private void create_happy() {
        AnonymousPipe ap;             
        Pipe pipe;
        try {
            ap = new AnonymousPipe(1024);
            pipe = ap.create();
            assertNotNull(pipe);
            assertTrue(pipe.getReadHandle() > 0);
            assertTrue(pipe.getWriteHandle() > 0);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail("Should not have failed. See stacktrace.");
        }
    }
    
    private void write_validation() {
        AnonymousPipe ap;             
        Pipe pipe;
        
        try {
            ap = new AnonymousPipe(1024);
            pipe = ap.create();
            assertNotNull(pipe);
            assertTrue(pipe.getReadHandle() > 0);
            assertTrue(pipe.getWriteHandle() > 0);
            
            ap.writePipe(-2, new byte[] {0x01});
            fail("Should have failed with an exception");
            
        } catch (Exception e) {
            assertEquals("Invalid handle", e.getMessage());
        }
        
        try {
            ap = new AnonymousPipe(1024);
            pipe = ap.create();
            assertNotNull(pipe);
            assertTrue(pipe.getReadHandle() > 0);
            assertTrue(pipe.getWriteHandle() > 0);
            
            ap.writePipe(1, null);
            fail("Should have failed with an exception");
            
        } catch (Exception e) {
            assertEquals("Cannot write nothing into the pipe", e.getMessage());
        }
    }
    
    private void write_happy() {
        AnonymousPipe ap;             
        Pipe pipe;
        
        try {
            ap = new AnonymousPipe(1024);
            pipe = ap.create();
            assertNotNull(pipe);
            assertTrue(pipe.getReadHandle() > 0);
            assertTrue(pipe.getWriteHandle() > 0);
            
            Boolean result = ap.writePipe(pipe.getWriteHandle(), new byte[] {0x01});
            assertTrue(result);            
        } catch (Exception e) {
            e.printStackTrace();
            fail("Should not have failed. See stacktrace.");
        }
    }
    
    private void read_validation() {
        AnonymousPipe ap;             
        Pipe pipe;
        try {
            ap = new AnonymousPipe(1024);
            pipe = ap.create();
            assertNotNull(pipe);
            assertTrue(pipe.getReadHandle() > 0);
            assertTrue(pipe.getWriteHandle() > 0);
            
            ap.read(-1);
            fail("Should have failed with an exception");
            
        } catch (Exception e) {
            assertEquals("Invalid handle", e.getMessage());     
        }
    }
    
    private void read_happy() {
        AnonymousPipe ap;             
        Pipe pipe;
        try {
            ap = new AnonymousPipe(1024);
            pipe = ap.create();
            assertNotNull(pipe);
            assertTrue(pipe.getReadHandle() > 0);
            assertTrue(pipe.getWriteHandle() > 0);
            
            Boolean result = ap.writePipe(pipe.getWriteHandle(), new byte[] {0x01});
            assertTrue(result);
            
            byte[] dataRead = ap.read(pipe.getReadHandle());
            assertNotNull(dataRead);
            assertTrue(dataRead.length != 0);
            assertTrue(dataRead[0] == 0x01);     
        } catch (Exception e) {
            e.printStackTrace();
            fail("Should not have failed. See stacktrace.");   
        }
    }
    
    private void peek_validation() {
        AnonymousPipe ap;             
        Pipe pipe;
        try {
            ap = new AnonymousPipe(1024);
            pipe = ap.create();
            assertNotNull(pipe);
            assertTrue(pipe.getReadHandle() > 0);
            assertTrue(pipe.getWriteHandle() > 0);
            
            ap.peek(-1);
            fail("Should have failed with an exception");
            
        } catch (Exception e) {
            assertEquals("Invalid handle", e.getMessage());     
        }
    }
    
    private void peek_happy() {
        AnonymousPipe ap;             
        Pipe pipe;
        try {
            ap = new AnonymousPipe(1024);
            pipe = ap.create();
            assertNotNull(pipe);
            assertTrue(pipe.getReadHandle() > 0);
            assertTrue(pipe.getWriteHandle() > 0);
            
            Boolean result = ap.writePipe(pipe.getWriteHandle(), new byte[] {0x01, 0x02});
            assertTrue(result);
            
            int bytesAvailable = ap.peek(pipe.getReadHandle());
            assertEquals(2, bytesAvailable);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Should not have failed. See stacktrace.");   
        }
    }
    
    private void closeHandle_validation() {
        AnonymousPipe ap;             
        try {
            ap = new AnonymousPipe(1024);
            ap.closeReadHandle(-1);
            fail("Should have failed with an exception");
        } catch (Exception e) {
            assertEquals("Invalid handle", e.getMessage());  
        }
    }
    
    @Test
    public void anonpipe_init() {
        init_validation();
    }
    
    @Test
    public void anonpipe_create() {
        create_happy();
    }
        
    @Test
    public void anonpipe_write() {
        write_validation();
        write_happy();
    }    
  
    @Test
    public void anonpipe_read() {
        read_validation();
        read_happy();
    }
    
    @Test
    public void anonpipe_peek() {
        peek_validation();
        peek_happy();
    }
    
    @Test
    public void anonpipe_closeHandles() {
        closeHandle_validation();
    }
}
