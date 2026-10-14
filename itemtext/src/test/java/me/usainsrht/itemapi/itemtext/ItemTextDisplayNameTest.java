package me.usainsrht.itemapi.itemtext;

import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemTextDisplayNameTest {

    private static final MiniMessage MM = MiniMessage.miniMessage();

    @Test
    void testItemTextOptionsDefaults() {
        ItemTextOptions defaults = ItemTextOptions.defaults();
        assertTrue(defaults.displayCustomName(), "displayCustomName should default to true");
        assertFalse(defaults.displayCustomNameIfHasColor(), "displayCustomNameIfHasColor should default to false");
    }

    @Test
    void testItemTextOptionsBuilderAndToBuilder() {
        ItemTextOptions options = ItemTextOptions.builder()
                .displayCustomName(false)
                .displayCustomNameIfHasColor(true)
                .build();

        assertFalse(options.displayCustomName());
        assertTrue(options.displayCustomNameIfHasColor());

        ItemTextOptions copy = options.toBuilder().build();
        assertFalse(copy.displayCustomName());
        assertTrue(copy.displayCustomNameIfHasColor());

        ItemTextOptions toggled = options.toBuilder()
                .displayCustomNameIfHasColor(false)
                .build();
        assertFalse(toggled.displayCustomNameIfHasColor());
    }

    @Test
    void testDisplayCustomNameTrueAlwaysDisplaysCustomName() {
        // When displayCustomName = true, custom name is displayed even if uncolored
        Component uncolored = Component.text("Plain Sword");
        ItemStack item = stubItem(Material.DIAMOND_SWORD, uncolored);

        ItemTextOptions options = ItemTextOptions.builder()
                .pattern("<item_displayname>")
                .hoverEnabled(false)
                .displayCustomName(true)
                .displayCustomNameIfHasColor(false)
                .removeItalic(false)
                .build();

        Component result = ItemText.format(item, options);
        assertEquals(uncolored, result);
    }

    @Test
    void testDisplayCustomNameFalseAndColorOptionFalseDefaultsToTranslatable() {
        // When displayCustomName = false and displayCustomNameIfHasColor = false,
        // it falls back to translatable even if the custom name has color
        Component colored = Component.text("Fire Sword", NamedTextColor.RED);
        ItemStack item = stubItem(Material.DIAMOND_SWORD, colored);

        ItemTextOptions options = ItemTextOptions.builder()
                .pattern("<item_displayname>")
                .hoverEnabled(false)
                .displayCustomName(false)
                .displayCustomNameIfHasColor(false)
                .removeItalic(false)
                .build();

        Component result = ItemText.format(item, options);
        TranslatableComponent translatable = extractTranslatable(result);
        assertEquals(Material.DIAMOND_SWORD.translationKey(), translatable.key());
    }

    @Test
    void testDisplayCustomNameIfHasColorWithDirectColor() {
        // displayCustomName = false, displayCustomNameIfHasColor = true
        // Custom name has direct color -> should display custom name
        Component colored = Component.text("Fire Sword", NamedTextColor.RED);
        ItemStack item = stubItem(Material.DIAMOND_SWORD, colored);

        ItemTextOptions options = ItemTextOptions.builder()
                .pattern("<item_displayname>")
                .hoverEnabled(false)
                .displayCustomName(false)
                .displayCustomNameIfHasColor(true)
                .removeItalic(false)
                .build();

        Component result = ItemText.format(item, options);
        assertEquals(colored, result);
    }

    @Test
    void testDisplayCustomNameIfHasColorWithHexColor() {
        Component hexColored = Component.text("Excalibur", TextColor.color(0xFFAA00));
        ItemStack item = stubItem(Material.DIAMOND_SWORD, hexColored);

        ItemTextOptions options = ItemTextOptions.builder()
                .pattern("<item_displayname>")
                .hoverEnabled(false)
                .displayCustomName(false)
                .displayCustomNameIfHasColor(true)
                .removeItalic(false)
                .build();

        Component result = ItemText.format(item, options);
        assertEquals(hexColored, result);
    }

    @Test
    void testDisplayCustomNameIfHasColorWithNestedChildColor() {
        // MiniMessage <gray>[<gold>Legendary</gold>]</gray> produces children with color
        Component componentWithColoredChildren = MM.deserialize("<gray>[<gold>Legendary</gold>]</gray>");
        ItemStack item = stubItem(Material.DIAMOND_SWORD, componentWithColoredChildren);

        ItemTextOptions options = ItemTextOptions.builder()
                .pattern("<item_displayname>")
                .hoverEnabled(false)
                .displayCustomName(false)
                .displayCustomNameIfHasColor(true)
                .removeItalic(false)
                .build();

        Component result = ItemText.format(item, options);
        assertEquals(componentWithColoredChildren, result);
    }

    @Test
    void testDisplayCustomNameIfHasColorWithoutColorDefaultsToTranslatable() {
        // displayCustomName = false, displayCustomNameIfHasColor = true
        // Custom name has NO color -> defaults to translatable component
        Component uncolored = Component.text("Plain Anvil Renamed Sword");
        ItemStack item = stubItem(Material.DIAMOND_SWORD, uncolored);

        ItemTextOptions options = ItemTextOptions.builder()
                .pattern("<item_displayname>")
                .hoverEnabled(false)
                .displayCustomName(false)
                .displayCustomNameIfHasColor(true)
                .removeItalic(false)
                .build();

        Component result = ItemText.format(item, options);
        TranslatableComponent translatable = extractTranslatable(result);
        assertEquals(Material.DIAMOND_SWORD.translationKey(), translatable.key());
    }

    @Test
    void testDisplayCustomNameIfHasColorWithNoCustomNameDefaultsToTranslatable() {
        // Item has no custom name at all
        ItemStack item = stubItem(Material.DIAMOND_SWORD, null);

        ItemTextOptions options = ItemTextOptions.builder()
                .pattern("<item_displayname>")
                .hoverEnabled(false)
                .displayCustomName(false)
                .displayCustomNameIfHasColor(true)
                .removeItalic(false)
                .build();

        Component result = ItemText.format(item, options);
        TranslatableComponent translatable = extractTranslatable(result);
        assertEquals(Material.DIAMOND_SWORD.translationKey(), translatable.key());
    }

    @Test
    void testDisplayCustomNameTrueBypassesColorCheck() {
        // When displayCustomName = true, displayCustomNameIfHasColor is ignored
        Component uncolored = Component.text("Plain Sword");
        ItemStack item = stubItem(Material.DIAMOND_SWORD, uncolored);

        ItemTextOptions options = ItemTextOptions.builder()
                .pattern("<item_displayname>")
                .hoverEnabled(false)
                .displayCustomName(true)
                .displayCustomNameIfHasColor(true)
                .removeItalic(false)
                .build();

        Component result = ItemText.format(item, options);
        assertEquals(uncolored, result);
    }

    @Test
    void testStripMapDecorationsOptionDefaults() {
        ItemTextOptions defaults = ItemTextOptions.defaults();
        assertTrue(defaults.stripMapDecorations(), "stripMapDecorations should default to true");
    }

    @Test
    void testStripMapDecorationsOptionBuilder() {
        ItemTextOptions options = ItemTextOptions.builder()
                .stripMapDecorations(false)
                .build();
        assertFalse(options.stripMapDecorations());
        assertTrue(options.toBuilder().stripMapDecorations(true).build().stripMapDecorations());
    }

    @Test
    void testSanitizeHoverItemStripsMapDecorations() {
        java.util.concurrent.atomic.AtomicBoolean hasDecorations = new java.util.concurrent.atomic.AtomicBoolean(true);
        ItemStack item = new ItemStack() {
            @Override
            public Material getType() {
                return Material.FILLED_MAP;
            }

            @Override
            public int getAmount() {
                return 1;
            }

            @Override
            public boolean hasData(DataComponentType type) {
                if (type == DataComponentTypes.MAP_DECORATIONS) {
                    return hasDecorations.get();
                }
                return false;
            }

            @Override
            public void unsetData(DataComponentType type) {
                if (type == DataComponentTypes.MAP_DECORATIONS) {
                    hasDecorations.set(false);
                }
            }

            @Override
            public ItemStack clone() {
                return this;
            }
        };

        assertTrue(item.hasData(DataComponentTypes.MAP_DECORATIONS));
        ItemStack sanitized = ItemText.sanitizeHoverItem(item, item);
        assertNotNull(sanitized);
        assertFalse(hasDecorations.get(), "MAP_DECORATIONS should be unset during sanitization");
    }

    private static ItemStack stubItem(Material material, @Nullable Component customName) {
        return new ItemStack() {
            @Override
            public Material getType() {
                return material;
            }

            @Override
            public int getAmount() {
                return 1;
            }

            @Override
            @SuppressWarnings("unchecked")
            public <T> @Nullable T getData(DataComponentType.Valued<T> type) {
                if (type == DataComponentTypes.CUSTOM_NAME) {
                    return (T) customName;
                }
                return null;
            }

            @Override
            public boolean hasItemMeta() {
                return false;
            }
        };
    }

    private static TranslatableComponent extractTranslatable(Component component) {
        if (component instanceof TranslatableComponent tc) {
            return tc;
        }
        for (Component child : component.children()) {
            if (child instanceof TranslatableComponent tc) {
                return tc;
            }
            TranslatableComponent nested = extractTranslatable(child);
            if (nested != null) {
                return nested;
            }
        }
        fail("No TranslatableComponent found in " + component);
        return null;
    }
}
