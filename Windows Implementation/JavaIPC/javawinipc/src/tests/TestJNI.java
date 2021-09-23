package tests;
import windowsipc.*;

public class TestJNI {

	public static void main(String[] args) {
		PingJni ipc = new PingJni();
		System.out.println(ipc.ping("Pong"));
	}
}
