package uk.ac.st_andrews.inspect4j.DummyFiles.DummyDir;

import java.util.List;

/**
 * Hello world!
 *
 */
public class App extends Object
{
    public static void main( String[] args )
    {
        cat2( "kitty", 2, "tabby" );
        System.out.println( "Hello World!" );
    }

    public String cat(String name, int age, String breed, List<String> list){
        System.out.println("Cat name:"+name);
        System.out.println("Cat age:"+age);
        System.out.println("Cat bree:"+breed);

        return "abdbf";
         
    }

    public static String cat2(String name, int age, String breed){
        System.out.println("Cat name:"+name);
        System.out.println("Cat age:"+age);

        return "abdbf";
         
    }

    public String bat(String name, int age, String breed, List<String> list){
        System.out.println("Bat name:"+name);
        System.out.println("Bat age:"+age);
        System.out.println("Bat bree:"+breed);

        return "Bat";
         
    }

    public String printAddress(String address){
        System.out.println("Address: "+address);
        return "abd,12,uebf";

    }
    
}

