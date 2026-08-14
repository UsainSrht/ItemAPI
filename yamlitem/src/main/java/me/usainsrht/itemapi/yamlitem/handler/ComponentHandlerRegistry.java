package me.usainsrht.itemapi.yamlitem.handler;

import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import me.usainsrht.itemapi.yamlitem.YamlParseException;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Maps component id strings and {@link DataComponentType}s to handlers.
 */
public final class ComponentHandlerRegistry {

    private final Map<DataComponentType, ComponentHandler> byType = new HashMap<>();
    private final Map<String, DataComponentType> byId = new HashMap<>();

    public ComponentHandlerRegistry() {
        indexTypes();
        registerAlias("enchants", DataComponentTypes.ENCHANTMENTS);
        registerAlias("stored_enchants", DataComponentTypes.STORED_ENCHANTMENTS);
        ComponentHandlers.registerAll(this);
    }

    private void indexTypes() {
        for (Field field : DataComponentTypes.class.getFields()) {
            if (!Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            if (!DataComponentType.class.isAssignableFrom(field.getType())) {
                continue;
            }
            try {
                DataComponentType type = (DataComponentType) field.get(null);
                registerId(type);
            } catch (IllegalAccessException ignored) {
                // skip inaccessible
            }
        }
        // Prefer registry if available at runtime
        Registry<DataComponentType> registry = Registry.DATA_COMPONENT_TYPE;
        for (DataComponentType type : registry) {
            registerId(type);
        }
    }

    private void registerId(DataComponentType type) {
        NamespacedKey key = type.getKey();
        byId.put(key.getKey(), type);
        byId.put(key.toString(), type);
        byId.put(key.getKey().replace('_', '-'), type);
    }

    public void register(DataComponentType type, ComponentHandler handler) {
        byType.put(type, handler);
        registerId(type);
    }

    public void registerAlias(String alias, DataComponentType type) {
        String normalized = alias.toLowerCase(Locale.ROOT).trim();
        byId.put(normalized, type);
        byId.put("minecraft:" + normalized, type);
        byId.put(normalized.replace('_', '-'), type);
    }

    public DataComponentType requireType(String id, String path) {
        DataComponentType type = resolveType(id);
        if (type == null) {
            throw new YamlParseException(path, "unknown data component: " + id);
        }
        return type;
    }

    public DataComponentType resolveType(String id) {
        String normalized = id.toLowerCase(Locale.ROOT).trim();
        if (normalized.startsWith("!")) {
            normalized = normalized.substring(1);
        }
        if (normalized.startsWith("minecraft:")) {
            normalized = normalized.substring("minecraft:".length());
            DataComponentType type = byId.get(normalized);
            if (type != null) {
                return type;
            }
            return byId.get("minecraft:" + normalized);
        }
        DataComponentType type = byId.get(normalized);
        if (type != null) {
            return type;
        }
        return byId.get("minecraft:" + normalized);
    }

    public ComponentHandler requireHandler(DataComponentType type, String path) {
        ComponentHandler handler = byType.get(type);
        if (handler == null) {
            throw new YamlParseException(path, "no handler registered for component " + type.getKey());
        }
        return handler;
    }

    public Set<DataComponentType> registeredTypes() {
        return Collections.unmodifiableSet(byType.keySet());
    }

    public boolean hasHandler(DataComponentType type) {
        return byType.containsKey(type);
    }
}
