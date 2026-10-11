package me.usainsrht.itemapi.itemtext;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.object.ObjectContents;
import net.kyori.adventure.text.object.PlayerHeadObjectContents;
import org.bukkit.entity.EntityType;
import org.jspecify.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * Registry of curated entity head skins and factory for Adventure mob head components.
 */
public final class MobHeadRegistry {

    private static final boolean OBJECT_CONTENTS_SUPPORTED;

    static {
        boolean supported;
        try {
            Class.forName("net.kyori.adventure.text.object.ObjectContents");
            supported = true;
        } catch (Throwable ignored) {
            supported = false;
        }
        OBJECT_CONTENTS_SUPPORTED = supported;
    }

    public record MobHead(UUID id, String base64Texture) {
    }

    private static final Map<String, MobHead> BY_NAME = load();

    private MobHeadRegistry() {
    }

    /**
     * Resolves the {@link MobHead} for the given entity key or entity type name.
     */
    public static @Nullable MobHead find(String entityKey) {
        if (entityKey == null || entityKey.isEmpty()) {
            return null;
        }
        String normalized = normalize(entityKey);
        return BY_NAME.get(normalized);
    }

    /**
     * Resolves the {@link MobHead} for the given {@link EntityType}.
     */
    public static @Nullable MobHead find(EntityType entityType) {
        if (entityType == null) {
            return null;
        }
        return find(entityType.name());
    }

    /**
     * Creates a player head component for the given {@link EntityType} with hat enabled.
     */
    public static Component create(EntityType entityType) {
        return create(entityType, true);
    }

    /**
     * Creates a player head component for the given {@link EntityType} with explicit hat layer control.
     */
    public static Component create(EntityType entityType, boolean hat) {
        if (entityType == null) {
            return Component.empty();
        }
        return create(entityType.name(), hat);
    }

    /**
     * Creates a player head component for the given entity key (e.g. "zombie", "minecraft:cow") with hat enabled.
     */
    public static Component create(String entityKey) {
        return create(entityKey, true);
    }

    /**
     * Creates a player head component for the given entity key with explicit hat layer control.
     */
    public static Component create(String entityKey, boolean hat) {
        if (!OBJECT_CONTENTS_SUPPORTED || entityKey == null) {
            return Component.empty();
        }
        MobHead ref = find(entityKey);
        if (ref == null) {
            return Component.empty();
        }
        return Component.object(ObjectContents.playerHead()
                .id(ref.id())
                .profileProperty(PlayerHeadObjectContents.property("textures", ref.base64Texture()))
                .hat(hat)
                .build());
    }

    /**
     * Normalizes entity keys by removing the "minecraft:" namespace, lowercasing, and replacing hyphens with underscores.
     */
    public static String normalize(String key) {
        String k = key.trim().toLowerCase(Locale.ROOT);
        if (k.startsWith("minecraft:")) {
            k = k.substring("minecraft:".length());
        }
        return k.replace('-', '_');
    }

    private static Map<String, MobHead> load() {
        String resource = "me/usainsrht/itemapi/itemtext/mob_textures.txt";
        InputStream in = MobHeadRegistry.class.getClassLoader().getResourceAsStream(resource);
        if (in == null) {
            return Collections.emptyMap();
        }
        Map<String, MobHead> map = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.charAt(0) == '#') {
                    continue;
                }
                int eq = line.indexOf('=');
                if (eq <= 0) {
                    continue;
                }
                String key = normalize(line.substring(0, eq));
                String value = line.substring(eq + 1).trim();
                if (key.isEmpty() || value.isEmpty()) {
                    continue;
                }

                String base64 = toBase64Texture(value);
                UUID uuid = UUID.nameUUIDFromBytes(("itemapi:mob:" + key).getBytes(StandardCharsets.UTF_8));
                map.put(key, new MobHead(uuid, base64));
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return Collections.unmodifiableMap(map);
    }

    private static String toBase64Texture(String value) {
        // If already full Base64 payload, keep it
        if (value.startsWith("ey")) {
            return value;
        }
        String json = "{\"textures\":{\"SKIN\":{\"url\":\"http://textures.minecraft.net/texture/" + value + "\"}}}";
        return Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
    }
}
