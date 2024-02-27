package test_files.test_doc_and_dependencies;

/**
 * Class with java doc comments
 */
public class BasicClassWithJavaDoc {
    private String catName;
   
    /**
     * Constructor
     * @param catName
     */
    public BasicClassWithJavaDoc(String catName ){
        this.catName = catName;
    }
    
    /**
     * Prints cat details 
     * @param name - (int) cat name
     * @param age - (int) cat age
     * @param breed - (String) cat breed
     * @param list
     * @return
     */
    public String cat(String name, int age, String breed){
        System.out.println("Cat name:"+name);
        System.out.println("Cat age:"+age);
        System.out.println("Cat bree:"+breed);
        return "printed cat details";
        
    }

    /**
     * Prints mat details
     * @param name -  (String) mat product name
     * @param size - (int) size of mat
     */
    private void mat(String name , int size){
        System.out.println("mat name:"+name);
        System.out.println("mat size:"+size);
    }
}