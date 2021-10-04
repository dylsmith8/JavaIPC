package windowsipc;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class JavaSocket implements Closeable {
    private Selector _selector;
    private ServerSocketChannel _serverSocketChannel;
    
    public void initServer(String host, int port) throws IOException {
        _selector = Selector.open();
        _serverSocketChannel = ServerSocketChannel.open();
        InetSocketAddress inetAddress = new InetSocketAddress(host, port);
        _serverSocketChannel.bind(inetAddress);
        _serverSocketChannel.configureBlocking(false);
        
        int ops = _serverSocketChannel.validOps();
        _serverSocketChannel.register(_selector, ops, null);
    }
    
    public byte[] read(int bufferSize) throws IOException {
        if (bufferSize < 0)
            throw new IOException("Invalid buffer size");
        
        while (true) {
            _selector.select();
            
            Set<SelectionKey> keys = _selector.selectedKeys();
            Iterator<SelectionKey> iterator = keys.iterator();
            
            while (iterator.hasNext()) {
                SelectionKey currentKey = iterator.next();
                
                if (currentKey.isAcceptable()) {
                    SocketChannel clientChannel = _serverSocketChannel.accept();
                    clientChannel.configureBlocking(false);
                    clientChannel.register(_selector, SelectionKey.OP_READ);                
                } else if (currentKey.isReadable()) {
                    SocketChannel clientChannel = (SocketChannel)currentKey.channel();
                    
                    ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
                    clientChannel.read(buffer);
                    
                    byte[] raw = new byte[buffer.remaining()];
                    buffer.get(raw);
                    
                    return raw;
                }
                
                iterator.remove();
            }            
        }
    }
    
    public void write(String host, int port, byte[] data) throws IOException {
        InetSocketAddress inetAddress = new InetSocketAddress(host, port);
        SocketChannel writeClient = SocketChannel.open(inetAddress);
        
        ByteBuffer buffer = ByteBuffer.wrap(data);
        writeClient.write(buffer);
        
        writeClient.close();
    }

    @Override
    public void close() throws IOException {
        if (_serverSocketChannel != null && _serverSocketChannel.isOpen()) {
            _serverSocketChannel.close();
            _serverSocketChannel = null;            
        }
    }
}