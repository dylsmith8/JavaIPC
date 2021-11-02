package anonymouspipes;

import testutils.TestHelper;
import testutils.Timer;
import windowsipc.AnonymousPipe;
import windowsipc.Pipe;

public class AnonymousPipesTest {
    public static void main(String[] args) {
        final int BUFFER_SIZE = 50000;
        AnonymousPipe ap;             
        Pipe pipe;
        
        try {
            ap = new AnonymousPipe(BUFFER_SIZE);
            pipe = ap.create();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        
        long readHandle = pipe.getReadHandle();
        long writeHandle = pipe.getWriteHandle();
        
        if (readHandle > 0 && writeHandle > 0) {
            byte[] testData = TestHelper.getTestData(425);
            Thread t = new Thread(new AnonymousPipeClientThread(ap, writeHandle, testData));
                       
            t.start();
            
            try {
                t.join();
                
                int inPipe = ap.peek(readHandle);
                System.out.println("Bytes in pipe: " + inPipe);
                
                ap.closeWriteHandle(writeHandle);
                
                byte[] readData = (byte[])Timer.timeReturn(() -> {
                    try {
                        return ap.read(readHandle);                       
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }, "anonymous pipe read");
                
                ap.closeReadHandle(readHandle);
                boolean result = TestHelper.compareBytes(testData, readData);          
                
                String response = result ? "Success" : "Failed";
                System.out.println("Anonymous pipe result: " + response);           
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private static class AnonymousPipeClientThread implements Runnable {
        AnonymousPipe _ap;
        private long _writeHandle;
        private byte[] _data;
        
        public AnonymousPipeClientThread(AnonymousPipe ap, long writeHandle, byte[] data) {
            this._ap = ap;
            this._writeHandle = writeHandle;
            this._data = data;
        }
        
        @Override
        public void run() {
            Timer.timeReturn(() -> {
                try {
                    return _ap.writePipe(_writeHandle, _data);                       
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }, "anonymous pipe write");
        }    
    }    
}