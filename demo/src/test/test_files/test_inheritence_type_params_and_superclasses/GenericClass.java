import java.util.List;

public class GenericClass<T> extends GenericClassParent<T> {

    private int age;
    
    public GenericClass(String name, String age){
        super(name);
        this.age = age;
    }
    
     public String[] cat(String name, int age, String breed, List<String> list){
        System.out.println("Cat name:"+name);
        System.out.println("Cat age:"+age);
        System.out.println("Cat bree:"+breed);
        
        if(name.length() > 5){
             if(true){
               String[] ar3 = {"i","efwef"};;
                return ar3;  
            }
        }
        String[] ar2 = {"i","5"};
        return ar2;     
    }

 
}
