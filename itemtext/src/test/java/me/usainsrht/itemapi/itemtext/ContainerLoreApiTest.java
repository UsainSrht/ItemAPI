package me.usainsrht.itemapi.itemtext;

import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemContainerContents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ContainerLoreApiTest {

    private static final PlainTextComponentSerializer PLAIN = PlainTextComponentSerializer.plainText();
    private ItemTextOptions originalDefaults;

    @BeforeEach
    void setUp() {
        originalDefaults = ItemText.defaultOptions();
    }

    @AfterEach
    void tearDown() {
        ItemText.setDefaultOptions(originalDefaults);
    }

    @Test
    void testContentLoreOptionsDefaults() {
        ContentLoreOptions defaults = ContentLoreOptions.defaults();
        assertTrue(defaults.enabled());
        assertEquals(ContentLoreMode.TOTAL_STACK, defaults.mode());
        assertFalse(defaults.header().isEmpty());
        assertTrue(defaults.footer().isEmpty());
        assertEquals("<sprite:gui:container/slot>", defaults.emptySlot());
        assertEquals("", defaults.separator());
        assertTrue(defaults.emptyMessage().contains("empty"));
        assertEquals(27, defaults.maxLines());
        assertTrue(defaults.contentLine().contains("<content>"));
    }

    @Test
    void testContentLoreOptionsFromConfigKebabCase() throws Exception {
        String yaml = """
                enabled: false
                mode: gui-view
                header:
                  - "<yellow>Container Preview"
                footer:
                  - "<dark_gray>End of preview"
                empty-slot: "[X]"
                separator: " | "
                empty-message: "<red>Box is empty"
                max-lines: 9
                content-line: ">> <content>"
                """;
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new StringReader(yaml));
        ContentLoreOptions options = ContentLoreOptions.fromConfig(config);

        assertFalse(options.enabled());
        assertEquals(ContentLoreMode.GUI_VIEW, options.mode());
        assertEquals(List.of("<yellow>Container Preview"), options.header());
        assertEquals(List.of("<dark_gray>End of preview"), options.footer());
        assertEquals("[X]", options.emptySlot());
        assertEquals(" | ", options.separator());
        assertEquals("<red>Box is empty", options.emptyMessage());
        assertEquals(9, options.maxLines());
        assertEquals(">> <content>", options.contentLine());
    }

    @Test
    void testContentLoreOptionsFromConfigSnakeCaseAndSingleLines() throws Exception {
        String yaml = """
                mode: total_stack
                header: "<green>Header"
                footer: "<blue>Footer"
                empty_slot: "[ ]"
                empty_message: "Nothing here"
                max_lines: 5
                content_line: "<content>"
                """;
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new StringReader(yaml));
        ContentLoreOptions options = ContentLoreOptions.fromConfig(config);

        assertTrue(options.enabled());
        assertEquals(ContentLoreMode.TOTAL_STACK, options.mode());
        assertEquals(List.of("<green>Header"), options.header());
        assertEquals(List.of("<blue>Footer"), options.footer());
        assertEquals("[ ]", options.emptySlot());
        assertEquals("Nothing here", options.emptyMessage());
        assertEquals(5, options.maxLines());
        assertEquals("<content>", options.contentLine());
    }

    @Test
    void testContentLoreOptionsFromConfigNullReturnsDefaults() {
        ContentLoreOptions options = ContentLoreOptions.fromConfig(null);
        assertNotNull(options);
        assertTrue(options.enabled());
        assertEquals(ContentLoreMode.TOTAL_STACK, options.mode());
    }

    @Test
    void testContentLoreOptionsFromMap() {
        Map<String, Object> map = Map.of(
                "enabled", false,
                "mode", "gui_view",
                "header", List.of("H1", "H2"),
                "footer", List.of("F1"),
                "empty_slot", "empty",
                "separator", ",",
                "empty_message", "empty box",
                "max_lines", 12,
                "content_line", "- <content>");
        ContentLoreOptions options = ContentLoreOptions.fromMap(map);

        assertFalse(options.enabled());
        assertEquals(ContentLoreMode.GUI_VIEW, options.mode());
        assertEquals(List.of("H1", "H2"), options.header());
        assertEquals(List.of("F1"), options.footer());
        assertEquals("empty", options.emptySlot());
        assertEquals(",", options.separator());
        assertEquals("empty box", options.emptyMessage());
        assertEquals(12, options.maxLines());
        assertEquals("- <content>", options.contentLine());
    }

    @Test
    void testContentLoreOptionsBuilderMethods() {
        ContentLoreOptions options = ContentLoreOptions.builder()
                .mode("gui-view")
                .header("Line 1", "Line 2")
                .addHeader("Line 3")
                .footer("End 1")
                .addFooter("End 2", "End 3")
                .emptySlot("<slot>")
                .separator(" ")
                .emptyMessage("Empty")
                .maxLines(18)
                .contentLine("  <content>")
                .build();

        assertEquals(ContentLoreMode.GUI_VIEW, options.mode());
        assertEquals(List.of("Line 1", "Line 2", "Line 3"), options.header());
        assertEquals(List.of("End 1", "End 2", "End 3"), options.footer());
        assertEquals("<slot>", options.emptySlot());
        assertEquals(" ", options.separator());
        assertEquals("Empty", options.emptyMessage());
        assertEquals(18, options.maxLines());
        assertEquals("  <content>", options.contentLine());
    }

    @Test
    void testItemTextOptionsContentLorePreservesExistingSettings() {
        // Build initial options with custom maxLines
        ItemTextOptions initial = ItemTextOptions.builder()
                .contentLore(lore -> lore.maxLines(42).header("Custom Header"))
                .build();

        assertEquals(42, initial.contentLore().maxLines());
        assertEquals(List.of("Custom Header"), initial.contentLore().header());

        // Modify mode using unary operator: prior maxLines and header must NOT be wiped
        ItemTextOptions modified = initial.toBuilder()
                .contentLore(lore -> lore.mode(ContentLoreMode.GUI_VIEW))
                .build();

        assertEquals(ContentLoreMode.GUI_VIEW, modified.contentLore().mode());
        assertEquals(42, modified.contentLore().maxLines(), "maxLines must be preserved when modifying mode");
        assertEquals(List.of("Custom Header"), modified.contentLore().header(), "header must be preserved");
    }

    @Test
    void testItemTextOptionsBuilderShortcuts() {
        ItemTextOptions options = ItemTextOptions.builder()
                .containerShowAsBundle(false)
                .contentLoreMode(ContentLoreMode.GUI_VIEW)
                .contentLoreHeader("H1", "H2")
                .contentLoreFooter("F1")
                .contentLoreEmptySlot("[ ]")
                .contentLoreSeparator(";")
                .contentLoreEmptyMessage("Empty!")
                .contentLoreMaxLines(15)
                .contentLoreContentLine("-> <content>")
                .build();

        assertFalse(options.containerShowAsBundle());
        assertEquals(ContentLoreMode.GUI_VIEW, options.contentLore().mode());
        assertEquals(List.of("H1", "H2"), options.contentLore().header());
        assertEquals(List.of("F1"), options.contentLore().footer());
        assertEquals("[ ]", options.contentLore().emptySlot());
        assertEquals(";", options.contentLore().separator());
        assertEquals("Empty!", options.contentLore().emptyMessage());
        assertEquals(15, options.contentLore().maxLines());
        assertEquals("-> <content>", options.contentLore().contentLine());
    }

    @Test
    void testItemTextOptionsFromConfig() throws Exception {
        String yaml = """
                brackets: true
                custom-name: false
                italic: false
                amount-display: normal
                shadow: true
                hover: false
                container-show-as-bundle: false
                container-lore:
                  enabled: true
                  mode: gui_view
                  max-lines: 9
                """;
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new StringReader(yaml));
        ItemTextOptions options = ItemTextOptions.fromConfig(config);

        assertTrue(options.displayBrackets());
        assertFalse(options.displayCustomName());
        assertTrue(options.removeItalic()); // italic: false means removeItalic = true
        assertEquals(AmountDisplay.NORMAL, options.amountDisplay());
        assertTrue(options.shadowEnabled());
        assertFalse(options.hoverEnabled());
        assertFalse(options.containerShowAsBundle());
        assertTrue(options.contentLore().enabled());
        assertEquals(ContentLoreMode.GUI_VIEW, options.contentLore().mode());
        assertEquals(9, options.contentLore().maxLines());
    }

    @Test
    void testItemTextOptionsFromMapWithBooleanContainerLore() {
        Map<String, Object> map = Map.of(
                "container_show_as_bundle", false,
                "container_lore", false);
        ItemTextOptions options = ItemTextOptions.fromMap(map);

        assertFalse(options.containerShowAsBundle());
        assertFalse(options.contentLore().enabled());
    }

    @Test
    void testContainerLoreIsContainerAndHasContents() {
        ItemStack normalItem = stubItem(Material.DIAMOND_SWORD);
        assertFalse(ContainerLore.isContainer(normalItem));
        assertFalse(ContainerLore.isContainer(null));
        assertFalse(ContainerLore.hasContents(normalItem));
        assertFalse(ContainerLore.hasContents(null));

        ItemStack emptyContainer = stubContainer(Material.CHEST, List.of());
        assertTrue(ContainerLore.isContainer(emptyContainer));
        assertFalse(ContainerLore.hasContents(emptyContainer));

        ItemStack airContainer = stubContainer(Material.CHEST, List.of(stubItem(Material.AIR)));
        assertTrue(ContainerLore.isContainer(airContainer));
        assertFalse(ContainerLore.hasContents(airContainer));

        ItemStack filledContainer = stubContainer(Material.CHEST, List.of(stubItem(Material.DIAMOND)));
        assertTrue(ContainerLore.isContainer(filledContainer));
        assertTrue(ContainerLore.hasContents(filledContainer));
    }

    @Test
    void testToBundle() {
        ItemStack sword = stubItem(Material.DIAMOND_SWORD);
        assertSame(sword, ContainerLore.toBundle(sword));
        assertSame(sword, ItemText.toBundle(sword));
        assertNull(ContainerLore.toBundle(null));
        assertNull(ItemText.toBundle(null));

        ItemStack chest = stubContainer(Material.CHEST, List.of(stubItem(Material.DIAMOND)));
        ItemStack bundle = ContainerLore.toBundle(chest);
        assertNotNull(bundle);
        assertEquals(Material.BUNDLE, bundle.getType());
    }

    @Test
    void testContainerLoreRenderOverloads() {
        ItemStack emptyChest = stubContainer(Material.CHEST, List.of());

        List<Component> defaultLore = ContainerLore.render(emptyChest);
        assertFalse(defaultLore.isEmpty(), "Render with defaults should produce lore lines");

        ContentLoreOptions customOptions = ContentLoreOptions.builder()
                .emptyMessage("Custom Empty Msg")
                .header(List.of())
                .footer(List.of())
                .build();

        List<Component> customLore = ContainerLore.render(emptyChest, customOptions);
        assertEquals(1, customLore.size());
        assertEquals("Custom Empty Msg", PLAIN.serialize(customLore.getFirst()));

        ItemTextOptions parentOpts = ItemTextOptions.builder().contentLore(customOptions).build();
        List<Component> parentLore = ContainerLore.render(emptyChest, parentOpts);
        assertEquals(1, parentLore.size());
        assertEquals("Custom Empty Msg", PLAIN.serialize(parentLore.getFirst()));

        // Also test ItemText wrapper
        List<Component> itemTextLore = ItemText.containerLore(emptyChest, customOptions);
        assertEquals(1, itemTextLore.size());
        assertEquals("Custom Empty Msg", PLAIN.serialize(itemTextLore.getFirst()));
    }

    @Test
    void testContainerLoreApplyOverloads() {
        ContentLoreOptions customOptions = ContentLoreOptions.builder()
                .emptyMessage("Empty Box")
                .header(List.of())
                .footer(List.of())
                .build();

        ItemStack chest = stubContainer(Material.CHEST, List.of());

        // Apply replace
        ItemStack withLore = ContainerLore.apply(chest, customOptions);
        assertNotSame(chest, withLore);
        assertNotNull(withLore.lore());
        assertEquals(1, withLore.lore().size());
        assertEquals("Empty Box", PLAIN.serialize(withLore.lore().getFirst()));

        // Apply append
        ItemStack withExistingLore = withLore.clone();
        ItemStack appended = ContainerLore.apply(withExistingLore, customOptions, true);
        assertNotNull(appended.lore());
        assertEquals(2, appended.lore().size());

        // ItemText wrapper apply
        ItemStack itemTextApplied = ItemText.applyContainerLore(chest, customOptions);
        assertNotNull(itemTextApplied.lore());
        assertEquals(1, itemTextApplied.lore().size());
    }

    @Test
    void testItemTextGlobalSettingsMutators() {
        ItemText.setContainerShowAsBundle(false);
        assertFalse(ItemText.defaultOptions().containerShowAsBundle());

        ItemText.setContentLore(lore -> lore.mode(ContentLoreMode.GUI_VIEW));
        assertEquals(ContentLoreMode.GUI_VIEW, ItemText.defaultOptions().contentLore().mode());

        ContentLoreOptions direct = ContentLoreOptions.builder().maxLines(10).build();
        ItemText.setContentLore(direct);
        assertEquals(10, ItemText.defaultOptions().contentLore().maxLines());
    }

    @Test
    void testResolveContentRemoveItalicFalseByDefault() {
        ContentLoreOptions options = ContentLoreOptions.defaults();
        ItemTextOptions parent = ItemTextOptions.builder().removeItalic(true).build();
        assertTrue(parent.removeItalic());

        ItemTextOptions resolved = options.resolveContent(parent);
        assertFalse(resolved.removeItalic(), "container lore total stack must resolve removeItalic to false by default");
        assertFalse(resolved.hoverEnabled());
        assertFalse(resolved.contentLore().enabled());
    }

    @Test
    void testResolveContentWithExplicitContent() {
        ContentLoreOptions options = ContentLoreOptions.builder()
                .content(c -> c.removeItalic(true).hoverEnabled(true))
                .build();
        assertNotNull(options.content());
        assertTrue(options.content().removeItalic());

        ItemTextOptions parent = ItemTextOptions.builder().removeItalic(false).build();
        ItemTextOptions resolved = options.resolveContent(parent);
        assertTrue(resolved.removeItalic());
        assertTrue(resolved.hoverEnabled());
    }

    @Test
    void testContainerLoreTotalStackRemoveItalicFalseByDefault() {
        ItemStack diamond = stubItem(Material.DIAMOND);
        ItemStack chest = stubContainer(Material.CHEST, List.of(diamond));

        ContentLoreOptions options = ContentLoreOptions.builder()
                .header(List.of())
                .footer(List.of())
                .contentLine("<content>")
                .build();

        ItemTextOptions parent = ItemTextOptions.builder()
                .pattern("<item_displayname>")
                .removeItalic(true)
                .build();

        List<Component> lines = ContainerLore.render(chest, options, parent);
        assertFalse(lines.isEmpty());
        Component line = lines.getFirst();
        assertFalse(hasItalicFalse(line), "Inner item should not have TextDecoration.ITALIC = FALSE by default");
    }

    @Test
    void testContainerLoreTotalStackRemoveItalicTrueWhenConfigured() {
        ItemStack diamond = stubItem(Material.DIAMOND);
        ItemStack chest = stubContainer(Material.CHEST, List.of(diamond));

        ContentLoreOptions options = ContentLoreOptions.builder()
                .header(List.of())
                .footer(List.of())
                .contentLine("<content>")
                .content(c -> c.removeItalic(true))
                .build();

        ItemTextOptions parent = ItemTextOptions.builder()
                .pattern("<item_displayname>")
                .build();

        List<Component> lines = ContainerLore.render(chest, options, parent);
        assertFalse(lines.isEmpty());
        Component line = lines.getFirst();
        assertTrue(hasItalicFalse(line), "Inner item should have TextDecoration.ITALIC = FALSE when configured");
    }

    private static boolean hasItalicFalse(Component component) {
        if (component.decoration(TextDecoration.ITALIC) == TextDecoration.State.FALSE) {
            return true;
        }
        for (Component child : component.children()) {
            if (hasItalicFalse(child)) {
                return true;
            }
        }
        return false;
    }

    @Test
    void testContentLoreOptionsContentFromConfig() throws Exception {
        String yaml = """
                content:
                  remove-italic: false
                  brackets: true
                """;
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new StringReader(yaml));
        ContentLoreOptions options = ContentLoreOptions.fromConfig(config);
        assertNotNull(options.content());
        assertFalse(options.content().removeItalic());
        assertTrue(options.content().displayBrackets());
    }

    @Test
    void testContentLoreOptionsContentFromMap() {
        Map<String, Object> map = Map.of(
                "content", Map.of(
                        "remove_italic", false,
                        "brackets", true
                )
        );
        ContentLoreOptions options = ContentLoreOptions.fromMap(map);
        assertNotNull(options.content());
        assertFalse(options.content().removeItalic());
        assertTrue(options.content().displayBrackets());
    }

    @Test
    void testContentLorePartialOverrideFromConfig() throws Exception {
        String yaml = """
                content:
                  remove-italic: false
                  brackets: true
                """;
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new StringReader(yaml));
        ContentLoreOptions options = ContentLoreOptions.fromConfig(config);

        ItemTextOptions parent = ItemTextOptions.builder()
                .displayBrackets(false)
                .pattern("<item_sprite> <item_displayname>")
                .shadowEnabled(true)
                .displayCustomNameIfHasColor(true)
                .removeItalic(true)
                .build();

        ItemTextOptions resolved = options.resolveContent(parent);

        // Explicitly overridden in content:
        assertFalse(resolved.removeItalic());
        assertTrue(resolved.displayBrackets());

        // Fallbacks inherited from parent:
        assertEquals("<item_sprite> <item_displayname>", resolved.pattern());
        assertTrue(resolved.shadowEnabled());
        assertTrue(resolved.displayCustomNameIfHasColor());
        assertFalse(resolved.hoverEnabled());
        assertFalse(resolved.contentLore().enabled());
    }

    @Test
    void testContentLorePartialOverrideFromCodeConfigurator() {
        ContentLoreOptions options = ContentLoreOptions.builder()
                .content(c -> c.removeItalic(false).displayBrackets(true))
                .build();

        ItemTextOptions parent = ItemTextOptions.builder()
                .displayBrackets(false)
                .pattern("<item_sprite> <item_displayname>")
                .shadowEnabled(true)
                .displayCustomNameIfHasColor(true)
                .removeItalic(true)
                .build();

        ItemTextOptions resolved = options.resolveContent(parent);

        // Explicitly overridden via configurator:
        assertFalse(resolved.removeItalic());
        assertTrue(resolved.displayBrackets());

        // Fallbacks inherited from parent:
        assertEquals("<item_sprite> <item_displayname>", resolved.pattern());
        assertTrue(resolved.shadowEnabled());
        assertTrue(resolved.displayCustomNameIfHasColor());
        assertFalse(resolved.hoverEnabled());
        assertFalse(resolved.contentLore().enabled());
    }

    @Test
    void testContentLorePartialOverrideFromMap() {
        Map<String, Object> map = Map.of(
                "content", Map.of(
                        "remove_italic", false,
                        "brackets", true
                )
        );
        ContentLoreOptions options = ContentLoreOptions.fromMap(map);

        ItemTextOptions parent = ItemTextOptions.builder()
                .displayBrackets(false)
                .pattern("<item_sprite> <item_displayname>")
                .shadowEnabled(true)
                .removeItalic(true)
                .build();

        ItemTextOptions resolved = options.resolveContent(parent);

        // Explicitly overridden in map:
        assertFalse(resolved.removeItalic());
        assertTrue(resolved.displayBrackets());

        // Fallbacks inherited from parent:
        assertEquals("<item_sprite> <item_displayname>", resolved.pattern());
        assertTrue(resolved.shadowEnabled());
    }

    private static ItemStack stubItem(Material material) {
        return new ItemStack() {
            private List<Component> lore = new ArrayList<>();

            @Override
            public Material getType() {
                return material;
            }

            @Override
            public boolean isSimilar(@Nullable ItemStack stack) {
                return stack != null && stack.getType() == material;
            }

            @Override
            public int hashCode() {
                return material.hashCode();
            }

            @Override
            public boolean equals(Object obj) {
                return obj instanceof ItemStack other && other.getType() == material;
            }

            @Override
            public int getAmount() {
                return 1;
            }

            @Override
            public @Nullable ItemMeta getItemMeta() {
                return null;
            }

            @Override
            public <T> @Nullable T getData(DataComponentType.Valued<T> type) {
                return null;
            }

            @Override
            public boolean hasItemMeta() {
                return false;
            }

            @Override
            public @Nullable List<Component> lore() {
                return lore;
            }

            @Override
            public void lore(@Nullable List<? extends Component> lore) {
                this.lore = lore == null ? new ArrayList<>() : new ArrayList<>(lore);
            }

            @Override
            public void unsetData(DataComponentType type) {
            }

            @Override
            public <T> void setData(DataComponentType.Valued<T> type, T value) {
            }

            @Override
            public boolean editMeta(java.util.function.Consumer<? super ItemMeta> consumer) {
                return true;
            }

            @Override
            public ItemStack clone() {
                ItemStack parent = this;
                ItemStack copy = stubItem(material);
                if (parent.lore() != null) {
                    copy.lore(new ArrayList<>(parent.lore()));
                }
                return copy;
            }
        };
    }

    private static ItemStack stubContainer(Material material, List<ItemStack> contents) {
        ItemContainerContents containerContents = new ItemContainerContents() {
            @Override
            public List<ItemStack> contents() {
                return contents;
            }
        };

        return new ItemStack() {
            private List<Component> lore = new ArrayList<>();

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
                if (type == DataComponentTypes.CONTAINER) {
                    return (T) containerContents;
                }
                return null;
            }

            @Override
            public boolean hasItemMeta() {
                return false;
            }

            @Override
            public @Nullable ItemMeta getItemMeta() {
                return null;
            }

            @Override
            public @Nullable List<Component> lore() {
                return lore;
            }

            @Override
            public void lore(@Nullable List<? extends Component> lore) {
                this.lore = lore == null ? new ArrayList<>() : new ArrayList<>(lore);
            }

            @Override
            public ItemStack withType(Material newType) {
                return stubItem(newType);
            }

            @Override
            public void unsetData(DataComponentType type) {
            }

            @Override
            public <T> void setData(DataComponentType.Valued<T> type, T value) {
            }

            @Override
            public ItemStack clone() {
                ItemStack parent = this;
                ItemStack copy = stubContainer(material, contents);
                if (parent.lore() != null) {
                    copy.lore(new ArrayList<>(parent.lore()));
                }
                return copy;
            }
        };
    }
}
