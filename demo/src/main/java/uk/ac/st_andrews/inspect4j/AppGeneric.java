package uk.ac.st_andrews.inspect4j;

import java.util.List;

public class AppGeneric<T> {

     public String[] cat(String name, int age, String breed, List<String> list){
        System.out.println("Cat name:"+name);
        System.out.println("Cat age:"+age);
        System.out.println("Cat bree:"+breed);

        breed = returnBreed();
        
        if(name.length() > 5){
             if(true){
               String[] ar3 = {"i","efwef"};
               name = returnBreed();
                return ar3;  
            }

            String[] ar1 = {"i","efwef"};
            return ar1;
           
        }
        String[] ar2 = {"i","5"};
        return ar2;     
    }

    public String printAddress(String address){
        System.out.println("Address: "+address);
        return "abd,12,uebf";

    }

    public String returnBreed(){
        return("Tabby");
    }
}
