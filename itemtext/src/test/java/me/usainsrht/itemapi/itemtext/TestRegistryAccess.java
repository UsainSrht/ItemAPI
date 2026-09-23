package me.usainsrht.itemapi.itemtext;

import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.BlockType;
import org.bukkit.inventory.ItemType;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public class TestRegistryAccess implements RegistryAccess {

    private final Map<String, Registry<?>> registries = new HashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Keyed> Registry<T> getRegistry(Class<T> type) {
        return (Registry<T>) registries.computeIfAbsent(type.getName(), k -> createMockRegistry(type, null));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Keyed> Registry<T> getRegistry(RegistryKey<T> key) {
        return (Registry<T>) registries.computeIfAbsent(key.key().asString(), k -> createMockRegistry(null, key));
    }

    @SuppressWarnings("unchecked")
    private static <T extends Keyed> Registry<T> createMockRegistry(@Nullable Class<T> type, @Nullable RegistryKey<T> regKey) {
        return (Registry<T>) Proxy.newProxyInstance(
                Registry.class.getClassLoader(),
                new Class<?>[]{Registry.class},
                new RegistryInvocationHandler(type, regKey)
        );
    }

    private static class RegistryInvocationHandler implements InvocationHandler {
        private final @Nullable Class<?> type;
        private final @Nullable RegistryKey<?> regKey;
        private final Map<String, Object> cache = new HashMap<>();

        RegistryInvocationHandler(@Nullable Class<?> type, @Nullable RegistryKey<?> regKey) {
            this.type = type;
            this.regKey = regKey;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String name = method.getName();
            if (name.equals("get") || name.equals("getOrThrow")) {
                Object keyObj = args[0];
                Key key;
                if (keyObj instanceof Key k) {
                    key = k;
                } else if (keyObj instanceof NamespacedKey nk) {
                    key = nk;
                } else {
                    return null;
                }
                return cache.computeIfAbsent(key.asString(), k -> createMockEntry(key));
            }
            if (name.equals("getKey") || name.equals("getKeyOrThrow")) {
                if (args != null && args.length > 0 && args[0] instanceof Keyed keyed) {
                    return keyed.getKey();
                }
                return null;
            }
            if (name.equals("stream") || name.equals("keyStream")) {
                return cache.values().stream();
            }
            if (name.equals("iterator")) {
                return cache.values().iterator();
            }
            if (name.equals("size")) {
                return cache.size();
            }
            if (name.equals("toString")) {
                return "TestRegistryProxy[" + (type != null ? type.getSimpleName() : regKey) + "]";
            }
            if (name.equals("hashCode")) {
                return System.identityHashCode(proxy);
            }
            if (name.equals("equals")) {
                return proxy == args[0];
            }
            return null;
        }

        private Object createMockEntry(Key key) {
            NamespacedKey namespacedKey = NamespacedKey.fromString(key.asString());
            Class<?>[] interfaces = resolveInterfaces();

            return Proxy.newProxyInstance(
                    TestRegistryAccess.class.getClassLoader(),
                    interfaces,
                    (p, m, a) -> {
                        String mName = m.getName();
                        if (mName.equals("getKey") || mName.equals("key")) {
                            return namespacedKey;
                        }
                        if (mName.equals("isAir")) {
                            return key.value().endsWith("air");
                        }
                        if (mName.equals("translationKey") || mName.equals("getTranslationKey")
                                || mName.equals("getItemTranslationKey") || mName.equals("getBlockTranslationKey")) {
                            return "item.minecraft." + key.value();
                        }
                        if (mName.equals("toString")) {
                            return "MockEntry[" + key + "]";
                        }
                        if (mName.equals("hashCode")) {
                            return key.hashCode();
                        }
                        if (mName.equals("equals")) {
                            return p == a[0];
                        }
                        if (m.getReturnType().equals(boolean.class)) return false;
                        if (m.getReturnType().equals(int.class)) return 0;
                        return null;
                    }
            );
        }

        private Class<?>[] resolveInterfaces() {
            if ((type != null && BlockType.class.isAssignableFrom(type))
                    || (regKey != null && regKey.key().value().equals("block"))) {
                return new Class<?>[]{BlockType.class, Keyed.class};
            }
            if ((type != null && ItemType.class.isAssignableFrom(type))
                    || (regKey != null && regKey.key().value().equals("item"))) {
                return new Class<?>[]{ItemType.class, ItemType.Typed.class, Keyed.class};
            }
            if ((type != null && DataComponentType.class.isAssignableFrom(type))
                    || (regKey != null && regKey.key().value().equals("data_component_type"))) {
                return new Class<?>[]{DataComponentType.Valued.class, DataComponentType.NonValued.class, Keyed.class};
            }
            return new Class<?>[]{Keyed.class};
        }
    }
}
