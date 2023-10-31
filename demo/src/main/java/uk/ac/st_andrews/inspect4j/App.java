package uk.ac.st_andrews.inspect4j;

import java.util.List;

/**
 * Hello world!
 *
 */
public class App extends Object implements AppInterface<String>
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
    }

    public String cat(String name, int age, String breed, List<String> list){
        System.out.println("Cat name:"+name);
        System.out.println("Cat age:"+age);
        System.out.println("Cat bree:"+breed);
         return "abd,12,uebf";
    }

    public String printAddress(String address){
        System.out.println("Address: "+address);
        return "abd,12,uebf";

    }
}

