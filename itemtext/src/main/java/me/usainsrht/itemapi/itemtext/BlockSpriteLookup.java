package me.usainsrht.itemapi.itemtext;

import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.Set;

/**
 * Resolves a holdable block item id to a real blocks-atlas sprite name.
 * Tries exact match, then face suffixes, then shape/wood pattern derivation.
 */
final class BlockSpriteLookup {

    private static final String[] FACE_SUFFIXES = {"_front", "_side", "_top", "_end", "_bottom"};

    private static final Set<String> WOOD = Set.of(
            "acacia", "bamboo", "birch", "cherry", "crimson", "dark_oak",
            "jungle", "mangrove", "oak", "pale_oak", "spruce", "warped"
    );

    /**
     * Irregular item id → block sprite when face suffixes / patterns are not enough.
     */
    private static final Map<String, String> SPECIALS = Map.ofEntries(
            Map.entry("smooth_sandstone", "sandstone_top"),
            Map.entry("smooth_red_sandstone", "red_sandstone_top"),
            Map.entry("smooth_quartz", "quartz_block_bottom"),
            Map.entry("smooth_stone_slab", "smooth_stone_slab_side"),
            Map.entry("quartz_pillar", "quartz_pillar_side"),
            Map.entry("purpur_pillar", "purpur_pillar_side"),
            Map.entry("sticky_piston", "piston_top_sticky"),
            Map.entry("chipped_anvil", "chipped_anvil_top"),
            Map.entry("damaged_anvil", "damaged_anvil_top"),
            Map.entry("anvil", "anvil_top"),
            Map.entry("crafting_table", "crafting_table_front"),
            Map.entry("cartography_table", "cartography_table_side3"),
            Map.entry("fletching_table", "fletching_table_front"),
            Map.entry("smithing_table", "smithing_table_front"),
            Map.entry("respawn_anchor", "respawn_anchor_side0"),
            Map.entry("daylight_detector", "daylight_detector_top"),
            Map.entry("hopper", "hopper_outside"),
            Map.entry("moss_carpet", "moss_block"),
            Map.entry("petrified_oak_slab", "oak_planks"),
            Map.entry("light_weighted_pressure_plate", "gold_block"),
            Map.entry("heavy_weighted_pressure_plate", "iron_block"),
            Map.entry("suspicious_sand", "suspicious_sand_0"),
            Map.entry("suspicious_gravel", "suspicious_gravel_0"),
            Map.entry("test_block", "test_block_accept")
    );

    private BlockSpriteLookup() {
    }

    static String resolve(String itemName) {
        if (BlockAtlasSprites.contains(itemName)) {
            return itemName;
        }

        // Prefer full glass faces over pane edge textures (*_pane_top stripe)
        String glass = glassBlockSprite(itemName);
        if (glass != null) {
            return glass;
        }

        String special = SPECIALS.get(itemName);
        if (special != null && BlockAtlasSprites.contains(special)) {
            return special;
        }

        String faced = withFaceSuffix(itemName);
        if (faced != null) {
            return faced;
        }

        // snow_block → snow, dried_kelp_block → dried_kelp_side, magma_block → magma
        if (itemName.endsWith("_block")) {
            String stem = itemName.substring(0, itemName.length() - "_block".length());
            if (BlockAtlasSprites.contains(stem)) {
                return stem;
            }
            String stemFaced = withFaceSuffix(stem);
            if (stemFaced != null) {
                return stemFaced;
            }
        }

        String derived = derive(itemName);
        if (derived != null && BlockAtlasSprites.contains(derived)) {
            return derived;
        }

        return itemName;
    }

    private static @Nullable String withFaceSuffix(String name) {
        for (String suffix : FACE_SUFFIXES) {
            String candidate = name + suffix;
            if (BlockAtlasSprites.contains(candidate)) {
                return candidate;
            }
        }
        return null;
    }

    private static @Nullable String glassBlockSprite(String name) {
        if ("glass_pane".equals(name)) {
            return "glass";
        }
        if (name.endsWith("_stained_glass_pane")) {
            String glass = name.substring(0, name.length() - "_pane".length());
            if (BlockAtlasSprites.contains(glass)) {
                return glass;
            }
        }
        return null;
    }

    private static @Nullable String derive(String name) {
        // infested_stone_bricks → stone_bricks
        if (name.startsWith("infested_")) {
            return name.substring("infested_".length());
        }

        // oak_wood / stripped_oak_wood → *_log
        if (name.endsWith("_wood")) {
            return name.substring(0, name.length() - "_wood".length()) + "_log";
        }
        // crimson_hyphae / stripped_warped_hyphae → *_stem
        if (name.endsWith("_hyphae")) {
            return name.substring(0, name.length() - "_hyphae".length()) + "_stem";
        }

        // colored carpets → wool
        if (name.endsWith("_carpet") && !name.equals("pale_moss_carpet")) {
            return name.substring(0, name.length() - "_carpet".length()) + "_wool";
        }

        if (name.endsWith("_fence_gate")) {
            return woodPlanks(name.substring(0, name.length() - "_fence_gate".length()));
        }
        if (name.endsWith("_fence")) {
            String base = name.substring(0, name.length() - "_fence".length());
            if ("nether_brick".equals(base)) {
                return "nether_bricks";
            }
            return woodPlanks(base);
        }

        if (name.endsWith("_button")) {
            String base = name.substring(0, name.length() - "_button".length());
            if ("stone".equals(base) || "polished_blackstone".equals(base)) {
                return base;
            }
            return woodPlanks(base);
        }

        if (name.endsWith("_pressure_plate")) {
            String base = name.substring(0, name.length() - "_pressure_plate".length());
            if ("stone".equals(base) || "polished_blackstone".equals(base)) {
                return base;
            }
            return woodPlanks(base);
        }

        String shapeMaterial = stripShapeSuffix(name);
        if (shapeMaterial != null) {
            return mapShapeMaterial(shapeMaterial);
        }

        return null;
    }

    private static @Nullable String stripShapeSuffix(String name) {
        if (name.endsWith("_stairs")) {
            return name.substring(0, name.length() - "_stairs".length());
        }
        if (name.endsWith("_slab")) {
            return name.substring(0, name.length() - "_slab".length());
        }
        if (name.endsWith("_wall")) {
            return name.substring(0, name.length() - "_wall".length());
        }
        return null;
    }

    private static String mapShapeMaterial(String base) {
        return switch (base) {
            case "deepslate_tile" -> "deepslate_tiles";
            case "quartz" -> "quartz_block_side";
            case "smooth_quartz" -> "quartz_block_bottom";
            case "purpur" -> "purpur_block";
            case "smooth_sandstone" -> "sandstone_top";
            case "smooth_red_sandstone" -> "red_sandstone_top";
            case "smooth_stone" -> "smooth_stone_slab_side";
            case "cut_sandstone" -> "cut_sandstone";
            case "cut_red_sandstone" -> "cut_red_sandstone";
            case "petrified_oak" -> "oak_planks";
            case "bamboo_mosaic" -> "bamboo_mosaic";
            default -> {
                String planks = woodPlanks(base);
                if (planks != null) {
                    yield planks;
                }
                // sulfur_brick / cinnabar_brick / end_stone_brick / stone_brick → *_bricks
                if ("brick".equals(base) || base.endsWith("_brick")) {
                    yield base + "s";
                }
                yield base;
            }
        };
    }

    private static @Nullable String woodPlanks(String wood) {
        if (WOOD.contains(wood)) {
            return wood + "_planks";
        }
        return null;
    }
}
