package windowsipc;

public class WindowsIPC {
	static {
	    System.loadLibrary("libjavawinipc_native");
	  }
	
	public native byte [] createMailslot(String name);
	public native int add(int x, int y);
}