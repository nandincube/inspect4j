package test_files.test_doc_and_dependencies;

import java.util.List;
import java.security.interfaces.DSAKey;
import static java.lang.Math.abs;

public class BasicClassWithDependencies {

    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
    }

    public static final String bat(String name, int age, String breed){
        System.out.println("bat name:"+name);
        System.out.println("bat age:"+age);

        if(age > 2){
            return "yes";
        }else{
            return "no";
        }
        
    }
}