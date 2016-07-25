
import java.util.*;
import java.io.*;
import com.steelcode.smof.*;

/** Uses Shared Memory Object Framework
 */
public class BPingPongSMOF
{

    public static class SMOFBall extends SharedObject
    {

        public SharedLong date = new SharedLong();
        public SharedByteBuffer buf;
        public SMOFBall(File key, int arraySize)
        {
            buf = new SharedByteBuffer(arraySize);
            bindSharedFields(key.getAbsolutePath());
        }
    }
    private String myName;
    private String yourName;
    private SMOFBall outBall, inBall;
    private static long startSetup;
    private long endSetup;
    private static int ARRAY_SIZE = 40000;
    private ArrayList<Long> results;

    private void printResults(String name)
    {
        //calculate the total
        long total = 0;
        for (Long d : results)
        {
            total += d;

        }


        //calculate the mean
        long mean = total / results.size();

        int outliers = 0;
        for (Long d : results)
        {
            if (d > 10*mean || d < mean/10)
            {
                outliers++;
           //     System.out.printf("Outlier %d (%d)\n",d,mean);
            }
        }

        //calculate the sample variance
        double variance = 0;
        for (Long d : results)
        {
            variance += (d - mean) * (d - mean);
        }
        variance /= (results.size() - 1);

        //calculate the standard deviation
        double stdev = Math.sqrt(variance);

        //calculate the standard error
        double stderror = stdev / Math.sqrt(results.size());

        System.out.printf("Mean time %d +- %.1f ns using %s [N=%d] o/l: %d \n", mean, stderror, name,results.size(),outliers);

    }

    public BPingPongSMOF(String myName) throws IOException
    {
        this.myName = myName;

        //Create file to use as a key
        File keyfpong = new File("KEY_PONG_FILE_" + ARRAY_SIZE);
        keyfpong.createNewFile();
        File keyfping = new File("KEY_PING_FILE_" + ARRAY_SIZE);
        keyfping.createNewFile();

        System.out.println(myName + " starting initialisation.");
        if (myName.equals("Ping"))
        {
            //Its also ping's job to clean old semaphores.
            //It must start first
            yourName = "Pong";
            inBall = new SMOFBall(keyfpong, ARRAY_SIZE);
            inBall.tryLock(); inBall.releaseLock();

            outBall = new SMOFBall(keyfping, ARRAY_SIZE);
            outBall.tryLock(); outBall.releaseLock();
        }
        else
        {
            yourName = "Ping";
            outBall = new SMOFBall(keyfpong, ARRAY_SIZE);
            inBall = new SMOFBall(keyfping, ARRAY_SIZE);
        }
        //System.out.println(myName + " initialisation complete.");
    } // constructor

    public static void main(String[] args)
    {
        startSetup = System.currentTimeMillis();
        String myName = args[0];
        int rounds = Integer.parseInt(args[1]);
        if (args.length == 3)
        {
            ARRAY_SIZE = Integer.parseInt(args[2]);
        }
        try
        {
            new BPingPongSMOF(myName).play(rounds);
        }
        catch (Exception exc)
        {
            System.err.println("I/O Error: " + exc);
            exc.printStackTrace();
        }
    } // main

    private void play(int rounds) throws IOException, ClassNotFoundException
    {
        //System.out.println(myName + " starting rounds.");
        results = new ArrayList<Long>(rounds);
        long date;
        long now;
        for (int i = 0; i < rounds; i++)
        {
            if (myName.equals("Ping"))
            { // Ping throws then catches
                throwBall(0);
                date = catchBall();
                now = System.nanoTime();
                if (i>0)
                {
                    results.add(now-date);
                }
            }
            else
            { // Pong catches then throws

                long d =  catchBall();
                throwBall(d);
            }
        }
        if (myName.equals("Ping")) // Report average trip time
        {
            printResults("SMOF, payload size=" + ARRAY_SIZE);
        }

    } // play

    private void throwBall(long date) throws IOException
    {
        if (date == 0)
            date = System.nanoTime();

        //If we were doing something with the buffer, we would lock
        //the whole ball, and unlock it when done. Otherwise we can
        //use the default atomic setter
        outBall.date.setValue(date);
        
    } 

    private long catchBall() throws IOException, ClassNotFoundException
    {
        long date;
        while (true)
        {
            inBall.waitForLock();
            //Get the value without locking (we have locked manually)
            date = inBall.date.getValue(false);
            if (date != 0)
            {
                //Here we would do our own processing on buf, knowing that its
                //still locked

                //Flag to future us that data has been consumed
                inBall.date.setValue(0,false);
                inBall.releaseLock();
                return date;
            }
            inBall.releaseLock();
            inBall.yieldProcess(); //Wrapper for sched_yield(2)
        }
    } // catchBall

} // class BPingPongShm1

