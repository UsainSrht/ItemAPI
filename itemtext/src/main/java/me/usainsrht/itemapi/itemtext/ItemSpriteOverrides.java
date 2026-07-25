package me.usainsrht.itemapi.itemtext;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.object.SpriteObjectContents;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Hardcoded {@link org.bukkit.inventory.ItemType} → atlas sprite mappings for
 * holdable items whose inventory model is 3D / entity-based, or whose sprite
 * name differs from the item id (multi-frame items, multi-face blocks).
 */
final class ItemSpriteOverrides {

    private static final Key ITEMS_ATLAS = Key.key("minecraft", "items");
    private static final Key BLOCKS_ATLAS = SpriteObjectContents.DEFAULT_ATLAS;
    private static final Key MAP_DECORATIONS_ATLAS = Key.key("minecraft", "map_decorations");
    private static final Key SHIELD_PATTERNS_ATLAS = Key.key("minecraft", "shield_patterns");
    private static final Key DECORATED_POT_ATLAS = Key.key("minecraft", "decorated_pot");

    private static final Map<String, SpriteRef> BY_ITEM = build();

    private ItemSpriteOverrides() {
    }

    static @Nullable SpriteRef sprite(String itemName) {
        return BY_ITEM.get(itemName);
    }

    private static Map<String, SpriteRef> build() {
        Map<String, SpriteRef> map = new HashMap<>();

        // Multi-frame / stateful item sprites (inventory default frame)
        putItem(map, "compass", "compass_16");
        putItem(map, "recovery_compass", "recovery_compass_16");
        putItem(map, "clock", "clock_00");
        putItem(map, "crossbow", "crossbow_standby");
        putItem(map, "tipped_arrow", "tipped_arrow_base");
        putItem(map, "light", "light_00");
        putItem(map, "enchanted_golden_apple", "golden_apple");
        putItem(map, "debug_stick", "stick");

        // Banners: no item/block sprite — use map decoration icons
        for (String color : colors()) {
            put(map, color + "_banner", MAP_DECORATIONS_ATLAS, color + "_banner");
        }

        // Chests: entity models — approximate with recognizable block faces
        putBlock(map, "chest", "oak_planks");
        putBlock(map, "trapped_chest", "oak_planks");
        putBlock(map, "ender_chest", "obsidian");
        putBlock(map, "copper_chest", "copper_block");
        putBlock(map, "exposed_copper_chest", "exposed_copper");
        putBlock(map, "weathered_copper_chest", "weathered_copper");
        putBlock(map, "oxidized_copper_chest", "oxidized_copper");
        putBlock(map, "waxed_copper_chest", "copper_block");
        putBlock(map, "waxed_exposed_copper_chest", "exposed_copper");
        putBlock(map, "waxed_weathered_copper_chest", "weathered_copper");
        putBlock(map, "waxed_oxidized_copper_chest", "oxidized_copper");

        // Copper golem statues (3D)
        putBlock(map, "copper_golem_statue", "copper_block");
        putBlock(map, "exposed_copper_golem_statue", "exposed_copper");
        putBlock(map, "weathered_copper_golem_statue", "weathered_copper");
        putBlock(map, "oxidized_copper_golem_statue", "oxidized_copper");
        putBlock(map, "waxed_copper_golem_statue", "copper_block");
        putBlock(map, "waxed_exposed_copper_golem_statue", "exposed_copper");
        putBlock(map, "waxed_weathered_copper_golem_statue", "weathered_copper");
        putBlock(map, "waxed_oxidized_copper_golem_statue", "oxidized_copper");

        // Beds (entity models)
        for (String color : colors()) {
            putBlock(map, color + "_bed", color + "_bed_head_up");
        }

        // Heads / skulls (3D) — approximate recognizable blocks
        putBlock(map, "skeleton_skull", "bone_block_side");
        putBlock(map, "wither_skeleton_skull", "coal_block");
        putBlock(map, "zombie_head", "green_terracotta");
        putBlock(map, "creeper_head", "lime_concrete");
        putBlock(map, "dragon_head", "purple_terracotta");
        putBlock(map, "piglin_head", "orange_terracotta");
        // player_head uses ObjectContents.playerHead when a profile is present

        // Other entity / multi-texture holdables
        put(map, "shield", SHIELD_PATTERNS_ATLAS, "entity/shield/base");
        put(map, "decorated_pot", DECORATED_POT_ATLAS, "entity/decorated_pot/decorated_pot_side");
        putBlock(map, "vault", "vault_front_off");
        putBlock(map, "trial_spawner", "trial_spawner_side_inactive");
        putBlock(map, "dried_ghast", "dried_ghast_hydration_0_north");

        // Waxed copper reuses unwaxed textures (doors/lanterns/chains are item sprites)
        // Other multi-face blocks are resolved by BlockSpriteLookup.
        putBlock(map, "waxed_copper_block", "copper_block");
        putBlock(map, "waxed_chiseled_copper", "chiseled_copper");
        putBlock(map, "waxed_copper_grate", "copper_grate");
        putBlock(map, "waxed_cut_copper", "cut_copper");
        putBlock(map, "waxed_cut_copper_slab", "cut_copper");
        putBlock(map, "waxed_cut_copper_stairs", "cut_copper");
        putBlock(map, "waxed_copper_bulb", "copper_bulb");
        putItem(map, "waxed_copper_door", "copper_door");
        putBlock(map, "waxed_copper_trapdoor", "copper_trapdoor");
        putBlock(map, "waxed_copper_bars", "copper_bars");
        putItem(map, "waxed_copper_chain", "copper_chain");
        putItem(map, "waxed_copper_lantern", "copper_lantern");
        putBlock(map, "waxed_lightning_rod", "lightning_rod");

        putBlock(map, "waxed_exposed_copper", "exposed_copper");
        putBlock(map, "waxed_exposed_chiseled_copper", "exposed_chiseled_copper");
        putBlock(map, "waxed_exposed_copper_grate", "exposed_copper_grate");
        putBlock(map, "waxed_exposed_cut_copper", "exposed_cut_copper");
        putBlock(map, "waxed_exposed_cut_copper_slab", "exposed_cut_copper");
        putBlock(map, "waxed_exposed_cut_copper_stairs", "exposed_cut_copper");
        putBlock(map, "waxed_exposed_copper_bulb", "exposed_copper_bulb");
        putItem(map, "waxed_exposed_copper_door", "exposed_copper_door");
        putBlock(map, "waxed_exposed_copper_trapdoor", "exposed_copper_trapdoor");
        putBlock(map, "waxed_exposed_copper_bars", "exposed_copper_bars");
        putItem(map, "waxed_exposed_copper_chain", "exposed_copper_chain");
        putItem(map, "waxed_exposed_copper_lantern", "exposed_copper_lantern");
        putBlock(map, "waxed_exposed_lightning_rod", "exposed_lightning_rod");

        putBlock(map, "waxed_weathered_copper", "weathered_copper");
        putBlock(map, "waxed_weathered_chiseled_copper", "weathered_chiseled_copper");
        putBlock(map, "waxed_weathered_copper_grate", "weathered_copper_grate");
        putBlock(map, "waxed_weathered_cut_copper", "weathered_cut_copper");
        putBlock(map, "waxed_weathered_cut_copper_slab", "weathered_cut_copper");
        putBlock(map, "waxed_weathered_cut_copper_stairs", "weathered_cut_copper");
        putBlock(map, "waxed_weathered_copper_bulb", "weathered_copper_bulb");
        putItem(map, "waxed_weathered_copper_door", "weathered_copper_door");
        putBlock(map, "waxed_weathered_copper_trapdoor", "weathered_copper_trapdoor");
        putBlock(map, "waxed_weathered_copper_bars", "weathered_copper_bars");
        putItem(map, "waxed_weathered_copper_chain", "weathered_copper_chain");
        putItem(map, "waxed_weathered_copper_lantern", "weathered_copper_lantern");
        putBlock(map, "waxed_weathered_lightning_rod", "weathered_lightning_rod");

        putBlock(map, "waxed_oxidized_copper", "oxidized_copper");
        putBlock(map, "waxed_oxidized_chiseled_copper", "oxidized_chiseled_copper");
        putBlock(map, "waxed_oxidized_copper_grate", "oxidized_copper_grate");
        putBlock(map, "waxed_oxidized_cut_copper", "oxidized_cut_copper");
        putBlock(map, "waxed_oxidized_cut_copper_slab", "oxidized_cut_copper");
        putBlock(map, "waxed_oxidized_cut_copper_stairs", "oxidized_cut_copper");
        putBlock(map, "waxed_oxidized_copper_bulb", "oxidized_copper_bulb");
        putItem(map, "waxed_oxidized_copper_door", "oxidized_copper_door");
        putBlock(map, "waxed_oxidized_copper_trapdoor", "oxidized_copper_trapdoor");
        putBlock(map, "waxed_oxidized_copper_bars", "oxidized_copper_bars");
        putItem(map, "waxed_oxidized_copper_chain", "oxidized_copper_chain");
        putItem(map, "waxed_oxidized_copper_lantern", "oxidized_copper_lantern");
        putBlock(map, "waxed_oxidized_lightning_rod", "oxidized_lightning_rod");

        return Map.copyOf(map);
    }

    private static void putItem(Map<String, SpriteRef> map, String item, String sprite) {
        put(map, item, ITEMS_ATLAS, "item/" + sprite);
    }

    private static void putBlock(Map<String, SpriteRef> map, String item, String sprite) {
        put(map, item, BLOCKS_ATLAS, "block/" + sprite);
    }

    private static void put(Map<String, SpriteRef> map, String item, Key atlas, String sprite) {
        map.put(item, new SpriteRef(atlas, Key.key("minecraft", sprite)));
    }

    private static String[] colors() {
        return new String[]{
                "white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray",
                "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black"
        };
    }

    record SpriteRef(Key atlas, Key sprite) {
    }
}
