import java.util.List;

public class GenericClassParent<T> {
    
    protected String name;

    public GenericClassParent(String name){
        this.name = name;
    }

    public void cat(String name, int age, String breed, List<String> list){
        System.out.println("Cat name:"+name);
        System.out.println("Cat age:"+age);
        System.out.println("Cat bree:"+breed);
    }

 
}
