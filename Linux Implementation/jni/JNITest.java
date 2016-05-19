//Filename: JNITest.java

public class JNITest
  {
    public native void whatNow (byte[] b);

    static
      {
        System.loadLibrary("JNITest");
      } // static block

    public static void main( String args[])
      { byte[] b = {1, 2, 3, 4, 5, 6, 7, 8};
      
        new JNITest().whatNow(b);
      } // main
  } // class JNITest
