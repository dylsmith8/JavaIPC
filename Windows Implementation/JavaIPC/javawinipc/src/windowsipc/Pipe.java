package windowsipc;

public class Pipe {
    private long _readHandle;
    private long _writeHandle;
    
    public Pipe(long readHandle, long writeHandle) {
        _readHandle = readHandle;
        _writeHandle = writeHandle;
    }
    
    public long getReadHandle() {
        return _readHandle;
    }
    
    public long getWriteHandle() {
        return _writeHandle;
    }
}