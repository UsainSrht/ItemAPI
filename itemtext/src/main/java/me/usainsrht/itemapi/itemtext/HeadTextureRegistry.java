package me.usainsrht.itemapi.itemtext;

import org.jspecify.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

/**
 * Registry of pre-baked, curated Base64 textures and deterministic UUIDs for
 * items and blocks
 * whose inventory representation is 3D or entity-based (chests, mob heads,
 * heavy core) so they
 * can render as 2D player head font glyphs with asynchronous client-side
 * texture loading and caching.
 */
final class HeadTextureRegistry {

    record HeadRef(UUID id, String base64Texture) implements SpriteTarget {
    }

    private static final String CHEST =
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDVjNmRjMmJiZjUxYzM2Y2ZjNzcxNDU4NWE2YTU2ODNlZjJiMTRkNDdkOGZmNzE0NjU0YTg5M2Y1ZGE2MjIifX19";
    private static final String ENDER_CHEST =
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTZjYzQ4NmMyYmUxY2I5ZGZjYjJlNTNkZDlhM2U5YTg4M2JmYWRiMjdjYjk1NmYxODk2ZDYwMmI0MDY3In19fQ==";
    private static final String COPPER_CHEST =
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmE3ODQ4ZGZlNTk3OTk1ZDA0ZTgyMmFjNTM4NzhhMjllZDgxZTFmMTUyODQ4ZjE4MjNjYjM1MjljNDdlMWNlZiJ9fX0=";
    private static final String EXPOSED_COPPER_CHEST =
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWMwOTJkZGQ2OGRmMjZhMzBhZmJhYjgyNDdhNTgxY2I5ODFmYjNlYzgxMTRhYjk1NTE5ZTE1MDgzYzQ4MDEzOSJ9fX0=";
    private static final String WEATHERED_COPPER_CHEST =
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2I0Yjk3MjMzNWFlNDM0YjAwNGVjN2FkMmUzNzA2Yzc1N2MxNGYyMTBiMmIxMTMyZDA4ZTM4NGVhNzIyZTQ1YiJ9fX0=";
    private static final String OXIDIZED_COPPER_CHEST =
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzNhZjNiZmRmNDg2ZWVmZjc4YzQ5ZGE0MDA5MDdiNDVhNGM0ZGI0ZDZmYjE1OTkwOWEzMjE3ZTA2OWM2NDBiMSJ9fX0=";
    private static final String COPPER_GOLEM =
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjQ4ZTBmOWViMjRhMTA2MDA3Mjk0MzhkZDk5OTk3ODM5ZTFhOTllMjc2ZDc2ZTMyZmQ0MzRiZGI1ZjU0Mjk2YyJ9fX0=";
    private static final String EXPOSED_COPPER_GOLEM =
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmRiMWM0NDViODBhYjliNGM5MWZjYTdjOGNhMzZkMDRiZTZiYWY1ZTI1MDdiYTFiNDE2MTYwYjg5MjVhODk1MyJ9fX0=";
    private static final String WEATHERED_COPPER_GOLEM =
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzRlODViNTJlMzc4ZDM5YzY2N2I4MDQ0OGU1M2ViZmY0ODhjNWYwNmNhZTNhZWEwMGQ5NTcwYmQ2Y2MzMmE5OSJ9fX0=";
    private static final String OXIDIZED_COPPER_GOLEM =
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjFiZjI0YWU4ODk1YmZkYjlkMGU3NWZlNGQ0MTVkNzZmOTk0ZGM3YTVmZjM2OGQ5NDE0OWQ3NjU5YzM5NmExYiJ9fX0=";
    private static final String HEAVY_CORE =
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTA2OWEwZjhiZGQ4YzA4NjkzZTYwYjkzYmRmZTgwMGFiZGY5NmViMWRjOWU3ZWZjMzg5ODczMTFkYTRlMGYzNCJ9fX0=";
    private static final String DRAGON_HEAD =
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjhhYTNjNTNlNDY3NTIzYzZjM2Q4MzNiYmJlZTM3YTY2ZmMxNGYzMDUzMGYzOWE2YTljMDQ1N2ZmZTgwNWMyNSJ9fX0=";
    private static final String SKELETON_SKULL =
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjQ3MzkwMzFmMjFiM2ExNDFjN2MzNDE2ZGUwZDdiMjk3OWFjNzVhOTI1ZTQzOWM2YmIwN2JiMTkwNjY3NTdmIn19fQ==";
    private static final String WITHER_SKELETON_SKULL =
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTViNDQxNjI5MGNhYTgyN2MxZDI4OGJiMTVlMGJjYjM1MzgwZmYyM2E5NmY0NjY5OThkMzA4YjM1YWQyMTI4YSJ9fX0=";
    private static final String ZOMBIE_HEAD =
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTZmYzg1NGJiODRjZjRiNzY5NzI5Nzk3M2UwMmI3OWJjMTA2OTg0NjBiNTFhNjM5YzYwZTVlNDE3NzM0ZTExIn19fQ==";
    private static final String CREEPER_HEAD =
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjQyNTQ4MzhjMzNlYTIyN2ZmY2EyMjNkZGRhYWJmZTBiMDIxNWY3MGRhNjQ5ZTk0NDQ3N2Y0NDM3MGNhNjk1MiJ9fX0=";
    private static final String PIGLIN_HEAD =
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODNlMmMyZDY2OTY3ZDQ3MTViYmY2ZjE0ZDIyMDVhNjM3ZDNkMDFiM2ZkMTA2MzExYzYwNzM3ODAyZjJiZDc1NyJ9fX0=";

    private static final Map<String, HeadRef> HEADS = Map.ofEntries(
            entry("chest", CHEST),
            entry("trapped_chest", CHEST),
            entry("ender_chest", ENDER_CHEST),
            entry("copper_chest", COPPER_CHEST),
            entry("exposed_copper_chest", EXPOSED_COPPER_CHEST),
            entry("weathered_copper_chest", WEATHERED_COPPER_CHEST),
            entry("oxidized_copper_chest", OXIDIZED_COPPER_CHEST),
            entry("waxed_copper_chest", COPPER_CHEST),
            entry("waxed_exposed_copper_chest", EXPOSED_COPPER_CHEST),
            entry("waxed_weathered_copper_chest", WEATHERED_COPPER_CHEST),
            entry("waxed_oxidized_copper_chest", OXIDIZED_COPPER_CHEST),
            entry("copper_golem", COPPER_GOLEM),
            entry("exposed_copper_golem", EXPOSED_COPPER_GOLEM),
            entry("weathered_copper_golem", WEATHERED_COPPER_GOLEM),
            entry("oxidized_copper_golem", OXIDIZED_COPPER_GOLEM),
            entry("copper_golem_statue", COPPER_GOLEM),
            entry("exposed_copper_golem_statue", EXPOSED_COPPER_GOLEM),
            entry("weathered_copper_golem_statue", WEATHERED_COPPER_GOLEM),
            entry("oxidized_copper_golem_statue", OXIDIZED_COPPER_GOLEM),
            entry("waxed_copper_golem", COPPER_GOLEM),
            entry("waxed_exposed_copper_golem", EXPOSED_COPPER_GOLEM),
            entry("waxed_weathered_copper_golem", WEATHERED_COPPER_GOLEM),
            entry("waxed_oxidized_copper_golem", OXIDIZED_COPPER_GOLEM),
            entry("waxed_copper_golem_statue", COPPER_GOLEM),
            entry("waxed_exposed_copper_golem_statue", EXPOSED_COPPER_GOLEM),
            entry("waxed_weathered_copper_golem_statue", WEATHERED_COPPER_GOLEM),
            entry("waxed_oxidized_copper_golem_statue", OXIDIZED_COPPER_GOLEM),
            entry("heavy_core", HEAVY_CORE),
            entry("dragon_head", DRAGON_HEAD),
            entry("skeleton_skull", SKELETON_SKULL),
            entry("wither_skeleton_skull", WITHER_SKELETON_SKULL),
            entry("zombie_head", ZOMBIE_HEAD),
            entry("creeper_head", CREEPER_HEAD),
            entry("piglin_head", PIGLIN_HEAD));

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
