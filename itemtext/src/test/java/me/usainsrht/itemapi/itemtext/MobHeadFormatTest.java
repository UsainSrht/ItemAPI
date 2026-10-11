package me.usainsrht.itemapi.itemtext;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.ShadowColor;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MobHeadFormatTest {

    private static final GsonComponentSerializer GSON = GsonComponentSerializer.gson();

    private Entity stubEntity(EntityType type, Component customName) {
        UUID uuid = UUID.nameUUIDFromBytes(("stub:" + type.name()).getBytes(StandardCharsets.UTF_8));
        return (Entity) Proxy.newProxyInstance(
                Entity.class.getClassLoader(),
                new Class<?>[]{Entity.class},
                (proxy, method, args) -> {
                    switch (method.getName()) {
                        case "getType" -> { return type; }
                        case "customName" -> { return customName; }
                        case "getUniqueId" -> { return uuid; }
                        case "asHoverEvent" -> {
                            return HoverEvent.showEntity(type.key(), uuid, customName);
                        }
                        default -> { return null; }
                    }
                }
        );
    }

    private Player stubPlayer(String name, UUID uuid, Component customName) {
        return (Player) Proxy.newProxyInstance(
                Player.class.getClassLoader(),
                new Class<?>[]{Player.class},
                (proxy, method, args) -> {
                    switch (method.getName()) {
                        case "getType" -> { return EntityType.PLAYER; }
                        case "getName" -> { return name; }
                        case "getUniqueId" -> { return uuid; }
                        case "customName" -> { return customName; }
                        case "asHoverEvent" -> {
                            return HoverEvent.showEntity(EntityType.PLAYER.key(), uuid, customName != null ? customName : Component.text(name));
                        }
                        default -> { return null; }
                    }
                }
        );
    }

    @Test
    void testFormatEntityDefaultAmountAndTranslatableFallback() {
        Entity zombie = stubEntity(EntityType.ZOMBIE, null);
        Component formatted = ItemText.format(zombie);

        String json = GSON.serialize(formatted);
        // Sprite with hat
        assertTrue(json.contains("\"hat\":true"));
        assertTrue(json.contains(MobHeadRegistry.find("zombie").base64Texture()));

        // Default amount is 1 and showAmountWhenOne is false -> no subscript amount
        String plain = PlainTextComponentSerializer.plainText().serialize(formatted);
        assertFalse(plain.contains("₁"));
        assertFalse(plain.contains("1"));

        // Name is translatable
        assertTrue(json.contains("entity.minecraft.zombie"));
    }

    @Test
    void testFormatEntityCustomName() {
        Component customName = Component.text("Super Zombie", NamedTextColor.GOLD);
        Entity zombie = stubEntity(EntityType.ZOMBIE, customName);

        Component formatted = ItemText.format(zombie);
        String json = GSON.serialize(formatted);

        assertTrue(json.contains("Super Zombie"));
        assertTrue(json.contains("gold"));
    }

    @Test
    void testFormatEntityCustomNameIfHasColor() {
        Component colored = Component.text("Colored Creeper", NamedTextColor.GREEN);
        Entity coloredCreeper = stubEntity(EntityType.CREEPER, colored);

        Component uncolored = Component.text("Plain Creeper");
        Entity plainCreeper = stubEntity(EntityType.CREEPER, uncolored);

        ItemTextOptions colorOnly = ItemTextOptions.builder()
                .hoverEnabled(false)
                .displayCustomName(false)
                .displayCustomNameIfHasColor(true)
                .build();

        // Colored creeper displays custom name
        Component coloredResult = ItemText.format(coloredCreeper, colorOnly);
        assertTrue(GSON.serialize(coloredResult).contains("Colored Creeper"));

        // Uncolored creeper falls back to translation key
        Component plainResult = ItemText.format(plainCreeper, colorOnly);
        assertFalse(GSON.serialize(plainResult).contains("Plain Creeper"));
        assertTrue(GSON.serialize(plainResult).contains("entity.minecraft.creeper"));
    }

    @Test
    void testFormatEntityWithAmount() {
        Entity skeleton = stubEntity(EntityType.SKELETON, null);

        // Subscript amount (default)
        Component subFormatted = ItemText.format(skeleton, 5);
        String subPlain = PlainTextComponentSerializer.plainText().serialize(subFormatted);
        assertTrue(subPlain.contains("₅"), "Amount 5 should render as subscript ₅");

        // Superscript amount
        Component superFormatted = ItemText.format(skeleton, 12, b -> b.amountDisplay(AmountDisplay.SUPERSCRIPT));
        String superPlain = PlainTextComponentSerializer.plainText().serialize(superFormatted);
        assertTrue(superPlain.contains("¹²"), "Amount 12 should render as superscript ¹²");

        // Normal amount
        Component normalFormatted = ItemText.format(skeleton, 3, b -> b.amountDisplay(AmountDisplay.NORMAL));
        String normalPlain = PlainTextComponentSerializer.plainText().serialize(normalFormatted);
        assertTrue(normalPlain.contains("3"), "Amount 3 should render as normal 3");
    }

    @Test
    void testFormatEntityType() {
        Component formatted = ItemText.format(EntityType.COW, 2);
        String json = GSON.serialize(formatted);

        assertTrue(json.contains(MobHeadRegistry.find("cow").base64Texture()));
        assertTrue(json.contains("entity.minecraft.cow"));

        String plain = PlainTextComponentSerializer.plainText().serialize(formatted);
        assertTrue(plain.contains("₂"));
    }

    @Test
    void testFormatStringEntityKey() {
        Component formatted = ItemText.format("warden", 1);
        String json = GSON.serialize(formatted);

        assertTrue(json.contains(MobHeadRegistry.find("warden").base64Texture()));
        assertTrue(json.contains("entity.minecraft.warden"));
    }

    @Test
    void testBracketsAndCustomPattern() {
        ItemTextOptions options = ItemTextOptions.builder()
                .pattern("<mob_sprite><mob_amount> <mob_name>")
                .displayBrackets(true)
                .amountDisplay(AmountDisplay.NORMAL)
                .build();

        Component formatted = ItemText.format(EntityType.PIG, 4, options);
        String plain = PlainTextComponentSerializer.plainText().serialize(formatted);

        assertTrue(plain.startsWith("["), "Should start with bracket [");
        assertTrue(plain.endsWith("]"), "Should end with bracket ]");
        assertTrue(plain.contains("4"));
    }

    @Test
    void testShadowAndSpriteColor() {
        ItemTextOptions options = ItemTextOptions.builder()
                .shadowEnabled(true)
                .shadowColor(ShadowColor.shadowColor(0x80FF0000))
                .spriteColor(NamedTextColor.YELLOW)
                .build();

        Component formatted = ItemText.format(EntityType.BLAZE, options);
        String json = GSON.serialize(formatted);

        assertTrue(json.contains("\"shadow_color\":"));
        assertTrue(json.contains("\"color\":\"yellow\""));
    }

    @Test
    void testMobHeadHatOption() {
        ItemTextOptions noHat = ItemTextOptions.builder()
                .mobHeadHat(false)
                .build();

        Component formatted = ItemText.format(EntityType.ZOMBIE, noHat);
        String json = GSON.serialize(formatted);

        assertTrue(json.contains("\"hat\":false"), "Outer hat layer must be false when configured");
    }

    @Test
    void testHoverEventAttached() {
        Entity spider = stubEntity(EntityType.SPIDER, Component.text("Spidey"));
        Component formatted = ItemText.format(spider, b -> b.hoverEnabled(true));

        assertNotNull(formatted.hoverEvent());
        assertEquals(HoverEvent.Action.SHOW_ENTITY, formatted.hoverEvent().action());

        Component noHover = ItemText.format(spider, b -> b.hoverEnabled(false));
        assertNull(noHover.hoverEvent());
    }

    @Test
    void testPlayerEntityFormatting() {
        UUID uuid = UUID.randomUUID();
        Player player = stubPlayer("UsainSRHT", uuid, Component.text("CustomUsain"));

        Component formatted = ItemText.format(player, 1);
        String json = GSON.serialize(formatted);

        assertTrue(json.contains("CustomUsain"), "Formatted player entity must contain custom name");
        assertTrue(json.contains("\"hat\":true"), "Player head must have hat: true");
    }

    @Test
    void testItemStackExplicitAmountOverload() {
        ItemStack item = new ItemStack() {
            @Override
            public Material getType() {
                return Material.DIAMOND;
            }

            @Override
            public int getAmount() {
                return 1;
            }

            @Override
            @SuppressWarnings("unchecked")
            public <T> T getData(io.papermc.paper.datacomponent.DataComponentType.Valued<T> type) {
                return null;
            }

            @Override
            public boolean hasItemMeta() {
                return false;
            }
        };

        Component formatted = ItemText.format(item, 64);
        String plain = PlainTextComponentSerializer.plainText().serialize(formatted);
        assertTrue(plain.contains("₆₄"), "Explicit amount 64 should override item amount 1");
    }
}
