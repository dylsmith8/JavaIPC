package windowsipc;

public class Mailslot {
	static {
	    System.loadLibrary("libjavawinipc_native");
	 }
	
	private String name;
	private int bufferSize;

	private native long create(String name, int bufferSize) throws Exception;
	private native void write(String name, byte[] data) throws Exception;
	private native byte[] read(String name, long handle, int bufferSize) throws Exception;
	private native void remove(long handle) throws Exception;
	  
	public Mailslot(String name, int bufferSize) {
		this.name = name;
	    this.bufferSize = bufferSize;
	}
  
	public long init() throws Exception {
	    return create(this.name, this.bufferSize);  
	}
	
	// the write method is actually optional as Java can of course write directly to std out
	// which is what a mailslot is
	public void write(byte[] data) throws Exception {
		write(this.name, data);
	}

	public byte[] read(long mailslotHandle) throws Exception {
		if (name == null)
			throw new Exception("Mailslot handle does not exist. Please create the mailslot first before attempting to read.");
		
		return read(this.name, mailslotHandle, this.bufferSize);
	}
	
	public void removeSlot(long handle) throws Exception {
		remove(handle);		
	}
}