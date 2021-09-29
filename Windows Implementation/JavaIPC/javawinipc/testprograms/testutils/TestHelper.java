package testutils;
import java.util.Random;

public class TestHelper {
    public static byte[] getTestData(int size) {
        byte[] bytes = new byte[size];
        new Random().nextBytes(bytes);
        
        return bytes;
    }

    public static boolean compareBytes(byte[] x, byte[] y) {
        if (x.length != y.length)
            return true;
        
        int delta = 0;
        for(int i = 0; i < x.length; i++)
            delta |= x[i] ^ y[i];
        
        return delta == 0;
        }	
}