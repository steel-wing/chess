public class SimpleJavaClass {

//    private int x;      // this is an instance variable, since it isn't static
//    private static boolean b;   // because this is static, the function can access it
//
//    // static variables can be accessed
//    static {
//        b = true;
//    }
    public static void main(String[] args) {
        System.out.println("Hello world!");
    }
}

// if you wanted to run this from command line, you'd cd to the folder it's in, then run
// javac .\SimpleJavaClass.java
// to compile it, then run
// java -cp . SimpleJavaClass
// to execute (that period means "the directory we're in," meaning, that original cd wasn't entirely necessary