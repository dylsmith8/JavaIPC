1. Try use Java IO to access mailslots instead of JNI calls --> DONE
2. Make the programs fail gracefully if the WindowIPC.dll isn't available
  - Investigate how to create custom exceptions
3. In WindowsIPC, in `createJavaSocketServer`, need to add some error checking
  to ensure that a valid port number is entered and not a privileged port (i.e mustn't be < 1024)
4. Code refactoring
5. Record timings of writing to a named pipe using Java's IO
	- Bug where it doesn't get the message correctly when trying to output it
6. Change method signatures to `byte` to correctly send varying message sizes across 
