package at.ac.tuwien.ifs.sge.agent.alpharisk.tree.nodes;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class NodeStatistics {
    private Map<String, Number> statistics = new HashMap<>();

    public NodeStatistics(Map<String, Number> statistics) {
        this.statistics = new HashMap<>(statistics);
    }

    public NodeStatistics(NodeStatistics statistics) {
        this(statistics.statistics);
    }

    public NodeStatistics() {
    }

    public static NodeStatistics of(String key, Number value) {
        return new NodeStatistics(Map.of(key, value));
    }

    public NodeStatistics with(String key, Number value) {
        this.update(key, value);
        return this;
    }

    public NodeStatistics concat(String key, Number value) {
        var stats = new NodeStatistics(this.statistics);
        stats.update(key, value);
        return stats;
    }

    public NodeStatistics concat(NodeStatistics statistics) {
        var stats = new NodeStatistics(this.statistics);
        for (Map.Entry<String, Number> entry : statistics.statistics.entrySet()) {
            stats.update(entry.getKey(), entry.getValue());
        }
        return stats;
    }

    public void update(String key, Number value) {
        statistics.put(key, value);
    }

    public void update(String key, int value) {
        statistics.put(key, value);
    }

    public void update(String key, double value) {
        statistics.put(key, value);
    }

    public void increment(String key, int value) {
        update(key, getInt(key) + value);
    }

    public void increment(String key, double value) {
        update(key, (this.contains(key) ? getDouble(key) : 0) + value);

    }

    public double getDouble(String key) {
        return statistics.get(key).doubleValue();
    }

    public int getInt(String key) {
        return statistics.get(key).intValue();
    }

    public boolean contains(String key) {
        return statistics.containsKey(key);
    }

    public Set<Map.Entry<String, Number>> entrieSet() {
        return statistics.entrySet();
    }

    @Override
    public String toString() {
        return entrieSet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining(", "));

    }
}
