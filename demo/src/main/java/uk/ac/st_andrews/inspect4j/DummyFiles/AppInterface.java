package uk.ac.st_andrews.inspect4j.DummyFiles;
import java.util.List;

public interface AppInterface<T> extends AppInterfaceParent<T> {

    T bat(String name, int age, String breed, List<String> list);
    T printAddress(String address);
}
