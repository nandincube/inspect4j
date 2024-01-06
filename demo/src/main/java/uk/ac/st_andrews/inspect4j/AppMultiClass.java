package uk.ac.st_andrews.inspect4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Hello world!
 *
 */
public class AppMultiClass extends Object implements AppInterface<String>{
    public static void main( String[] args ){
        System.out.println( "Hello World!" );
    }

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
        String b = null;
        b = printAddress("addr");
        String a = null;
        a = printAddress("addr");
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

