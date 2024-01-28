import java.util.List;

public interface GenericTypeInterface<T> {

    T cat(String name, int age, String breed, List<String> list);
    T printAddress(String address);
}