package me.usainsrht.itemapi.yamlitem;

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

    public static ItemStack parse(Map<?, ?> map) {
        return PARSER.parse(map);
    }

    public static YamlItemParser parser() {
        return PARSER;
    }
}
