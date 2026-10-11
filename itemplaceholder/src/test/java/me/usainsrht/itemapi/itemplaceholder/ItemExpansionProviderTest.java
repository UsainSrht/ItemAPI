package me.usainsrht.itemapi.itemplaceholder;

import io.github.miniplaceholders.api.Expansion;
import io.github.miniplaceholders.api.MiniPlaceholders;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemExpansionProviderTest {

    @Test
    void testExpansionProvidesMobHeadPlaceholders() {
        ItemExpansionProvider provider = new ItemExpansionProvider();
        Expansion expansion = provider.provideExpansion();

        assertNotNull(expansion);
        assertEquals("item", expansion.name());
        assertNotNull(expansion.globalPlaceholderByName("mob_head"));

        // Global placeholders TagResolver from the expansion
        Component component = MiniMessage.miniMessage().deserialize(
                "<item_mob_head:creeper>",
                expansion.globalPlaceholders());

        String json = GsonComponentSerializer.gson().serialize(component);
        assertTrue(json.contains("\"hat\":true"), "Item mob head placeholder must produce player head with hat");
        assertTrue(json.contains("\"properties\":[{\"name\":\"textures\""), "Must contain textures property");

        // Test formatted mob placeholder <item_mob:...>
        assertNotNull(expansion.globalPlaceholderByName("mob"));
        Component mobFormatted = MiniMessage.miniMessage().deserialize(
                "<item_mob:zombie:5:brackets>",
                expansion.globalPlaceholders());
        String mobFormattedJson = GsonComponentSerializer.gson().serialize(mobFormatted);
        assertTrue(mobFormattedJson.contains("\"hat\":true"));
        assertTrue(mobFormattedJson.contains("entity.minecraft.zombie"));
        assertTrue(mobFormattedJson.contains("₅"));

        // Test mob expansion if registered
        Expansion mobExp = MiniPlaceholders.expansionByName("mob");
        if (mobExp != null) {
            Component mobComponent = MiniMessage.miniMessage().deserialize(
                    "<mob_head:creeper>",
                    mobExp.globalPlaceholders());
            String mobJson = GsonComponentSerializer.gson().serialize(mobComponent);
            assertTrue(mobJson.contains("\"hat\":true"));

            Component mobFormatComp = MiniMessage.miniMessage().deserialize(
                    "<mob_format:warden:2:no_hat>",
                    mobExp.globalPlaceholders());
            String mobFormatJson = GsonComponentSerializer.gson().serialize(mobFormatComp);
            assertTrue(mobFormatJson.contains("\"hat\":false"));
            assertTrue(mobFormatJson.contains("entity.minecraft.warden"));
            assertTrue(mobFormatJson.contains("₂"));
        }
    }
}
