package windowsipc;

public class AnonymousPipe {
    static {
        System.loadLibrary("libjavawinipc_native");
    }
    
    private int _bufferSize;
    
    private native Pipe create(int bufferSize) throws Exception;
    private native boolean write(long handle, byte[] data) throws Exception;
    private native byte[] read(long handle, int bufferSize) throws Exception;
    private native void closeHandle(long handle) throws Exception;
    private native int peek(long handle, int bufferSize);
    
    public AnonymousPipe(int bufferSize) throws Exception {
        if (bufferSize <= 0)
            throw new Exception("The buffer's size should be a positive integer");
        
        this._bufferSize  = bufferSize;
    }
    
    public Pipe create() throws Exception {
        return create(_bufferSize);
    }
    
    public boolean writePipe(long writeHandle, byte[] data) throws Exception {
        if (data == null || data.length == 0)
            throw new Exception("Cannot write nothing into the pipe");
        
        return write(writeHandle, data);
    }
    
    public byte[] read(long handle) throws Exception {
        if (handle <= 0)
            throw new Exception("Invalid handle");
        
        return read(handle, _bufferSize);
    }
    
    public int peek(long handle) {
        return peek(handle, _bufferSize);
    }
    
    public void closeReadHandle(long handle) throws Exception {
        closeHandle(handle);
    }
    
    public void closeWriteHandle(long handle) throws Exception {
        closeHandle(handle);
    }
}