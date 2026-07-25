package me.usainsrht.itemapi.itemtext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Names present in the vanilla {@code minecraft:items} atlas (texture file basenames).
 */
final class ItemAtlasSprites {

    private static final Set<String> NAMES = load();

    private ItemAtlasSprites() {
    }

    static boolean contains(String spriteName) {
        return NAMES.contains(spriteName);
    }

    private static Set<String> load() {
        String resource = "me/usainsrht/itemapi/itemtext/item_sprites.txt";
        InputStream in = ItemAtlasSprites.class.getClassLoader().getResourceAsStream(resource);
        if (in == null) {
            throw new IllegalStateException("Missing resource: " + resource);
        }
        Set<String> names = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && line.charAt(0) != '#') {
                    names.add(line);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return Collections.unmodifiableSet(names);
    }
}
