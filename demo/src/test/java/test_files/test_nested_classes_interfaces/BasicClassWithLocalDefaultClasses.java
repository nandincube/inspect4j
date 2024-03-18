package test_files.test_nested_classes_interfaces;

public class BasicClassWithLocalDefaultClasses {
    public static String APPLE = "apple";

    protected static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        class LocalClass {
            public static String ORANGE = "orange";
        }
    }
}

class DefaultClass {
    public static String BANANA = "banana";

    public void hi(String arg){
        System.out.println( "Hello World!" );
    }
    
}


