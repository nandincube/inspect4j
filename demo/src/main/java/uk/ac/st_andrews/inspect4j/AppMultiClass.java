package uk.ac.st_andrews.inspect4j;

import java.util.List;


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

        return "Cat";
         
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
            }

    }
}

