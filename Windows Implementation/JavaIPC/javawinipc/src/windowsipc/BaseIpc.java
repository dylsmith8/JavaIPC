package windowsipc;

public class BaseIpc {
    static {
        System.loadLibrary("libjavawinipc_native");
    }
    
    public native String ping(String pong);
    
    public BaseIpc() {}
}