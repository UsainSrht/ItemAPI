package me.usainsrht.itemapi.yamlitem;

import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

/**
 * Entry point for parsing {@link ItemStack}s from YAML.
 */
public final class YamlItem {

    private static final YamlItemParser PARSER = new YamlItemParser();

    private YamlItem() {
    }

    public static ItemStack parse(ConfigurationSection section) {
        return PARSER.parse(section);
    }

    public static ItemStack parse(ConfigurationSection section, TagResolver... tagResolvers) {
        return PARSER.parse(section, tagResolvers);
    }

    public static ItemStack parse(ConfigurationSection section, YamlItemOptions options) {
        return PARSER.parse(section, options);
    }

    public static ItemStack parse(Map<?, ?> map) {
        return PARSER.parse(map);
    }

    public static ItemStack parse(Map<?, ?> map, TagResolver... tagResolvers) {
        return PARSER.parse(map, tagResolvers);
    }

    public static ItemStack parse(Map<?, ?> map, YamlItemOptions options) {
        return PARSER.parse(map, options);
    }

    public static YamlItemParser parser() {
        return PARSER;
    }
}
