package me.usainsrht.itemapi.itemtext;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
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
        assertEquals("minecraft:block/straw_bed_head_up", strawBed.sprite().asString());
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
        assertEquals("minecraft:block/shelf_mushroom_top", shelfRef.sprite().asString());

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
}
