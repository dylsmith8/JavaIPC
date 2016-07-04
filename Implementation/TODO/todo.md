1. Try use Java IO to access mailslots instead of JNI calls
2. Make the programs fail gracefully if the WindowIPC.dll isn't available
  - Investigate how to create custom exceptions 
