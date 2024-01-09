package uk.ac.st_andrews.inspect4j.DummyFiles;
import java.util.List;

public interface AppInterfaceParent<T> {

    T cat(String name, int age, String breed, List<String> list);
    T printAddress(String address);
}
