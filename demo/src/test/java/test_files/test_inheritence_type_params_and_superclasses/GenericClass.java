package test_files.test_inheritence_type_params_and_superclasses;

import java.util.List;

public class GenericClass<T> extends GenericClassParent<T> {

    private int age;

    public GenericClass(String name, int age) {
        super(name);
        this.age = age;
    }

    public void cat(String name, int age, String breed, List<String> list) {
        System.out.println("Cat name:" + name);
        System.out.println("Cat age:" + age);
        System.out.println("Cat bree:" + breed);

        if (name.length() > 5) {
            if (true) {
                String[] ar3 = { "i", "efwef" };
            }
        }

    }

}
