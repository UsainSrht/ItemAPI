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
        registerAlias("enchants", "enchantments");
        registerAlias("stored_enchants", "stored_enchantments");
        registerAlias("swing_animation", "attack_animation");
        ComponentHandlers.registerAll(this);
    }

    private void indexTypes() {
        try {
            for (Field field : DataComponentTypes.class.getFields()) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                if (!DataComponentType.class.isAssignableFrom(field.getType())) {
                    continue;
                }
                try {
                    DataComponentType type = (DataComponentType) field.get(null);
                    if (type != null) {
                        registerId(type);
                    }
                } catch (Throwable ignored) {
                    // skip inaccessible or linkage issues
                }
            }
        } catch (Throwable ignored) {
        }
        // Prefer registry if available at runtime
        try {
            Registry<DataComponentType> registry = Registry.DATA_COMPONENT_TYPE;
            if (registry != null) {
                for (DataComponentType type : registry) {
                    registerId(type);
                }
            }
        } catch (Throwable ignored) {
        }
    }

    private void registerId(DataComponentType type) {
        NamespacedKey key = type.getKey();
        String rawKey = key.getKey();
        byId.put(rawKey, type);
        byId.put(key.toString(), type);
        byId.put(rawKey.replace('_', '-'), type);
        if (rawKey.contains("/")) {
            byId.put(rawKey.replace('/', '_'), type);
            byId.put(rawKey.replace('/', '-'), type);
            byId.put("minecraft:" + rawKey.replace('/', '_'), type);
        }
    }

    public void register(DataComponentType type, ComponentHandler handler) {
        byType.put(type, handler);
        registerId(type);
    }

    public boolean register(String id, ComponentHandler handler) {
        DataComponentType type = resolveType(id);
        if (type != null) {
            register(type, handler);
            return true;
        }
        return false;
    }

    public boolean registerSafely(String id, ComponentHandler handler) {
        try {
            return register(id, handler);
        } catch (Throwable t) {
            me.usainsrht.itemapi.yamlitem.internal.ItemLogger.warn("Failed to register component handler for '" + id + "': " + t.getMessage());
            return false;
        }
    }

    public void registerAlias(String alias, DataComponentType type) {
        if (type == null) {
            return;
        }
        String normalized = alias.toLowerCase(Locale.ROOT).trim();
        byId.put(normalized, type);
        byId.put("minecraft:" + normalized, type);
        byId.put(normalized.replace('_', '-'), type);
    }

    public void registerAlias(String alias, String targetComponentId) {
        DataComponentType type = resolveType(targetComponentId);
        if (type != null) {
            registerAlias(alias, type);
        }
    }

    public DataComponentType requireType(String id, String path) {
        DataComponentType type = resolveType(id);
        if (type == null) {
            throw new YamlParseException(path, "unknown data component: " + id);
        }
        return type;
    }

    public boolean hasType(String id) {
        return resolveType(id) != null;
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

    public ComponentHandler getHandler(DataComponentType type) {
        return byType.get(type);
    }

    public boolean hasHandler(DataComponentType type) {
        return byType.containsKey(type);
    }
}
