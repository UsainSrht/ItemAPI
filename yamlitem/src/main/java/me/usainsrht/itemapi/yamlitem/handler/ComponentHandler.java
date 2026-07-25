package me.usainsrht.itemapi.yamlitem.handler;

import io.papermc.paper.datacomponent.DataComponentType;
import me.usainsrht.itemapi.yamlitem.YamlItemParser;
import me.usainsrht.itemapi.yamlitem.internal.YamlNode;
import org.bukkit.inventory.ItemStack;

@FunctionalInterface
public interface ComponentHandler {

    void apply(ItemStack stack, DataComponentType type, Object value, String path, YamlItemParser parser);

    default void unset(ItemStack stack, DataComponentType type) {
        stack.unsetData(type);
    }
}
