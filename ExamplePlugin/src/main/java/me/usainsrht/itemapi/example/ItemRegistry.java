package me.usainsrht.itemapi.example;

import me.usainsrht.itemapi.yamlitem.YamlItem;
import me.usainsrht.itemapi.yamlitem.YamlParseException;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

/**
 * Loads and caches items defined in {@code items.yml}.
 */
public final class ItemRegistry {

    private final ItemAPIExamplePlugin plugin;
    private final Map<String, ItemStack> items = new LinkedHashMap<>();

    public ItemRegistry(ItemAPIExamplePlugin plugin) {
        this.plugin = plugin;
    }

    public void reload() {
        items.clear();

        File file = new File(plugin.getDataFolder(), "items.yml");
        if (!file.exists()) {
            plugin.saveResource("items.yml", false);
        }

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        int loaded = 0;
        int failed = 0;

        for (String key : yaml.getKeys(false)) {
            ConfigurationSection section = yaml.getConfigurationSection(key);
            if (section == null) {
                plugin.getLogger().warning("Skipping '" + key + "': not a configuration section");
                failed++;
                continue;
            }
            try {
                items.put(key.toLowerCase(), YamlItem.parse(section));
                loaded++;
            } catch (YamlParseException ex) {
                failed++;
                plugin.getLogger().log(Level.WARNING, "Failed to parse item '" + key + "': " + ex.getMessage());
            }
        }

        plugin.getLogger().info("Loaded " + loaded + " yaml item(s)" + (failed == 0 ? "" : " (" + failed + " failed)"));
    }

    public ItemStack get(String key) {
        ItemStack stack = items.get(key.toLowerCase());
        return stack == null ? null : stack.clone();
    }

    public Set<String> keys() {
        return Collections.unmodifiableSet(items.keySet());
    }
}
