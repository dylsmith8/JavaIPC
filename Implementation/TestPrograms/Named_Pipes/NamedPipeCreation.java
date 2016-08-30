public class NamedPipeCreation {
    public static void main (String [] args) {
        final String PIPE_NAME = "\\\\.\\Pipe\\JavaPipe";
        WindowsIPC winIPC = new WindowsIPC();
        byte [] data = winIPC.createNamedPipeServer(PIPE_NAME);
        
        for (int i = 0; i < data.length; i++) {
            System.out.println("Data at elem " + i + ": " + data[i]);
        }
    }
}