// DiningPhil2.java

import java.io.*;
import java.util.Date;

/** This class implements a simple version of the Dining Philosophers problem using LinuxIPC semaphores.
  * @author George Wells  --  2 December 2008
  */
public class DiningPhil2 extends Thread
  { private static final int ITERATIONS = 5;
    private static final int THINK_TIME = 2000; // ms
    private static final int EAT_TIME = 1000; // ms
    private static int numPhils;
    
    private static LinuxSemaphore mealTickets;
    private static LinuxSemaphore[] fork;

    private int number;
    
    public DiningPhil2 (int number)
      { this.number = number;
      } // DiningPhil
      
    private void getForks ()
      { mealTickets.acquire();
        System.out.println("  Philosopher " + number + " got meal ticket. " + mealTickets);
        fork[number].acquire();
        System.out.println("  Philosopher " + number + " got left fork.");
        fork[(number+1) % numPhils].acquire();
        System.out.println("  Philosopher " + number + " got right fork.");
      } // getForks
    
    private void putForks ()
      { mealTickets.release();
        fork[number].release();
        fork[(number+1) % numPhils].release();
        System.out.println("  Philosopher " + number + " returned forks.");
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
      
    private static void putOutForks (int numPhils)
      { mealTickets = new LinuxSemaphore(numPhils-1);
        fork = new LinuxSemaphore[numPhils];
        for (int k = 0; k < numPhils; k++)
          fork[k] = new LinuxSemaphore(1);
      } // putOutForks
      
    public static void main (String[] args)
      { if (args.length != 1)
          { System.out.println("ERROR: Usage: java DiningPhil2 NUMBER_OF_PHILOSOPHERS");
            return;
          }
        numPhils = Integer.parseInt(args[0]);
        
        putOutForks(numPhils);
        DiningPhil2[] phil = new DiningPhil2[numPhils];
        for (int k = 0; k < numPhils; k++)
	        { phil[k] = new DiningPhil2(k);
	          phil[k].start();
	        }
        for (int k = 0; k < numPhils; k++)
	        { try
	            { phil[k].join();
	            }
	          catch (InterruptedException e)
	            {  }
	          System.out.println("Joined Phil " + k);
	        }
	      mealTickets.close();
	      for (int k = 0; k < numPhils; k++)
	        fork[k].close();
      } // main
      
  } // class DiningPhil2
