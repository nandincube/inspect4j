package test_files.test_basic;

import java.util.List;

public class BasicClassWithMultipleMethods {

    private String catName;
    private String dogName = dogNameSetter();

    public static void main(String[] args) {
        System.out.println("Hello World!");
    }

    public BasicClassWithMultipleMethods(String catName) {
        this.catName = catName;
    }

    public String cat(String name, int age, String breed, List<String> list) {
        System.out.println("Cat name:" + name);
        System.out.println("Cat age:" + age);
        System.out.println("Cat bree:" + breed);
        String dog;
        dog = dogNameSetter();
        String dog2 = dogNameSetter();

        return "abdbf";

    }

    public static String bat(String name, int age, String breed) {
        System.out.println("bat name:" + name);
        System.out.println("bat age:" + age);

        if (age > 2) {
            return "yes";
        } else {
            return "no";
        }

    }

    private void mat(String name, int size) {
        System.out.println("mat name:" + name);
        System.out.println("mat size:" + size);
    }

    private String dogNameSetter() {
        return "Sam";
    }
}