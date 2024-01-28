package uk.ac.st_andrews.inspect4j.DummyFiles;
import java.util.List;

/**
 * JAvaDoc blah blah
 */
public interface AppInterface<T> extends AppInterfaceParent<T> {

    T bat(String name, int age, String breed, List<String> list);
    T printAddress(String address);

    interface AppInterfaceTwo<T>{
        T display();
    }

    default String AppInterfaceTwo(){
        return "h";
    }

    static String AppInterfaceThree(){
        return "h";
    }
    class NestedClass {
        // Nested class members
        public NestedClass(){

        }

        private void hi(){

        }
        
    }

    

}
