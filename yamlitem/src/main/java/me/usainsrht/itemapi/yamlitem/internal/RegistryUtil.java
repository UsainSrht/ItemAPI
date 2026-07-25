package me.usainsrht.itemapi.yamlitem.internal;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
import me.usainsrht.itemapi.yamlitem.YamlParseException;
import net.kyori.adventure.key.Key;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class RegistryUtil {

    private RegistryUtil() {
    }

    public static <T extends Keyed> T require(RegistryKey<T> registryKey, Object value, String path) {
        T resolved = get(registryKey, value, path);
        if (resolved == null) {
            throw new YamlParseException(path, "unknown " + registryKey.key().value() + ": " + value);
        }
        return resolved;
    }

    public static <T extends Keyed> @Nullable T get(RegistryKey<T> registryKey, Object value, String path) {
        Key key = ValueUtil.key(value, path);
        Registry<T> registry = RegistryAccess.registryAccess().getRegistry(registryKey);
        return registry.get(key);
    }

    public static <T extends Keyed> T require(Registry<T> registry, Object value, String path) {
        Key key = ValueUtil.key(value, path);
        T resolved = registry.get(key);
        if (resolved == null) {
            throw new YamlParseException(path, "unknown registry value: " + value);
        }
        return resolved;
    }

    public static <T extends Keyed> RegistryKeySet<T> keySet(RegistryKey<T> registryKey, YamlNode node, String key) {
        Object value = node.raw(key);
        if (value == null) {
            throw new YamlParseException(node.childPath(key), "missing registry key set");
        }
        return keySet(registryKey, value, node.childPath(key));
    }

    public static <T extends Keyed> RegistryKeySet<T> keySet(RegistryKey<T> registryKey, Object value, String path) {
        List<TypedKey<T>> keys = new ArrayList<>();
        if (value instanceof List<?> list) {
            for (int i = 0; i < list.size(); i++) {
                keys.add(typedKey(registryKey, list.get(i), path + "[" + i + "]"));
            }
        } else {
            keys.add(typedKey(registryKey, value, path));
        }
        return RegistrySet.keySet(registryKey, keys);
    }

    public static <T extends Keyed> @Nullable RegistryKeySet<T> keySetOrNull(RegistryKey<T> registryKey, YamlNode node, String key) {
        if (!node.contains(key)) {
            return null;
        }
        return keySet(registryKey, node, key);
    }

    public static <T extends Keyed> TypedKey<T> typedKey(RegistryKey<T> registryKey, Object value, String path) {
        Key key = ValueUtil.key(value, path);
        return TypedKey.create(registryKey, key);
    }

    public static NamespacedKey asNamespaced(Key key) {
        return new NamespacedKey(key.namespace(), key.value());
    }

    public static String normalizeId(String id) {
        return id.toLowerCase(Locale.ROOT).replace(' ', '_');
    }
}
