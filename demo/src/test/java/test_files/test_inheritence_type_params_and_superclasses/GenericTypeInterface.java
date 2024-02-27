package test_files.test_inheritence_type_params_and_superclasses;

import java.util.List;

public interface GenericTypeInterface<T> {

    T cat(String name, int age, String breed, List<String> list);
    T printAddress(String address);
}