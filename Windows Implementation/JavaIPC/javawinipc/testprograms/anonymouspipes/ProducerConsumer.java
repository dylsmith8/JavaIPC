package anonymouspipes;

import java.util.Arrays;

import windowsipc.AnonymousPipe;
import windowsipc.Pipe;

public class ProducerConsumer {
    final static int SIZE = 2;
    final static Object sync = new Object();
    
    public static void main(String[] args) {
        AnonymousPipe ap;             
        Pipe pipe;        
        
        try {
            ap = new AnonymousPipe(SIZE);
            pipe = ap.create();
            
            Thread producerThread = new Thread(new ProducerThread(ap, pipe.getReadHandle(), pipe.getWriteHandle()));
            Thread consumerThread = new Thread(new ConsumerThread(ap, pipe.getReadHandle()));
            
            producerThread.start();
            consumerThread.start();

            producerThread.join();
            consumerThread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static class ProducerThread implements Runnable {
        AnonymousPipe _ap;
        private long _readHandle;
        private long _writeHandle;
        
        public ProducerThread(AnonymousPipe ap, long readHandle, long writeHandle) {
            this._ap = ap;
            this._readHandle = readHandle;
            this._writeHandle = writeHandle;
        }
        
        @Override
        public void run() {
            try {
                produce();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        private void produce() throws Exception { 
            byte value = 0x01;
            while(true) {
                synchronized (sync) {
                    while (_ap.peek(this._readHandle) >= SIZE) {
                        System.out.println("Full...waiting for consumer");
                        sync.wait();
                    }
                    
                    System.out.println("Producing...");
                    _ap.writePipe(this._writeHandle, new byte[] {value});
                    
                    value = (byte)(value + 1);
                    
                    sync.notify();
                    Thread.sleep(1000);
                }
            }
        }
    }
    
    private static class ConsumerThread implements Runnable {
        AnonymousPipe _ap;
        private long _readHandle;
        
        public ConsumerThread(AnonymousPipe ap, long readHandle) {
            this._ap = ap;
            this._readHandle = readHandle;
        }
        
        @Override
        public void run() {
            try {
                consume();
            } catch (Exception e) {
                e.printStackTrace();
            }            
        }
        
        private void consume() throws Exception { 
            while(true) {
                synchronized (sync) {
                    while (_ap.peek(this._readHandle) == 0) {
                        System.out.println("Nothing to consume...waiting for producer");
                        sync.wait();
                    }
                    
                    System.out.println("Consuming...");
                    byte[] read = _ap.read(this._readHandle);
                    System.out.println("Consumed: " + Arrays.toString(read));                   
                    
                    sync.notify();
                    Thread.sleep(1000);
                }
            }
        }
    }
}