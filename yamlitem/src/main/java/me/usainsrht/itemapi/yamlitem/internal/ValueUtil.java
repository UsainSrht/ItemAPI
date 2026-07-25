package me.usainsrht.itemapi.yamlitem.internal;

import me.usainsrht.itemapi.yamlitem.YamlParseException;
import net.kyori.adventure.key.Key;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public final class ValueUtil {

    private ValueUtil() {
    }

    public static String requireString(YamlNode node, String key) {
        Object value = node.raw(key);
        if (value == null) {
            throw new YamlParseException(node.childPath(key), "missing value");
        }
        return String.valueOf(value);
    }

    public static @Nullable String string(YamlNode node, String key) {
        Object value = node.raw(key);
        return value == null ? null : String.valueOf(value);
    }

    public static int requireInt(YamlNode node, String key) {
        Object value = node.raw(key);
        if (value == null) {
            throw new YamlParseException(node.childPath(key), "missing int");
        }
        return asInt(value, node.childPath(key));
    }

    public static int intOr(YamlNode node, String key, int defaultValue) {
        Object value = node.raw(key);
        if (value == null) {
            return defaultValue;
        }
        return asInt(value, node.childPath(key));
    }

    public static Integer integer(YamlNode node, String key) {
        Object value = node.raw(key);
        if (value == null) {
            return null;
        }
        return asInt(value, node.childPath(key));
    }

    public static float requireFloat(YamlNode node, String key) {
        Object value = node.raw(key);
        if (value == null) {
            throw new YamlParseException(node.childPath(key), "missing float");
        }
        return asFloat(value, node.childPath(key));
    }

    public static float floatOr(YamlNode node, String key, float defaultValue) {
        Object value = node.raw(key);
        if (value == null) {
            return defaultValue;
        }
        return asFloat(value, node.childPath(key));
    }

    public static Float boxedFloat(YamlNode node, String key) {
        Object value = node.raw(key);
        if (value == null) {
            return null;
        }
        return asFloat(value, node.childPath(key));
    }

    public static long longOr(YamlNode node, String key, long defaultValue) {
        Object value = node.raw(key);
        if (value == null) {
            return defaultValue;
        }
        return asLong(value, node.childPath(key));
    }

    public static boolean boolOr(YamlNode node, String key, boolean defaultValue) {
        Object value = node.raw(key);
        if (value == null) {
            return defaultValue;
        }
        return asBoolean(value, node.childPath(key));
    }

    public static Boolean boxedBoolean(YamlNode node, String key) {
        Object value = node.raw(key);
        if (value == null) {
            return null;
        }
        return asBoolean(value, node.childPath(key));
    }

    public static int asInt(Object value, String path) {
        if (value instanceof Number number) {
            return number.intValue();
        }
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (NumberFormatException ex) {
            throw new YamlParseException(path, "expected int, got " + value);
        }
    }

    public static float asFloat(Object value, String path) {
        if (value instanceof Number number) {
            return number.floatValue();
        }
        try {
            return Float.parseFloat(String.valueOf(value));
        } catch (NumberFormatException ex) {
            throw new YamlParseException(path, "expected float, got " + value);
        }
    }

    public static long asLong(Object value, String path) {
        if (value instanceof Number number) {
            return number.longValue();
        }
        try {
            return Long.parseLong(String.valueOf(value));
        } catch (NumberFormatException ex) {
            throw new YamlParseException(path, "expected long, got " + value);
        }
    }

    public static double asDouble(Object value, String path) {
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        try {
            return Double.parseDouble(String.valueOf(value));
        } catch (NumberFormatException ex) {
            throw new YamlParseException(path, "expected double, got " + value);
        }
    }

    public static boolean asBoolean(Object value, String path) {
        if (value instanceof Boolean bool) {
            return bool;
        }
        String text = String.valueOf(value).toLowerCase(Locale.ROOT);
        if (text.equals("true") || text.equals("yes") || text.equals("on")) {
            return true;
        }
        if (text.equals("false") || text.equals("no") || text.equals("off")) {
            return false;
        }
        throw new YamlParseException(path, "expected boolean, got " + value);
    }

    public static Key key(Object value, String path) {
        String text = String.valueOf(value);
        try {
            if (!text.contains(":")) {
                return Key.key("minecraft", text.toLowerCase(Locale.ROOT));
            }
            return Key.key(text.toLowerCase(Locale.ROOT));
        } catch (Exception ex) {
            throw new YamlParseException(path, "invalid key: " + text, ex);
        }
    }

    public static Key key(YamlNode node, String keyName) {
        Object value = node.raw(keyName);
        if (value == null) {
            throw new YamlParseException(node.childPath(keyName), "missing key");
        }
        return key(value, node.childPath(keyName));
    }

    public static @Nullable Key keyOrNull(YamlNode node, String keyName) {
        Object value = node.raw(keyName);
        if (value == null) {
            return null;
        }
        return key(value, node.childPath(keyName));
    }

    public static NamespacedKey namespacedKey(Object value, String path) {
        Key adventure = key(value, path);
        return new NamespacedKey(adventure.namespace(), adventure.value());
    }

    public static UUID uuid(Object value, String path) {
        try {
            return UUID.fromString(String.valueOf(value));
        } catch (IllegalArgumentException ex) {
            throw new YamlParseException(path, "invalid uuid: " + value, ex);
        }
    }

    public static Color color(Object value, String path) {
        if (value instanceof Number number) {
            return Color.fromRGB(number.intValue() & 0xFFFFFF);
        }
        String text = String.valueOf(value).trim();
        if (text.startsWith("#")) {
            text = text.substring(1);
        }
        try {
            if (text.contains(",")) {
                String[] parts = text.split(",");
                if (parts.length < 3) {
                    throw new YamlParseException(path, "expected r,g,b color");
                }
                return Color.fromRGB(
                        Integer.parseInt(parts[0].trim()),
                        Integer.parseInt(parts[1].trim()),
                        Integer.parseInt(parts[2].trim())
                );
            }
            return Color.fromRGB(Integer.parseInt(text, 16));
        } catch (NumberFormatException ex) {
            throw new YamlParseException(path, "invalid color: " + value, ex);
        }
    }

    public static Color color(YamlNode node, String key) {
        Object value = node.raw(key);
        if (value == null) {
            throw new YamlParseException(node.childPath(key), "missing color");
        }
        return color(value, node.childPath(key));
    }

    public static @Nullable Color colorOrNull(YamlNode node, String key) {
        Object value = node.raw(key);
        if (value == null) {
            return null;
        }
        return color(value, node.childPath(key));
    }

    public static List<String> stringList(YamlNode node, String key) {
        Object value = node.raw(key);
        if (value == null) {
            return List.of();
        }
        if (value instanceof List<?> list) {
            List<String> result = new ArrayList<>(list.size());
            for (Object element : list) {
                result.add(String.valueOf(element));
            }
            return result;
        }
        return List.of(String.valueOf(value));
    }

    public static <E extends Enum<E>> E enumValue(Class<E> type, Object value, String path) {
        String text = String.valueOf(value).trim().toUpperCase(Locale.ROOT).replace('-', '_').replace(' ', '_');
        try {
            return Enum.valueOf(type, text);
        } catch (IllegalArgumentException ex) {
            throw new YamlParseException(path, "unknown " + type.getSimpleName() + ": " + value);
        }
    }

    public static <E extends Enum<E>> E enumValue(YamlNode node, String key, Class<E> type) {
        Object value = node.raw(key);
        if (value == null) {
            throw new YamlParseException(node.childPath(key), "missing " + type.getSimpleName());
        }
        return enumValue(type, value, node.childPath(key));
    }

    public static <E extends Enum<E>> @Nullable E enumOrNull(YamlNode node, String key, Class<E> type) {
        Object value = node.raw(key);
        if (value == null) {
            return null;
        }
        return enumValue(type, value, node.childPath(key));
    }
}
