package testutils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import java.util.Random;
import java.util.function.Supplier;

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
    
    public static void expectException(String error, Runnable func) {
        try {
            func.run();
            fail("Should have failed with an exception");
        }
        catch (Exception e) {
            assertEquals(error, e.getMessage());
        }
    }
}