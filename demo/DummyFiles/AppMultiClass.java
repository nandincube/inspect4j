package uk.ac.st_andrews.inspect4j.DummyFiles;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Hello world!
 *
 */
public class AppMultiClass extends App implements AppInterface<String>{
    public static void main( String[] args ){
        System.out.println( "Hello World!" );
    }

    /**
    * Returns an Image object that can then be painted on the screen. 
    * The url argument must specify an absolute <a href="#{@link}">{@link URL}</a>. The name
    * argument is a specifier that is relative to the url argument. 
    * <p>
    * This method always returns immediately, whether or not the 
    * image exists. When this applet attempts to draw the image on
    * the screen, the data will be loaded. The graphics primitives 
    * that draw the image will incrementally paint on the screen. 
    *
    * @param  url  an absolute URL giving the base location of the image
    * @param  name the location of the image, relative to the url argument
    * @return      the image at the specified URL
    * @see         Image
    */
    public String cat(String name, int age, String breed, List<String> list)
    {
        System.out.println("Cat name:"+name);
        System.out.println("Cat age:"+age);
        System.out.println("Cat bree:"+breed);
        int[] arr = new int[3];
        arr[0] = 0;
        arr[1] = 1;
        arr[2] = 2;
        ArrayList<Integer> numbers = new ArrayList<Integer>();
        numbers.add(0);
        numbers.add(1);
        numbers.add(2);
        numbers.forEach(x -> {
            System.out.println("printing i: "+x);});
        numbers.forEach( System.out::println);
        numbers.stream().map(n -> n * 2).collect(Collectors.toList());
        
        numbers.stream().map((n)-> n* 2).collect(Collectors.toList());
        String b = null;
        b = printAddress("addr");
        String a = null;
        a = printAddress("addr");

        List<String> c = new ArrayList<>();
        c.forEach(x -> System.out.println(x));
        if(true){
             return "Cat";
        }else{
            return "Cat2";
        }
       
         
    }

    public String bat(String name, int age, String breed, List<String> list){
        System.out.println("Bat name:"+name);
        System.out.println("Bat age:"+age);
        System.out.println("Bat bree:"+breed);

        return "Bat";
         
    }

    public String printAddress(String address){
        System.out.println("Address: "+address);
       

        class Local{
            
            void run() {
                System.out.println("hi");
            }

        }
        Local local = new Local();
        local.run();

         return "abd,12,uebf";

    }

    private class InnerClass {
        private String horseName;
    
        public InnerClass(String hn) { 
            this.horseName = hn;
           
            
        }
    
        private void hiya(){
            System.out.println("hi");
            String c = null;
            c = printAddress("addr");
        }
    
    }

}

 class AnotherClass {
    private String horseName;

    public static void main( String[] args ){
        System.out.println( "Hello World!" );
    }

}

