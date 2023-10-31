package uk.ac.st_andrews.inspect4j;

import java.util.List;

public interface AppInterface<T> {

    T cat(String name, int age, String breed, List<String> list);
    T printAddress(String address);
}
