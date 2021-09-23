package tests;
import windowsipc.*;

public class TestJNI {

	public static void main(String[] args) {
		WindowsIPC ipc = new WindowsIPC();
		System.out.println(ipc.add(1, 1));
	}
}
