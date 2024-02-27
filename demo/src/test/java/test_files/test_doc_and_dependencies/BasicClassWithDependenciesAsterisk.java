package test_files.test_doc_and_dependencies;

//import test_files.test_basic.*;
import test_files.test_basic.BasicClassWithMultipleMethods;


public class BasicClassWithDependenciesAsterisk{

    private String catName;
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
    }

    public BasicClassWithDependenciesAsterisk(String catName ){
        this.catName = catName;
    }


    public static String bat(String name, int age, String breed){
        System.out.println("bat name:"+name);
        System.out.println("bat age:"+age);

        if(age > 2){
            return "yes";
        }else{
            return "no";
        }
        
    }

    private void mat(String name, int size){
        System.out.println("mat name:"+name);
        System.out.println("mat size:"+size);
    }
}