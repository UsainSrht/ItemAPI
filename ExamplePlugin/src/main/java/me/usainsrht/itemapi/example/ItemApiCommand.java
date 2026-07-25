package me.usainsrht.itemapi.example;

import me.usainsrht.itemapi.itemtext.ItemText;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Registry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class ItemApiCommand implements CommandExecutor, TabCompleter {

    private static final int SPRITES_PER_MESSAGE = 40;

    private final ItemAPIExamplePlugin plugin;
    private final ItemRegistry itemRegistry;

    public ItemApiCommand(ItemAPIExamplePlugin plugin, ItemRegistry itemRegistry) {
        this.plugin = plugin;
        this.itemRegistry = itemRegistry;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Component.text("Usage: /itemapi <yamlitemkey|reload|testsprite>", NamedTextColor.RED));
            return true;
        }

        String sub = args[0].toLowerCase(Locale.ROOT);
        return switch (sub) {
            case "reload" -> {
                itemRegistry.reload();
                sender.sendMessage(Component.text("Reloaded items.yml (" + itemRegistry.keys().size() + " items)", NamedTextColor.GREEN));
                yield true;
            }
            case "testsprite" -> {
                printSprites(sender);
                yield true;
            }
            default -> giveYamlItem(sender, args[0]);
        };
    }

    private boolean giveYamlItem(CommandSender sender, String key) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can receive items.", NamedTextColor.RED));
            return true;
        }

        ItemStack item = itemRegistry.get(key);
        if (item == null) {
            sender.sendMessage(Component.text("Unknown yaml item key: " + key, NamedTextColor.RED));
            return true;
        }

        player.getInventory().addItem(item);
        sender.sendMessage(Component.text()
                .append(Component.text("Gave ", NamedTextColor.GREEN))
                .append(ItemText.format(item))
                .append(Component.text(" (" + key + ")", NamedTextColor.DARK_GRAY))
                .build());
        return true;
    }

    private void printSprites(CommandSender sender) {
        List<Component> sprites = new ArrayList<>();
        int total = 0;

        for (ItemType itemType : Registry.ITEM) {
            if (itemType == null) {
                continue;
            }
            ItemStack stack;
            try {
                stack = itemType.createItemStack();
            } catch (Exception ignored) {
                continue;
            }
            if (stack.getType().isAir()) {
                continue;
            }

            Component name = Component.translatable(itemType.getTranslationKey())
                    .append(Component.text(" (" + itemType.key().asString() + ")", NamedTextColor.DARK_GRAY));

            Component sprite = ItemText.format(stack, options -> options.pattern("<item_sprite>"))
                    .hoverEvent(HoverEvent.showText(name));

            sprites.add(sprite);
            total++;

            if (sprites.size() >= SPRITES_PER_MESSAGE) {
                sendSpriteLine(sender, sprites);
                sprites.clear();
            }
        }

        if (!sprites.isEmpty()) {
            sendSpriteLine(sender, sprites);
        }

        sender.sendMessage(Component.text("Printed " + total + " ItemType sprites. Hover to see names/keys.", NamedTextColor.GRAY));
        plugin.getLogger().info("testsprite printed " + total + " ItemType sprites for " + sender.getName());
    }

    private static void sendSpriteLine(CommandSender sender, List<Component> sprites) {
        var line = Component.text();
        for (Component sprite : sprites) {
            line.append(sprite);
        }
        sender.sendMessage(line.build());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length != 1) {
            return List.of();
        }
        String prefix = args[0].toLowerCase(Locale.ROOT);
        List<String> completions = new ArrayList<>();
        if ("reload".startsWith(prefix)) {
            completions.add("reload");
        }
        if ("testsprite".startsWith(prefix)) {
            completions.add("testsprite");
        }
        for (String key : itemRegistry.keys()) {
            if (key.startsWith(prefix)) {
                completions.add(key);
            }
        }
        return completions;
    }
}
