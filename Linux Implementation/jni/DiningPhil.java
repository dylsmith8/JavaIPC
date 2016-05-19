// DiningPhil.java

import java.io.*;
import java.util.Date;

/** This class implements a simple version of the Dining Philosophers problem using LinuxIPC semaphores.
  * @author George Wells  --  2 December 2008
  */
public class DiningPhil extends Thread
  { private static final int ITERATIONS = 5;
    private static final int THINK_TIME = 2000; // ms
    private static final int EAT_TIME = 1000; // ms
    private static int numPhils;
    
    private LinuxIPC ipc = new LinuxIPC();
    private int number;
    private int semid;
    
    public DiningPhil (int number, int semid)
      { this.number = number;
        this.semid = semid;
      } // DiningPhil
      
    private void getForks ()
      { LinuxIPC.Sembuf[] mealTicket = { new LinuxIPC.Sembuf(0, -1, 0) };
        LinuxIPC.Sembuf[] forks = { new LinuxIPC.Sembuf(number, -1, 0), new LinuxIPC.Sembuf((number % numPhils) + 1, -1, 0) };
        if (ipc.semop(semid, mealTicket) != -1)
          System.out.println("  Philosopher " + number + " got meal ticket.");
        else
          System.out.println("semop wait failed: errnum = " + ipc.getErrnum() + " " + ipc.strerror(ipc.getErrnum()));
        if (ipc.semop(semid, forks) != -1)
          System.out.println("  Philosopher " + number + " got forks.");
        else
          System.out.println("semop wait failed: errnum = " + ipc.getErrnum() + " " + ipc.strerror(ipc.getErrnum()));
      } // getForks
    
    private void putForks ()
      { LinuxIPC.Sembuf[] sb = { new LinuxIPC.Sembuf(0, 1, 0),  new LinuxIPC.Sembuf(number, 1, 0), new LinuxIPC.Sembuf((number % numPhils) + 1, 1, 0) };
        if (ipc.semop(semid, sb) != -1)
          System.out.println("  Philosopher " + number + " returned forks.");
        else
          System.out.println("semop wait failed: errnum = " + ipc.getErrnum() + " " + ipc.strerror(ipc.getErrnum()));
      } // putForks
          private void sleep (int time)
      { try
          { Thread.sleep((long)(Math.random() * time) + 1);
          }
        catch (InterruptedException e)
          { System.out.println("Sleep interrupted: " + e.toString());
          }
      } // sleep
    
    public void run ()
      { int k;
        //waitForStart();
        for (k = 0; k < ITERATIONS; k++)
          { System.out.println("Philospher " + number + " thinking...");
            sleep(THINK_TIME);
            System.out.println("Philospher " + number + " about to eat.");
            getForks();
            System.out.println("Philospher " + number + " eating...");
            sleep(EAT_TIME);
            putForks();
            System.out.println("Philospher " + number + " finished eating.");
          }

        System.out.println("*** Philospher " + number + " completed.");
      } // phil
      
    private static void putOutForks (int numPhils, int semid)
      { LinuxIPC ipc = new LinuxIPC();
        LinuxIPC.Sembuf[] sb = { new LinuxIPC.Sembuf(0, numPhils-1, 0) }; // Entry control
        if (ipc.semop(semid, sb) != -1)
          System.out.println("Set up entry control.");
        else
          System.out.println("Setting up entry control failed: errnum = " + ipc.getErrnum() + " " + ipc.strerror(ipc.getErrnum()));
        for (int k = 0; k < numPhils; k++)
          { sb[0] = new LinuxIPC.Sembuf(k+1, 1, 0);
            if (ipc.semop(semid, sb) != -1)
              System.out.println("Placed fork " + k);
            else
              System.out.println("Placing fork failed: errnum = " + ipc.getErrnum() + " " + ipc.strerror(ipc.getErrnum()));
          }
      } // putOutForks
      
    public static void main (String[] args)
      { if (args.length != 1)
          { System.out.println("ERROR: Usage: java DiningPhil NUMBER_OF_PHILOSOPHERS");
            return;
          }
        numPhils = Integer.parseInt(args[0]);
        int semid;
        int key;
        LinuxIPC ipc = new LinuxIPC();
        if ((key = ipc.ftok("/home/csgw/JavaProgs/JNI", 'p')) != -1)
          System.out.println("ftok succeeded.  key = " + key);
        else
          System.out.println("ftok failed: errnum = " + ipc.getErrnum() + " " + ipc.strerror(ipc.getErrnum()));

        if ((semid = ipc.semget(key, numPhils+1, LinuxIPC.IPC_CREAT | 0660)) != -1)
          { System.out.println("semget succeeded.  sem ID = " + semid);
          }
        else
          { System.out.println("semget failed: errnum = " + ipc.getErrnum() + " " + ipc.strerror(ipc.getErrnum()));
            return;
          }
        DiningPhil[] phil = new DiningPhil[numPhils];
        for (int k = 0; k < numPhils; k++)
	        { phil[k] = new DiningPhil(k+1, semid);
	          phil[k].start();
	        }
        putOutForks(numPhils, semid);
        for (int k = 0; k < numPhils; k++)
	        { try
	            { phil[k].join();
	            }
	          catch (InterruptedException e)
	            {  }
	          System.out.println("Joined Phil " + k);
	        }
	      if (ipc.semRmid(semid) != -1)
          System.out.println("semRmid succeeded.\n");
        else
          System.out.println("semRmid failed: errnum = " + ipc.getErrnum() + " " + ipc.strerror(ipc.getErrnum()));
      } // main
      
  } // class DiningPhil
