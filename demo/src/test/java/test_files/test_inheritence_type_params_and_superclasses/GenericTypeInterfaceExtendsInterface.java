package test_files.test_inheritence_type_params_and_superclasses;

import java.util.List;

public interface GenericTypeInterfaceExtendsInterface<T, S, A> extends GenericTypeInterface<T,S,A> {

    T bat(String name, int age, String breed, List<String> list);
}