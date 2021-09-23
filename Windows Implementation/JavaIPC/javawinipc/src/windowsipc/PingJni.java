package windowsipc;

public class PingJni {
	static {
	    System.loadLibrary("libjavawinipc_native");
	  }
	
	public native String ping(String pong);
}