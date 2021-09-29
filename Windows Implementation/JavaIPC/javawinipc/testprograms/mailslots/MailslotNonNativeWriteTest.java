package mailslots;

import testutils.TestHelper;
import testutils.Timer;
import windowsipc.Mailslot;
import java.io.*;

public class MailslotNonNativeWriteTest {
    public static void main(String[] args) {
        final String MAILSLOT_NAME = "\\\\.\\mailslot\\javaMailslot";
        final int BUFFER_SIZE = 50000;
        Mailslot slot;
        long slotHandle;
        
        try {
            slot = new Mailslot(MAILSLOT_NAME, BUFFER_SIZE);
        } 
        catch (Exception e) {
            e.printStackTrace();
            return;
        }
        
        slotHandle = (long)Timer.timeReturn(() -> {
            try {
                return slot.init();
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }, "mailslot initialise");	
        
        if (slotHandle > 0) {
            byte[] testData = TestHelper.getTestData(425);
            
            Thread t = new Thread(new MailslotClientThread(MAILSLOT_NAME, testData));
            t.start();
            
            try {
                t.join();
                
                byte[] readData = (byte[])Timer.timeReturn(() -> {
                    try {
                        return slot.read(slotHandle);						
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }, "mailslot read");
                
                boolean result = TestHelper.compareBytes(testData, readData);
                
                slot.removeSlot(slotHandle);
                
                String response = result ? "Success" : "Failed";
                System.out.println("Mailslot with non-native write result: " + response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private static class MailslotClientThread implements Runnable {
        private String name;
        private byte[] data;
        
        public MailslotClientThread(String name, byte[] data) {
            this.name = name;
            this.data = data;
        }
        
        @Override
        public void run() {
            try {
                PrintWriter pw = new PrintWriter(new FileOutputStream(this.name));
                
                Timer.timeVoid(() -> {
                    pw.println(data);
                }, "non-native mailslot write");
                
                pw.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }									
        }		
    }
}
