package mailslots;
import testutils.TestHelper;
import windowsipc.Mailslot;
import testutils.Timer;

public class MailslotTest {
    public static void main(String[] args) {
        final String MAILSLOT_NAME = "\\\\.\\mailslot\\javaMailslot";
        final int BUFFER_SIZE = 50000;
        Mailslot slot;
        long slotHandle;
        
        try {
            slot = new Mailslot(MAILSLOT_NAME, BUFFER_SIZE);
        } catch (Exception e) {
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
            
            Thread t = new Thread(new MailslotClientThread(testData, slot));
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
                
                // don't bother timing the remove - don't think valuable
                slot.removeSlot(slotHandle);
                
                String response = result ? "Success" : "Failed";
                System.out.println("Mailslot result: " + response);
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
            Timer.timeVoid(() -> {
                try {
                    slot.write(data);
                } catch (Exception e) {
                    e.printStackTrace();
                }				
            }, "mailslot write");								
        }		
    }
}