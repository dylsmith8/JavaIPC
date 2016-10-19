/*
  Author: Dylan Smith
  Date: 24 August 2016
*/

import java.io.*;
public class TestPipesRead {
  public static void main (String[] args) {
    try {
       final String pipeName = "\\\\.\\Pipe\\JavaPipe";
       BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(pipeName)));
       String line = br.readLine();
       System.out.println("Read from pipe OK: " + line);
       br.close();
     }
     catch (IOException exc) {
         exc.printStackTrace();
     }
  }
}
