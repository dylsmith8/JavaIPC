import java.util.*;
import java.io.*;

/** Uses named pipes for communication, and nanosecond timing.
  */
public class BPingPongFIFO
  { private static LinuxIPC ipc = new LinuxIPC();
    private static final String PING2PONG = "/tmp/fifo_ping2pong";
    private static final String PONG2PING = "/tmp/fifo_pong2ping";
    private String myName;
    private String yourName;
    private ObjectOutputStream outStream;
    private ObjectInputStream inStream;
    private long total = 0;
    private static long startSetup;
    private long endSetup;
    private static int ARRAY_SIZE = 40000;
    private ArrayList<Long> results;
    private void printResults(String name)
    {
        //calculate the total
        double total = 0;
        for (Long d: results) total += d;
        
        //calculate the mean
        double mean = total/results.size();
        
        //calculate the sample variance
        double variance = 0;
        for (Long d: results) variance += (d-mean)*(d-mean);
        variance /= (results.size()-1);
        
        //calculate the standard deviation
        double stdev = Math.sqrt(variance);
        
        //calculate the standard error
        double stderror = stdev/Math.sqrt(results.size());
        
        System.out.printf("Mean time %.2f +- %.2f ns using %s\n",mean,stderror,name);
        
    }
    public BPingPongFIFO (String myName) throws IOException
      { this.myName = myName;
        
        System.out.println(myName + " starting initialisation.");
        if (myName.equals("Ping"))
          { yourName = "Pong";
            inStream = new ObjectInputStream(new FileInputStream(PONG2PING));
            //System.out.println(myName + " inStream created.");
            outStream = new ObjectOutputStream(new FileOutputStream(PING2PONG));
            outStream.flush();
            //System.out.println(myName + " outStream created.");
          }
        else
          { yourName = "Ping";
            outStream = new ObjectOutputStream(new FileOutputStream(PONG2PING));
            outStream.flush();
            //System.out.println(myName + " outStream created.");
            inStream = new ObjectInputStream(new FileInputStream(PING2PONG));
            //System.out.println(myName + " inStream created.");
          }
       // System.out.println(myName + " initialisation complete.");
      } // constructor

    public static void main(String[] args)
      { startSetup = System.currentTimeMillis();
        String myName = args[0];
        int rounds = Integer.parseInt(args[1]);
        if (args.length == 3)
          ARRAY_SIZE = Integer.parseInt(args[2]);

        if (ipc.mkfifo(PING2PONG, 0660) == 0)
          {}//System.out.println("mkfifo succeeded for ping2pong");
        else
          {}//System.out.println("mkfifo failed: errnum = " + ipc.getErrnum() + " " + ipc.strerror(ipc.getErrnum()));
        if (ipc.mkfifo(PONG2PING, 0660) == 0)
          {}//System.out.println("mkfifo succeeded for pong2ping");
        else
          {}//System.out.println("mkfifo failed: errnum = " + ipc.getErrnum() + " " + ipc.strerror(ipc.getErrnum()));

        try
          { new BPingPongFIFO(myName).play(rounds);
          }
        catch (Exception exc)
          { System.err.println("I/O Error: " + exc);
            exc.printStackTrace();
          }
      } // main

    private void play (int rounds) throws IOException, ClassNotFoundException
      { endSetup = System.currentTimeMillis();
        //System.out.println(myName + " starting rounds.");
        results = new ArrayList<Long>(rounds);
        for (int i = 0; i < rounds; i++)
          { if (myName.equals("Ping"))
              { // Ping throws then catches
                throwDateBall(i);
                catchBall(i);
              }
            else
              { // Pong catches then throws
                PingPongBall d = catchDateBall(i);
                throwBall(d, i);
              }
          }
        if (myName.equals("Ping")) // Report average trip time
          { //System.out.println("Data size: " + ARRAY_SIZE);
            //System.out.println("Average trip time (ns): " + (total/(rounds-1.0)) + " using named pipes");
             printResults("named pipes, payload size="+ARRAY_SIZE);
          }
        //System.out.println("Setup time: " + (endSetup-startSetup));
        try
          { Thread.sleep(1000);
          }
        catch (InterruptedException e)
          { // Ignore
          }
      } // play

    private void throwBall (PingPongBall d, int i) throws IOException
      { // System.out.println(myName + " throwing " + d);
        outStream.writeObject(d);
        outStream.flush();
        // System.out.println(myName + " threw " + i);
      } // throwBall

    private void throwDateBall (int i) throws IOException
      { // System.out.println(myName + " throwing");
        PingPongBall b = new PingPongBall();
        b.data = new byte[ARRAY_SIZE];
        b.time = System.nanoTime();
        outStream.writeObject(b);
        outStream.flush();
        // System.out.println(myName + " threw " + i);
      } // throwBall

    private PingPongBall catchDateBall (int i) throws IOException, ClassNotFoundException
      { PingPongBall d = null;

        // System.out.println(myName + " catching " + i);
        d = (PingPongBall)inStream.readObject();
        // System.out.println(myName + " caught " + i);
        return d;
      } // catchBall

    private void catchBall (int i) throws IOException, ClassNotFoundException
      { PingPongBall d = null;
        long time;

        // System.out.println(myName + " catching " + i);
        d = (PingPongBall)inStream.readObject();
        // System.out.println(myName + " caught " + i);
        time = System.nanoTime()-d.time;
       // System.out.println("Round trip time = " + time);
        if (i > 0)
        {
          total += time;
          results.add(time);
        }
      } // catchBall

  } // class BPingPongFIFO
