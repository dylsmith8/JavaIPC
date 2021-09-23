package tests;
import windowsipc.*;

public class TestJNI {

	public static void main(String[] args) {
		BaseIpc ipc = new BaseIpc();
		System.out.println(ipc.ping("Pong"));
	}
}
