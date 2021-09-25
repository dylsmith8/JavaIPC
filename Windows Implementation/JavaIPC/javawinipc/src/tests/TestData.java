package tests;
import java.util.Random;

public class TestData {
	public byte[] getTestData() {
		byte[] bytes = new byte[20];
		new Random().nextBytes(bytes);
		
		return bytes;
	}
	
	public boolean compareBytes(byte[] x, byte[] y) {
		if (x.length != y.length)
			return true;
		
		int delta = 0;
		for(int i = 0; i < x.length; i++)
			delta |= x[i] ^ y[i];
		
		return delta == 0;
	}
}