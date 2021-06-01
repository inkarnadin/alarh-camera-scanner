package scanner.stat;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

abstract public class AbstractStatGatherer<T, S> {

    protected final Map<T, S> data = new TreeMap<>();

    public Map<String, String> getData() {
        recalculate();

        return data.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().toString(),
                        entry -> entry.getValue().toString())
                );
    }

    public void set(T item, S value) {
        data.put(item, value);
    }

    public S get(T item) {
        return data.get(item);
    }

    public String getStatsAsString() {
        recalculate();
        return data.values().stream()
                .map(Object::toString)
                .collect(Collectors.joining(";"));
    }

    public void increment(T item) {}

    public void incrementBy(T item, S value) {}

    abstract protected void recalculate();

}