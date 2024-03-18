package test_files.test_nested_classes_interfaces;

public class BasicClassWithNestedClasses {
    public static String APPLE = "apple";

    private static class NestedClass {
        public static String ORANGE = "orange";

        public void hi(String arg){
            System.out.println( "Hello World!" );
        }
    }

    private class InnerClass {
        public static String BANANA = "banana";
    }
    
}
