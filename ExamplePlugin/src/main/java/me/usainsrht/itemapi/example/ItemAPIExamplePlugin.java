package me.usainsrht.itemapi.example;

import me.usainsrht.itemapi.itemplaceholder.ItemExpansionProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class ItemAPIExamplePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) {
            // noinspection ResultOfMethodCallIgnored
            getDataFolder().mkdirs();
        }
        saveResource("items.yml", false);

        ItemRegistry itemRegistry = new ItemRegistry(this);
        itemRegistry.reload();

        ItemApiCommand command = new ItemApiCommand(this, itemRegistry);
        var pluginCommand = getCommand("itemapi");
        if (pluginCommand == null) {
            getLogger().severe("Command 'itemapi' is missing from plugin.yml");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        pluginCommand.setExecutor(command);
        pluginCommand.setTabCompleter(command);

        // Register MiniPlaceholders expansion if the plugin is present
        if (getServer().getPluginManager().getPlugin("MiniPlaceholders") != null) {
            new ItemExpansionProvider().provideExpansion().register();
            getLogger().info("MiniPlaceholders expansion registered.");
        }
    }
}

