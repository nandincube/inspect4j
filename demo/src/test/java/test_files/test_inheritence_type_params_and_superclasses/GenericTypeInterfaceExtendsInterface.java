package test_files.test_inheritence_type_params_and_superclasses;

import java.util.List;

/**
 * JAvaDoc blah blah
 */
public interface GenericTypeInterfaceExtendsInterface<T> extends GenericTypeInterface<T> {

    T bat(String name, int age, String breed, List<String> list);
    T printAddress(String address);


}