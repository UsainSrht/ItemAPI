package me.usainsrht.itemapi.yamlitem.internal;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Lightweight view over a ConfigurationSection or Map for YAML parsing.
 */
public final class YamlNode {

    private final Map<String, Object> values;
    private final String path;

    private YamlNode(Map<String, Object> values, String path) {
        this.values = values;
        this.path = path == null ? "" : path;
    }

    public static YamlNode of(ConfigurationSection section) {
        return of(section, section.getCurrentPath() == null ? "" : section.getCurrentPath());
    }

    public static YamlNode of(ConfigurationSection section, String path) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (String key : section.getKeys(false)) {
            map.put(key, section.get(key));
        }
        return new YamlNode(map, path);
    }

    @SuppressWarnings("unchecked")
    public static YamlNode of(Map<?, ?> map) {
        return of(map, "");
    }

    @SuppressWarnings("unchecked")
    public static YamlNode of(Map<?, ?> map, String path) {
        Map<String, Object> values = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (entry.getKey() == null) {
                continue;
            }
            values.put(String.valueOf(entry.getKey()), entry.getValue());
        }
        return new YamlNode(values, path);
    }

    public String path() {
        return path;
    }

    public String childPath(String key) {
        return path.isEmpty() ? key : path + "." + key;
    }

    public Set<String> keys() {
        return Collections.unmodifiableSet(values.keySet());
    }

    public boolean contains(String key) {
        return values.containsKey(key);
    }

    public @Nullable Object raw(String key) {
        return values.get(key);
    }

    public @Nullable YamlNode child(String key) {
        Object value = values.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof ConfigurationSection section) {
            return of(section, childPath(key));
        }
        if (value instanceof Map<?, ?> map) {
            return of(map, childPath(key));
        }
        return null;
    }

    public YamlNode requireChild(String key) {
        YamlNode child = child(key);
        if (child == null) {
            throw new me.usainsrht.itemapi.yamlitem.YamlParseException(childPath(key), "expected a map/section");
        }
        return child;
    }

    public List<YamlNode> childrenList(String key) {
        Object value = values.get(key);
        if (value == null) {
            return List.of();
        }
        if (!(value instanceof List<?> list)) {
            throw new me.usainsrht.itemapi.yamlitem.YamlParseException(childPath(key), "expected a list");
        }
        List<YamlNode> nodes = new ArrayList<>(list.size());
        for (int i = 0; i < list.size(); i++) {
            Object element = list.get(i);
            String elementPath = childPath(key) + "[" + i + "]";
            if (element instanceof ConfigurationSection section) {
                nodes.add(of(section, elementPath));
            } else if (element instanceof Map<?, ?> map) {
                nodes.add(of(map, elementPath));
            } else {
                throw new me.usainsrht.itemapi.yamlitem.YamlParseException(elementPath, "expected a map/section");
            }
        }
        return nodes;
    }

    public List<?> list(String key) {
        Object value = values.get(key);
        if (value == null) {
            return List.of();
        }
        if (!(value instanceof List<?> list)) {
            throw new me.usainsrht.itemapi.yamlitem.YamlParseException(childPath(key), "expected a list");
        }
        return list;
    }

    public ConfigurationSection asSection() {
        MemoryConfiguration configuration = new MemoryConfiguration();
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            configuration.set(entry.getKey(), entry.getValue());
        }
        return configuration;
    }
}
