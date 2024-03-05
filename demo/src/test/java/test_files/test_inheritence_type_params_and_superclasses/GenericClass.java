package test_files.test_inheritence_type_params_and_superclasses;

import java.util.List;

public class GenericClass<T,S, A> extends GenericClassParent<T, S> implements GenericTypeInterface<T, S, A>{

    private int age;

    public GenericClass(String name, int age) {
        super(name);
        this.age = age;
    }

    public T printAddress(String address) {
        T t = null;
        System.out.println("Address:" + address);
        return t;
    }

    public T bat(String name, int age, String breed, List<String> list) {
        System.out.println("Cat name:" + name);
        System.out.println("Cat age:" + age);
        System.out.println("Cat bree:" + breed);

        if (name.length() > 5) {
            if (true) {
                String[] ar3 = { "i", "efwef" };
            }
        }
        return null;

    }

}
