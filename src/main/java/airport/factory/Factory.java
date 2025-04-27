package airport.factory;

public interface Factory<T> {
    java.util.List<T> loadFromCSV(String csvPath) throws Exception;
} 