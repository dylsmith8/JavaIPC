package windowsipc;

public class Mailslot {
    static {
        System.loadLibrary("libjavawinipc_native");
    }

    private String _name;
    private int _bufferSize;

    private native long create(String name, int bufferSize) throws Exception;
    private native void write(String name, byte[] data) throws Exception;
    private native byte[] read(String name, long handle, int bufferSize) throws Exception;
    private native void remove(long handle) throws Exception;
    
    public Mailslot(String name, int bufferSize) throws Exception {
        if (bufferSize <= 0)
            throw new Exception("The buffer's size should be a positive integer");
        
        if (name == null || name.equals(""))
            throw new Exception("A name must be specified");

        this._name = name;
        this._bufferSize = bufferSize;
    }
  
    public long init() throws Exception {
        return create(this._name, this._bufferSize);  
    }

    // the `write` method is actually optional as Java can of course write directly to std out
    // which is what a mailslot is, therefore you can avoid the native call
    public void write(byte[] data) throws Exception {
        if (data == null || data.length == 0)
            throw new Exception("Cannot write nothing into the mailslot");
        
        write(this._name, data);
    }

    public byte[] read(long mailslotHandle) throws Exception {
        if (mailslotHandle <= 0)
            throw new Exception("Invalid handle");
        
        return read(this._name, mailslotHandle, this._bufferSize);
    }

    public void removeSlot(long mailslotHandle) throws Exception {
        if (this._name == null)
            throw new Exception("Cannot remove mailslot. Name not configured");
        
        // handle should be an unsigned long..
        if (mailslotHandle <= 0)
            throw new Exception("Invalid handle");
        
        remove(mailslotHandle);		
    }
}