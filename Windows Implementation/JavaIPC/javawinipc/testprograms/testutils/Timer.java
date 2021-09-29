package testutils;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class Timer {
	public static <R> R timeReturn(Supplier<R> toTime, String actionBeingTimed) {
		long start = System.nanoTime();
	    R result  = toTime.get();
	    System.out.printf("Execution for '%s' took %d micro-seconds:\n", actionBeingTimed, TimeUnit.NANOSECONDS.toMicros(System.nanoTime() - start));
	    return result;			
	}
	
	public static void timeVoid(Runnable toTime, String actionBeingTimed) {
		timeReturn(() -> {
			toTime.run();
			return null;
		}, actionBeingTimed);
	}
}
