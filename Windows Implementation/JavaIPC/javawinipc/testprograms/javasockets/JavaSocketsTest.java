package javasockets;

import java.io.IOException;
import windowsipc.JavaSocket;
import testutils.*;

public class JavaSocketsTest {
    @SuppressWarnings("resource")
    public static void main(String[] args) {
        final int PORT = 5063;
        final String HOST = "localhost";
        final int BUFFER_SIZE = 425;
        JavaSocket socket;
        
        try {
            socket = new JavaSocket();
            socket.initServer(HOST,PORT);
        } catch (IOException e) {
            e.printStackTrace();
            return;            
        };
        
        byte[] testData = TestHelper.getTestData(425);
        Thread t  = new Thread(new JavaSocketsClientThread(HOST, PORT, testData));
        t.start();
        
        try {
            t.join();
            
            byte[] readData = (byte[])Timer.timeReturn(() -> {
                try {
                    return socket.read(BUFFER_SIZE);                       
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }, "Java socket read");
            
            boolean result = TestHelper.compareBytes(testData, readData);
            socket.close();
            
            String response = result ? "Success" : "Failed";
            System.out.println("Java sockets result: " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }        
    }
    
    private static class JavaSocketsClientThread implements Runnable {
        private String _host;
        private int _port;
        private byte[] _data;
        
        public JavaSocketsClientThread(String host, int port, byte[] data) {
            this._host = host;
            this._port = port;
            this._data = data;
        }
        
        @SuppressWarnings("resource")
        public void run() {
            Timer.timeVoid(() -> {
                try {
                    new JavaSocket().write(_host, _port, _data);
                } catch (Exception e) {
                    e.printStackTrace();
                }               
            }, "Java socket write");   
        }
    }
}