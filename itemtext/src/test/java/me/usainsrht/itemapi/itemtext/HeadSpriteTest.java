package me.usainsrht.itemapi.itemtext;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Locale;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HeadSpriteTest {

    private static final List<String> EXPECTED_HEADS = List.of(
            "chest",
            "trapped_chest",
            "ender_chest",
            "copper_chest",
            "exposed_copper_chest",
            "weathered_copper_chest",
            "oxidized_copper_chest",
            "waxed_copper_chest",
            "waxed_exposed_copper_chest",
            "waxed_weathered_copper_chest",
            "waxed_oxidized_copper_chest",
            "copper_golem",
            "exposed_copper_golem",
            "weathered_copper_golem",
            "oxidized_copper_golem",
            "copper_golem_statue",
            "exposed_copper_golem_statue",
            "weathered_copper_golem_statue",
            "oxidized_copper_golem_statue",
            "waxed_copper_golem",
            "waxed_exposed_copper_golem",
            "waxed_weathered_copper_golem",
            "waxed_oxidized_copper_golem",
            "waxed_copper_golem_statue",
            "waxed_exposed_copper_golem_statue",
            "waxed_weathered_copper_golem_statue",
            "waxed_oxidized_copper_golem_statue",
            "heavy_core",
            "dragon_head",
            "skeleton_skull",
            "wither_skeleton_skull",
            "zombie_head",
            "creeper_head",
            "piglin_head",
            "lectern");

    @Test
    void testHeadTextureRegistryIntegrity() {
        for (String item : EXPECTED_HEADS) {
            HeadTextureRegistry.HeadRef head = HeadTextureRegistry.find(item);
            assertNotNull(head, "Missing HeadRef for " + item);
            assertNotNull(head.id(), "Missing UUID for " + item);
            assertNotNull(head.base64Texture(), "Missing base64 for " + item);

            // Verify deterministic UUID calculation
            assertEquals(
                    java.util.UUID.nameUUIDFromBytes(("itemapi:head:" + item).getBytes(StandardCharsets.UTF_8)),
                    head.id(),
                    "UUID for " + item + " should be deterministic");

            // Verify Base64 format and URL
            byte[] decoded = Base64.getDecoder().decode(head.base64Texture());
            String json = new String(decoded, StandardCharsets.UTF_8);
            assertTrue(
                    json.startsWith("{\"textures\":{\"SKIN\":{\"url\":\"http://textures.minecraft.net/texture/"),
                    "Texture JSON for " + item + " must point to official Mojang CDN: " + json);
        }
    }

    @Test
    void testHeadSpriteCreationWithDefaults() {
        ItemStack chest = stubItem(Material.CHEST);
        Component sprite = ItemSpriteFactory.create(chest, ItemTextOptions.defaults());
        String serialized = GsonComponentSerializer.gson().serialize(sprite);

        assertTrue(serialized.contains("\"hat\":true"), "Serialized component must have hat: true");
        assertTrue(serialized.contains("\"properties\":[{\"name\":\"textures\""), "Must contain textures property");
        assertTrue(serialized.contains(HeadTextureRegistry.find("chest").base64Texture()), "Must contain chest base64");
    }

    @Test
    void testLecternHeadSprite() {
        ItemStack lectern = stubItem(Material.LECTERN);
        Component sprite = ItemSpriteFactory.create(lectern, ItemTextOptions.defaults());
        String serialized = GsonComponentSerializer.gson().serialize(sprite);

        assertTrue(serialized.contains("\"hat\":true"), "Hat must be true for lectern");
        assertTrue(serialized.contains("\"properties\":[{\"name\":\"textures\""),
                "Textures property must exist for lectern");
        assertTrue(serialized.contains(HeadTextureRegistry.find("lectern").base64Texture()),
                "Must contain lectern base64");
    }

    @Test
    void testOfflineFallbackToggle() {
        ItemStack chest = stubItem(Material.CHEST);
        ItemTextOptions offlineOptions = ItemTextOptions.builder().usePlayerHeadsFor3DBlocks(false).build();
        Component sprite = ItemSpriteFactory.create(chest, offlineOptions);
        String serialized = GsonComponentSerializer.gson().serialize(sprite);

        assertEquals("{\"sprite\":\"minecraft:block/oak_planks\"}", serialized);

        ItemStack dragonHead = stubItem(Material.DRAGON_HEAD);
        Component dragonSprite = ItemSpriteFactory.create(dragonHead, offlineOptions);
        assertEquals("{\"sprite\":\"minecraft:block/purple_terracotta\"}",
                GsonComponentSerializer.gson().serialize(dragonSprite));

        ItemStack heavyCore = stubItem(Material.HEAVY_CORE);
        Component coreSprite = ItemSpriteFactory.create(heavyCore, offlineOptions);
        assertEquals("{\"sprite\":\"minecraft:block/heavy_core\"}",
                GsonComponentSerializer.gson().serialize(coreSprite));

        ItemStack copperChest = stubItem(Material.COPPER_CHEST);
        Component copperChestSprite = ItemSpriteFactory.create(copperChest, offlineOptions);
        assertEquals("{\"sprite\":\"minecraft:block/copper_block\"}",
                GsonComponentSerializer.gson().serialize(copperChestSprite));

        ItemStack lectern = stubItem(Material.LECTERN);
        Component lecternSprite = ItemSpriteFactory.create(lectern, offlineOptions);
        assertEquals("{\"sprite\":\"minecraft:block/lectern_front\"}",
                GsonComponentSerializer.gson().serialize(lecternSprite));
    }

    @Test
    void testAllHeadItemSprites() {
        Material[] materials = {
                Material.CHEST,
                Material.TRAPPED_CHEST,
                Material.ENDER_CHEST,
                Material.COPPER_CHEST,
                Material.EXPOSED_COPPER_CHEST,
                Material.WEATHERED_COPPER_CHEST,
                Material.OXIDIZED_COPPER_CHEST,
                Material.WAXED_COPPER_CHEST,
                Material.WAXED_EXPOSED_COPPER_CHEST,
                Material.WAXED_WEATHERED_COPPER_CHEST,
                Material.WAXED_OXIDIZED_COPPER_CHEST,
                Material.HEAVY_CORE,
                Material.DRAGON_HEAD,
                Material.SKELETON_SKULL,
                Material.WITHER_SKELETON_SKULL,
                Material.ZOMBIE_HEAD,
                Material.CREEPER_HEAD,
                Material.PIGLIN_HEAD,
                Material.LECTERN
        };

        for (Material mat : materials) {
            ItemStack item = stubItem(mat);
            Component sprite = ItemSpriteFactory.create(item, ItemTextOptions.defaults());
            String serialized = GsonComponentSerializer.gson().serialize(sprite);

            assertTrue(serialized.contains("\"hat\":true"), "Hat must be true for " + mat);
            assertTrue(serialized.contains("\"properties\":[{\"name\":\"textures\""),
                    "Textures property must exist for " + mat);
        }
    }

    @Test
    void testTrappedChestUsesChestTexture() {
        HeadTextureRegistry.HeadRef chest = HeadTextureRegistry.find("chest");
        HeadTextureRegistry.HeadRef trappedChest = HeadTextureRegistry.find("trapped_chest");
        assertNotNull(chest);
        assertNotNull(trappedChest);
        assertEquals(chest.base64Texture(), trappedChest.base64Texture(),
                "Trapped chest must use the exact same texture as chest");
        assertNotEquals(chest.id(), trappedChest.id(), "Trapped chest must have its own deterministic UUID");
    }

    @Test
    void testCopperGolemTextures() {
        String copperGolem = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjQ4ZTBmOWViMjRhMTA2MDA3Mjk0MzhkZDk5OTk3ODM5ZTFhOTllMjc2ZDc2ZTMyZmQ0MzRiZGI1ZjU0Mjk2YyJ9fX0=";
        String exposed = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmRiMWM0NDViODBhYjliNGM5MWZjYTdjOGNhMzZkMDRiZTZiYWY1ZTI1MDdiYTFiNDE2MTYwYjg5MjVhODk1MyJ9fX0=";
        String weathered = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzRlODViNTJlMzc4ZDM5YzY2N2I4MDQ0OGU1M2ViZmY0ODhjNWYwNmNhZTNhZWEwMGQ5NTcwYmQ2Y2MzMmE5OSJ9fX0=";
        String oxidized = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjFiZjI0YWU4ODk1YmZkYjlkMGU3NWZlNGQ0MTVkNzZmOTk0ZGM3YTVmZjM2OGQ5NDE0OWQ3NjU5YzM5NmExYiJ9fX0=";

        assertEquals(copperGolem, HeadTextureRegistry.find("copper_golem").base64Texture());
        assertEquals(copperGolem, HeadTextureRegistry.find("copper_golem_statue").base64Texture());
        assertEquals(copperGolem, HeadTextureRegistry.find("waxed_copper_golem").base64Texture());
        assertEquals(copperGolem, HeadTextureRegistry.find("waxed_copper_golem_statue").base64Texture());

        assertEquals(exposed, HeadTextureRegistry.find("exposed_copper_golem").base64Texture());
        assertEquals(exposed, HeadTextureRegistry.find("exposed_copper_golem_statue").base64Texture());
        assertEquals(exposed, HeadTextureRegistry.find("waxed_exposed_copper_golem").base64Texture());
        assertEquals(exposed, HeadTextureRegistry.find("waxed_exposed_copper_golem_statue").base64Texture());

        assertEquals(weathered, HeadTextureRegistry.find("weathered_copper_golem").base64Texture());
        assertEquals(weathered, HeadTextureRegistry.find("weathered_copper_golem_statue").base64Texture());
        assertEquals(weathered, HeadTextureRegistry.find("waxed_weathered_copper_golem").base64Texture());
        assertEquals(weathered, HeadTextureRegistry.find("waxed_weathered_copper_golem_statue").base64Texture());

        assertEquals(oxidized, HeadTextureRegistry.find("oxidized_copper_golem").base64Texture());
        assertEquals(oxidized, HeadTextureRegistry.find("oxidized_copper_golem_statue").base64Texture());
        assertEquals(oxidized, HeadTextureRegistry.find("waxed_oxidized_copper_golem").base64Texture());
        assertEquals(oxidized, HeadTextureRegistry.find("waxed_oxidized_copper_golem_statue").base64Texture());
    }

    @Test
    void testShieldSprite() {
        ItemStack shield = stubItem(Material.SHIELD);
        Component sprite = ItemSpriteFactory.create(shield, ItemTextOptions.defaults());
        String serialized = GsonComponentSerializer.gson().serialize(sprite);
        assertEquals("{\"atlas\":\"minecraft:gui\",\"sprite\":\"minecraft:mob_effect/resistance\"}", serialized);
    }

    @Test
    void testWildernessBound263Lookups() {
        // Poplar wood pattern derivation
        assertEquals("poplar_planks", BlockSpriteLookup.resolve("poplar_stairs"));
        assertEquals("poplar_planks", BlockSpriteLookup.resolve("poplar_slab"));
        assertEquals("poplar_planks", BlockSpriteLookup.resolve("poplar_fence"));
        assertEquals("poplar_planks", BlockSpriteLookup.resolve("poplar_fence_gate"));
        assertEquals("poplar_planks", BlockSpriteLookup.resolve("poplar_button"));
        assertEquals("poplar_planks", BlockSpriteLookup.resolve("poplar_pressure_plate"));
        assertEquals("poplar_log", BlockSpriteLookup.resolve("poplar_wood"));
        assertEquals("stripped_poplar_log", BlockSpriteLookup.resolve("stripped_poplar_wood"));

        // Wool and Concrete stairs/slabs
        assertEquals("red_wool", BlockSpriteLookup.resolve("red_wool_stairs"));
        assertEquals("red_wool", BlockSpriteLookup.resolve("red_wool_slab"));
        assertEquals("blue_concrete", BlockSpriteLookup.resolve("blue_concrete_stairs"));
        assertEquals("blue_concrete", BlockSpriteLookup.resolve("blue_concrete_slab"));

        // Straw bed override
        ItemSpriteOverrides.SpriteRef strawBed = ItemSpriteOverrides.sprite("straw_bed");
        assertNotNull(strawBed);
        assertEquals("minecraft:block/straw_bed", strawBed.sprite().asString());
    }

    @Test
    void testAutomatedForwardCompatibilityRouting() {
        SpriteTarget jackSprite = ItemSpriteFactory.resolve(stubItem(Material.JACK_O_LANTERN), ItemTextOptions.defaults());
        assertInstanceOf(ItemSpriteOverrides.SpriteRef.class, jackSprite);
        ItemSpriteOverrides.SpriteRef jackRef = (ItemSpriteOverrides.SpriteRef) jackSprite;
        assertEquals("minecraft:blocks", jackRef.atlas().asString());
        assertEquals("minecraft:block/jack_o_lantern", jackRef.sprite().asString());

        SpriteTarget seaLanternSprite = ItemSpriteFactory.resolve(stubItem(Material.SEA_LANTERN), ItemTextOptions.defaults());
        assertInstanceOf(ItemSpriteOverrides.SpriteRef.class, seaLanternSprite);
        ItemSpriteOverrides.SpriteRef seaLanternRef = (ItemSpriteOverrides.SpriteRef) seaLanternSprite;
        assertEquals("minecraft:blocks", seaLanternRef.atlas().asString());
        assertEquals("minecraft:block/sea_lantern", seaLanternRef.sprite().asString());

        SpriteTarget shelfSprite = ItemSpriteFactory.resolve(stubItem(Material.SHELF_MUSHROOM), ItemTextOptions.defaults());
        assertInstanceOf(ItemSpriteOverrides.SpriteRef.class, shelfSprite);
        ItemSpriteOverrides.SpriteRef shelfRef = (ItemSpriteOverrides.SpriteRef) shelfSprite;
        assertEquals("minecraft:blocks", shelfRef.atlas().asString());
        assertEquals("minecraft:block/shelf_mushroom_stage0", shelfRef.sprite().asString());

        assertFalse(BlockSpriteLookup.isKnownMaterial("fictional_wood_planks_xyz"));

        // Doors and signs pattern checks for future wood types
        assertTrue(ItemSpriteFactory.isItemTextureBlock("willow_door"));
        assertFalse(ItemSpriteFactory.isItemTextureBlock("willow_trapdoor"));
        assertTrue(ItemSpriteFactory.isItemTextureBlock("willow_sign"));
        assertTrue(ItemSpriteFactory.isItemTextureBlock("willow_hanging_sign"));
        assertTrue(ItemSpriteFactory.isItemTextureBlock("copper_lantern"));

        // Items and item-texture blocks resolved to items atlas
        SpriteTarget doorTarget = ItemSpriteFactory.resolve(stubItem(Material.POPLAR_DOOR), ItemTextOptions.defaults());
        assertInstanceOf(ItemSpriteOverrides.SpriteRef.class, doorTarget);
        ItemSpriteOverrides.SpriteRef doorRef = (ItemSpriteOverrides.SpriteRef) doorTarget;
        assertEquals("minecraft:items", doorRef.atlas().asString());
        assertEquals("minecraft:item/poplar_door", doorRef.sprite().asString());

        SpriteTarget signTarget = ItemSpriteFactory.resolve(stubItem(Material.POPLAR_SIGN), ItemTextOptions.defaults());
        assertInstanceOf(ItemSpriteOverrides.SpriteRef.class, signTarget);
        ItemSpriteOverrides.SpriteRef signRef = (ItemSpriteOverrides.SpriteRef) signTarget;
        assertEquals("minecraft:items", signRef.atlas().asString());
        assertEquals("minecraft:item/poplar_sign", signRef.sprite().asString());

        SpriteTarget boatTarget = ItemSpriteFactory.resolve(stubItem(Material.POPLAR_BOAT), ItemTextOptions.defaults());
        assertInstanceOf(ItemSpriteOverrides.SpriteRef.class, boatTarget);
        ItemSpriteOverrides.SpriteRef boatRef = (ItemSpriteOverrides.SpriteRef) boatTarget;
        assertEquals("minecraft:items", boatRef.atlas().asString());
        assertEquals("minecraft:item/poplar_boat", boatRef.sprite().asString());
    }

    @Test
    void testCushionsAndMaps263() {
        // Cushions must resolve to items atlas
        for (String color : new String[]{"white", "black", "red", "blue", "yellow", "cyan"}) {
            Material mat = Material.getMaterial(color.toUpperCase(Locale.ROOT) + "_CUSHION");
            assertNotNull(mat, "Missing cushion material: " + color);
            SpriteTarget target = ItemSpriteFactory.resolve(stubItem(mat), ItemTextOptions.defaults());
            assertInstanceOf(ItemSpriteOverrides.SpriteRef.class, target);
            ItemSpriteOverrides.SpriteRef ref = (ItemSpriteOverrides.SpriteRef) target;
            assertEquals("minecraft:items", ref.atlas().asString());
            assertEquals("minecraft:item/" + color + "_cushion", ref.sprite().asString());
        }

        // Structure maps must resolve to items atlas
        String[] maps = {"desert_pyramid_map", "ocean_monument_map", "buried_treasure_map", "woodland_mansion_map"};
        for (String mapName : maps) {
            Material mat = Material.getMaterial(mapName.toUpperCase(Locale.ROOT));
            assertNotNull(mat, "Missing map material: " + mapName);
            SpriteTarget target = ItemSpriteFactory.resolve(stubItem(mat), ItemTextOptions.defaults());
            assertInstanceOf(ItemSpriteOverrides.SpriteRef.class, target);
            ItemSpriteOverrides.SpriteRef ref = (ItemSpriteOverrides.SpriteRef) target;
            assertEquals("minecraft:items", ref.atlas().asString());
            assertEquals("minecraft:item/" + mapName, ref.sprite().asString());
        }

        // Poplar wall sign / wall hanging sign / leaves / potted sapling
        SpriteTarget wallSignTarget = ItemSpriteFactory.resolve(stubItem(Material.POPLAR_WALL_SIGN), ItemTextOptions.defaults());
        assertInstanceOf(ItemSpriteOverrides.SpriteRef.class, wallSignTarget);
        assertEquals("minecraft:item/poplar_sign", ((ItemSpriteOverrides.SpriteRef) wallSignTarget).sprite().asString());

        SpriteTarget wallHangingTarget = ItemSpriteFactory.resolve(stubItem(Material.POPLAR_WALL_HANGING_SIGN), ItemTextOptions.defaults());
        assertInstanceOf(ItemSpriteOverrides.SpriteRef.class, wallHangingTarget);
        assertEquals("minecraft:item/poplar_hanging_sign", ((ItemSpriteOverrides.SpriteRef) wallHangingTarget).sprite().asString());

        SpriteTarget pottedTarget = ItemSpriteFactory.resolve(stubItem(Material.POTTED_POPLAR_SAPLING), ItemTextOptions.defaults());
        assertInstanceOf(ItemSpriteOverrides.SpriteRef.class, pottedTarget);
        assertEquals("minecraft:block/poplar_sapling", ((ItemSpriteOverrides.SpriteRef) pottedTarget).sprite().asString());

        // Poplar leaves
        SpriteTarget orangeLeaves = ItemSpriteFactory.resolve(stubItem(Material.ORANGE_POPLAR_LEAVES), ItemTextOptions.defaults());
        assertInstanceOf(ItemSpriteOverrides.SpriteRef.class, orangeLeaves);
        assertEquals("minecraft:block/orange_poplar_leaves", ((ItemSpriteOverrides.SpriteRef) orangeLeaves).sprite().asString());

        // Shelves
        SpriteTarget acaciaShelf = ItemSpriteFactory.resolve(stubItem(Material.ACACIA_SHELF), ItemTextOptions.defaults());
        assertInstanceOf(ItemSpriteOverrides.SpriteRef.class, acaciaShelf);
        assertEquals("minecraft:block/acacia_shelf", ((ItemSpriteOverrides.SpriteRef) acaciaShelf).sprite().asString());
    }

    private ItemStack stubItem(Material material) {
        return new ItemStack() {
            @Override
            public Material getType() {
                return material;
            }

            @Override
            public int getAmount() {
                return 1;
            }
        };
    }

    @Test
    void testMobHeadRegistryIntegrity() {
        String[] sampleMobs = {
                "zombie", "skeleton", "creeper", "spider", "ender_dragon",
                "pig", "cow", "sheep", "chicken", "warden", "allay", "breeze", "bogged"
        };
        for (String mob : sampleMobs) {
            MobHeadRegistry.MobHead head = MobHeadRegistry.find(mob);
            assertNotNull(head, "Missing mob head for " + mob);
            assertNotNull(head.id(), "Missing UUID for " + mob);
            assertNotNull(head.base64Texture(), "Missing base64 for " + mob);

            // Deterministic UUID
            assertEquals(
                    java.util.UUID.nameUUIDFromBytes(("itemapi:mob:" + mob).getBytes(StandardCharsets.UTF_8)),
                    head.id(),
                    "UUID for " + mob + " should be deterministic");

            // Base64 format and URL
            byte[] decoded = Base64.getDecoder().decode(head.base64Texture());
            String json = new String(decoded, StandardCharsets.UTF_8);
            assertTrue(
                    json.startsWith("{\"textures\":{\"SKIN\":{\"url\":\"http://textures.minecraft.net/texture/"),
                    "Texture JSON for " + mob + " must point to official Mojang CDN: " + json);
        }

        // Test namespace and case normalization
        assertEquals(MobHeadRegistry.find("zombie"), MobHeadRegistry.find("minecraft:zombie"));
        assertEquals(MobHeadRegistry.find("zombie"), MobHeadRegistry.find("ZOMBIE"));
        assertEquals(MobHeadRegistry.find("ender_dragon"), MobHeadRegistry.find("ender-dragon"));
        assertEquals(MobHeadRegistry.find("ender_dragon"), MobHeadRegistry.find("minecraft:ender_dragon"));
    }

    @Test
    void testMobHeadCreationAndHatLayers() {
        Component zombieDefault = ItemText.mobHead("zombie");
        String zombieJson = GsonComponentSerializer.gson().serialize(zombieDefault);
        assertTrue(zombieJson.contains("\"hat\":true"), "Default mob head must have hat: true");
        assertTrue(zombieJson.contains(MobHeadRegistry.find("zombie").base64Texture()));

        Component zombieNoHat = ItemText.mobHead("zombie", false);
        String noHatJson = GsonComponentSerializer.gson().serialize(zombieNoHat);
        assertTrue(noHatJson.contains("\"hat\":false"), "Explicit hat false must serialize with hat: false");

        // Non-existent mob returns empty
        assertEquals(Component.empty(), ItemText.mobHead("fictional_creature_xyz"));
    }

    @Test
    void testMobHeadMiniMessageTagResolver() {
        Component parsed = MiniMessage.miniMessage().deserialize("<mob_head:creeper>", ItemText.mobHeadTagResolver());
        String json = GsonComponentSerializer.gson().serialize(parsed);
        assertTrue(json.contains("\"hat\":true"));
        assertTrue(json.contains(MobHeadRegistry.find("creeper").base64Texture()));

        Component parsedNoHat = MiniMessage.miniMessage().deserialize("<mob_head:warden:no_hat>", ItemText.mobHeadTagResolver());
        String noHatJson = GsonComponentSerializer.gson().serialize(parsedNoHat);
        assertTrue(noHatJson.contains("\"hat\":false"));
        assertTrue(noHatJson.contains(MobHeadRegistry.find("warden").base64Texture()));

        Component parsedFalse = MiniMessage.miniMessage().deserialize("<mob_head:allay:false>", ItemText.mobHeadTagResolver());
        String falseJson = GsonComponentSerializer.gson().serialize(parsedFalse);
        assertTrue(falseJson.contains("\"hat\":false"));
        assertTrue(falseJson.contains(MobHeadRegistry.find("allay").base64Texture()));
    }

    @Test
    void testSpawnEggsUntouched() {
        // Explicit constraint: do NOT touch spawn eggs! They must remain standard item sprites.
        ItemStack zombieEgg = stubItem(Material.ZOMBIE_SPAWN_EGG);
        SpriteTarget target = ItemSpriteFactory.resolve(zombieEgg, ItemTextOptions.defaults());
        assertInstanceOf(ItemSpriteOverrides.SpriteRef.class, target);
        ItemSpriteOverrides.SpriteRef spriteRef = (ItemSpriteOverrides.SpriteRef) target;
        assertEquals("minecraft:items", spriteRef.atlas().asString());
        assertEquals("minecraft:item/zombie_spawn_egg", spriteRef.sprite().asString());
    }
}
