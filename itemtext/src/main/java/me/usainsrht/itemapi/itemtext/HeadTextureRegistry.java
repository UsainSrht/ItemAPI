package me.usainsrht.itemapi.itemtext;

import org.jspecify.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

/**
 * Registry of pre-baked, curated Base64 textures and deterministic UUIDs for items and blocks
 * whose inventory representation is 3D or entity-based (chests, mob heads, heavy core) so they
 * can render as 2D player head font glyphs with asynchronous client-side texture loading and caching.
 */
final class HeadTextureRegistry {

    record HeadRef(UUID id, String base64Texture) implements SpriteTarget {
    }

    private static final Map<String, HeadRef> HEADS = Map.ofEntries(
            entry("chest", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDVjNmRjMmJiZjUxYzM2Y2ZjNzcxNDU4NWE2YTU2ODNlZjJiMTRkNDdkOGZmNzE0NjU0YTg5M2Y1ZGE2MjIifX19"),
            entry("trapped_chest", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjc1YmNmMmU3NGRlZWQzN2EzMTlhMWY0MDRlNzBkMDZhNWYzNjBjYWNlZTk5YzcxMzQ2ZjM4NTZjYmQ3MmEifX19"),
            entry("ender_chest", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTZjYzQ4NmMyYmUxY2I5ZGZjYjJlNTNkZDlhM2U5YTg4M2JmYWRiMjdjYjk1NmYxODk2ZDYwMmI0MDY3In19fQ=="),
            entry("heavy_core", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTA2OWEwZjhiZGQ4YzA4NjkzZTYwYjkzYmRmZTgwMGFiZGY5NmViMWRjOWU3ZWZjMzg5ODczMTFkYTRlMGYzNCJ9fX0="),
            entry("dragon_head", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjhhYTNjNTNlNDY3NTIzYzZjM2Q4MzNiYmJlZTM3YTY2ZmMxNGYzMDUzMGYzOWE2YTljMDQ1N2ZmZTgwNWMyNSJ9fX0="),
            entry("skeleton_skull", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjQ3MzkwMzFmMjFiM2ExNDFjN2MzNDE2ZGUwZDdiMjk3OWFjNzVhOTI1ZTQzOWM2YmIwN2JiMTkwNjY3NTdmIn19fQ=="),
            entry("wither_skeleton_skull", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTViNDQxNjI5MGNhYTgyN2MxZDI4OGJiMTVlMGJjYjM1MzgwZmYyM2E5NmY0NjY5OThkMzA4YjM1YWQyMTI4YSJ9fX0="),
            entry("zombie_head", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTZmYzg1NGJiODRjZjRiNzY5NzI5Nzk3M2UwMmI3OWJjMTA2OTg0NjBiNTFhNjM5YzYwZTVlNDE3NzM0ZTExIn19fQ=="),
            entry("creeper_head", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjQyNTQ4MzhjMzNlYTIyN2ZmY2EyMjNkZGRhYWJmZTBiMDIxNWY3MGRhNjQ5ZTk0NDQ3N2Y0NDM3MGNhNjk1MiJ9fX0="),
            entry("piglin_head", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODNlMmMyZDY2OTY3ZDQ3MTViYmY2ZjE0ZDIyMDVhNjM3ZDNkMDFiM2ZkMTA2MzExYzYwNzM3ODAyZjJiZDc1NyJ9fX0=")
    );

    private HeadTextureRegistry() {
    }

    private static Map.Entry<String, HeadRef> entry(String item, String base64) {
        UUID uuid = UUID.nameUUIDFromBytes(("itemapi:head:" + item).getBytes(StandardCharsets.UTF_8));
        return Map.entry(item, new HeadRef(uuid, base64));
    }

    static @Nullable HeadRef find(String itemName) {
        return HEADS.get(itemName);
    }
}
