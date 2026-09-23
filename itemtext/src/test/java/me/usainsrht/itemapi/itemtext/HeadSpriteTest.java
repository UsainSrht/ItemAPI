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
            "heavy_core",
            "dragon_head",
            "skeleton_skull",
            "wither_skeleton_skull",
            "zombie_head",
            "creeper_head",
            "piglin_head"
    );

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
                    "UUID for " + item + " should be deterministic"
            );

            // Verify Base64 format and URL
            byte[] decoded = Base64.getDecoder().decode(head.base64Texture());
            String json = new String(decoded, StandardCharsets.UTF_8);
            assertTrue(
                    json.startsWith("{\"textures\":{\"SKIN\":{\"url\":\"http://textures.minecraft.net/texture/"),
                    "Texture JSON for " + item + " must point to official Mojang CDN: " + json
            );
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
    void testOfflineFallbackToggle() {
        ItemStack chest = stubItem(Material.CHEST);
        ItemTextOptions offlineOptions = ItemTextOptions.builder().usePlayerHeadsFor3DBlocks(false).build();
        Component sprite = ItemSpriteFactory.create(chest, offlineOptions);
        String serialized = GsonComponentSerializer.gson().serialize(sprite);

        assertEquals("{\"sprite\":\"minecraft:block/oak_planks\"}", serialized);

        ItemStack dragonHead = stubItem(Material.DRAGON_HEAD);
        Component dragonSprite = ItemSpriteFactory.create(dragonHead, offlineOptions);
        assertEquals("{\"sprite\":\"minecraft:block/purple_terracotta\"}", GsonComponentSerializer.gson().serialize(dragonSprite));

        ItemStack heavyCore = stubItem(Material.HEAVY_CORE);
        Component coreSprite = ItemSpriteFactory.create(heavyCore, offlineOptions);
        assertEquals("{\"sprite\":\"minecraft:block/heavy_core\"}", GsonComponentSerializer.gson().serialize(coreSprite));

        ItemStack copperChest = stubItem(Material.COPPER_CHEST);
        Component copperChestSprite = ItemSpriteFactory.create(copperChest, offlineOptions);
        assertEquals("{\"sprite\":\"minecraft:block/copper_block\"}", GsonComponentSerializer.gson().serialize(copperChestSprite));
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
                Material.PIGLIN_HEAD
        };

        for (Material mat : materials) {
            ItemStack item = stubItem(mat);
            Component sprite = ItemSpriteFactory.create(item, ItemTextOptions.defaults());
            String serialized = GsonComponentSerializer.gson().serialize(sprite);

            assertTrue(serialized.contains("\"hat\":true"), "Hat must be true for " + mat);
            assertTrue(serialized.contains("\"properties\":[{\"name\":\"textures\""), "Textures property must exist for " + mat);
        }
    }

    @Test
    void testShieldSprite() {
        ItemStack shield = stubItem(Material.SHIELD);
        Component sprite = ItemSpriteFactory.create(shield, ItemTextOptions.defaults());
        String serialized = GsonComponentSerializer.gson().serialize(sprite);
        assertEquals("{\"atlas\":\"minecraft:mob_effects\",\"sprite\":\"minecraft:mob_effect/resistance\"}", serialized);
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
