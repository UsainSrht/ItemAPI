package me.usainsrht.itemapi.yamlitem;

import me.usainsrht.itemapi.yamlitem.internal.TextUtil;
import me.usainsrht.itemapi.yamlitem.internal.YamlNode;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class YamlItemPlaceholderTest {

    @Test
    void testYamlItemOptionsDefaults() {
        YamlItemOptions empty = YamlItemOptions.empty();
        assertNotNull(empty.tagResolver());
        assertNull(empty.stringPreprocessor());
        assertNull(empty.miniMessage());

        YamlItemOptions builtEmpty = YamlItemOptions.builder().build();
        assertSame(empty, builtEmpty);
    }

    @Test
    void testYamlItemOptionsCustom() {
        TagResolver resolver = TagResolver.resolver("test", Tag.inserting(Component.text("val")));
        YamlItemOptions options = YamlItemOptions.builder()
                .tagResolver(resolver)
                .stringPreprocessor(s -> s.replace("%var%", "replaced"))
                .miniMessage(MiniMessage.miniMessage())
                .build();

        assertNotNull(options.tagResolver());
        assertNotNull(options.stringPreprocessor());
        assertEquals("replaced", options.stringPreprocessor().apply("%var%"));
        assertNotNull(options.miniMessage());
    }

    @Test
    void testTextUtilWithTagResolver() {
        TagResolver resolver = TagResolver.resolver("player", Tag.inserting(Component.text("Steve", NamedTextColor.YELLOW)));
        Component component = TextUtil.deserialize(null, "Hello <player>!", resolver);

        String plain = PlainTextComponentSerializer.plainText().serialize(component);
        assertEquals("Hello Steve!", plain);
        assertEquals(TextDecoration.State.FALSE, component.decoration(TextDecoration.ITALIC));
    }

    @Test
    void testTextUtilWithoutTagResolver() {
        Component component = TextUtil.deserialize(null, "<red>Sword</red>", null);
        String plain = PlainTextComponentSerializer.plainText().serialize(component);
        assertEquals("Sword", plain);
        assertEquals(NamedTextColor.RED, component.color());
    }

    @Test
    void testYamlNodePreprocessing() {
        Map<String, Object> data = Map.of(
                "material", "%mat%",
                "amount", 5,
                "lore", List.of("Line for %player%"),
                "nested", Map.of("key", "%player%_key")
        );

        YamlNode node = YamlNode.of(data);

        // Without preprocessor, identity is retained
        assertSame(node, node.preprocessed(null));

        // With preprocessor
        YamlNode preprocessed = node.preprocessed(s -> s
                .replace("%mat%", "DIAMOND_SWORD")
                .replace("%player%", "Alex"));

        assertEquals("DIAMOND_SWORD", preprocessed.raw("material"));
        assertEquals(5, preprocessed.raw("amount"));

        List<?> lore = preprocessed.list("lore");
        assertEquals(1, lore.size());
        assertEquals("Line for Alex", lore.get(0));

        YamlNode nested = preprocessed.child("nested");
        assertNotNull(nested);
        assertEquals("Alex_key", nested.raw("key"));
    }

    @Test
    void testYamlItemParserParseTextWithBothResolvers() {
        TagResolver tagResolver = Placeholder.parsed("crate_name", "<gold>Legendary</gold>");
        YamlItemOptions options = YamlItemOptions.builder()
                .tagResolver(tagResolver)
                .stringPreprocessor(s -> s.replace("%player%", "Usain"))
                .build();

        YamlItemParser parser = new YamlItemParser().withOptions(options);

        Component result = parser.parseText("%player%'s <crate_name> Crate", "name");
        String plain = PlainTextComponentSerializer.plainText().serialize(result);
        assertEquals("Usain's Legendary Crate", plain);
    }
}
