package me.usainsrht.itemapi.yamlitem;

import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import me.usainsrht.itemapi.yamlitem.handler.ComponentHandler;
import me.usainsrht.itemapi.yamlitem.handler.ComponentHandlerRegistry;
import me.usainsrht.itemapi.yamlitem.internal.TextUtil;
import me.usainsrht.itemapi.yamlitem.internal.ValueUtil;
import me.usainsrht.itemapi.yamlitem.internal.YamlNode;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;

import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Parses YAML maps/sections into {@link ItemStack}s using Paper data components.
 * <p>
 * Root-level shortcuts ({@code name}, {@code lore}, {@code enchantments}, ...) win over
 * the same keys under {@code components}.
 */
public final class YamlItemParser {

    private static final Set<String> ROOT_META_KEYS = Set.of(
            "material", "type", "item", "amount", "count", "components"
    );

    private final ComponentHandlerRegistry handlers = new ComponentHandlerRegistry();

    public ItemStack parse(ConfigurationSection section) {
        return parse(YamlNode.of(section));
    }

    public ItemStack parse(Map<?, ?> map) {
        return parse(YamlNode.of(map));
    }

    public ItemStack parse(YamlNode node) {
        Material material = resolveMaterial(node);
        int amount = ValueUtil.intOr(node, "amount", ValueUtil.intOr(node, "count", 1));
        ItemStack stack = ItemStack.of(material, Math.max(1, amount));

        Set<String> applied = new HashSet<>();
        applyRootShortcuts(stack, node, applied);

        YamlNode components = node.child("components");
        if (components != null) {
            applyComponentsSection(stack, components, applied);
        }

        return stack;
    }

    private Material resolveMaterial(YamlNode node) {
        Object raw = firstPresent(node, "material", "type", "item");
        if (raw == null) {
            throw new YamlParseException(node.path(), "missing material/type/item");
        }
        String text = String.valueOf(raw).trim().toLowerCase(Locale.ROOT);
        if (text.contains(":")) {
            ItemType itemType = org.bukkit.Registry.ITEM.get(ValueUtil.key(raw, node.path()));
            if (itemType != null) {
                Material material = itemType.asMaterial();
                if (material != null) {
                    return material;
                }
            }
        }
        Material material = Material.matchMaterial(text);
        if (material == null || material.isAir()) {
            throw new YamlParseException(node.path(), "unknown material: " + raw);
        }
        return material;
    }

    private Object firstPresent(YamlNode node, String... keys) {
        for (String key : keys) {
            if (node.contains(key)) {
                return node.raw(key);
            }
        }
        return null;
    }

    private void applyRootShortcuts(ItemStack stack, YamlNode node, Set<String> applied) {
        // name / custom_name
        if (node.contains("name") || node.contains("custom_name")) {
            Object value = node.contains("name") ? node.raw("name") : node.raw("custom_name");
            String path = node.contains("name") ? node.childPath("name") : node.childPath("custom_name");
            applyValued(stack, DataComponentTypes.CUSTOM_NAME, value, path);
            applied.add("custom_name");
            applied.add("name");
        }
        if (node.contains("item_name")) {
            applyValued(stack, DataComponentTypes.ITEM_NAME, node.raw("item_name"), node.childPath("item_name"));
            applied.add("item_name");
        }
        if (node.contains("lore")) {
            applyValued(stack, DataComponentTypes.LORE, node.raw("lore"), node.childPath("lore"));
            applied.add("lore");
        }
        if (node.contains("enchantments") || node.contains("enchants")) {
            Object value = node.contains("enchantments") ? node.raw("enchantments") : node.raw("enchants");
            String path = node.contains("enchantments") ? node.childPath("enchantments") : node.childPath("enchants");
            applyValued(stack, DataComponentTypes.ENCHANTMENTS, value, path);
            applied.add("enchantments");
            applied.add("enchants");
        }
        if (node.contains("stored_enchantments") || node.contains("stored_enchants")) {
            Object value = node.contains("stored_enchantments") ? node.raw("stored_enchantments") : node.raw("stored_enchants");
            String path = node.contains("stored_enchantments") ? node.childPath("stored_enchantments") : node.childPath("stored_enchants");
            applyValued(stack, DataComponentTypes.STORED_ENCHANTMENTS, value, path);
            applied.add("stored_enchantments");
            applied.add("stored_enchants");
        }
        if (node.contains("unbreakable") || node.contains("!unbreakable")) {
            String key = node.contains("unbreakable") ? "unbreakable" : "!unbreakable";
            applyUnbreakable(stack, node.raw(key), node.childPath(key), key.startsWith("!"));
            applied.add("unbreakable");
        }
        if (node.contains("damage")) {
            applyValued(stack, DataComponentTypes.DAMAGE, node.raw("damage"), node.childPath("damage"));
            applied.add("damage");
        }
        if (node.contains("max_damage")) {
            applyValued(stack, DataComponentTypes.MAX_DAMAGE, node.raw("max_damage"), node.childPath("max_damage"));
            applied.add("max_damage");
        }
        if (node.contains("max_stack_size")) {
            applyValued(stack, DataComponentTypes.MAX_STACK_SIZE, node.raw("max_stack_size"), node.childPath("max_stack_size"));
            applied.add("max_stack_size");
        }
        if (node.contains("custom_model_data")) {
            applyValued(stack, DataComponentTypes.CUSTOM_MODEL_DATA, node.raw("custom_model_data"), node.childPath("custom_model_data"));
            applied.add("custom_model_data");
        }
        if (node.contains("glint") || node.contains("enchantment_glint_override")) {
            Object value = node.contains("glint") ? node.raw("glint") : node.raw("enchantment_glint_override");
            String path = node.contains("glint") ? node.childPath("glint") : node.childPath("enchantment_glint_override");
            applyValued(stack, DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, value, path);
            applied.add("enchantment_glint_override");
            applied.add("glint");
        }
        if (node.contains("rarity")) {
            applyValued(stack, DataComponentTypes.RARITY, node.raw("rarity"), node.childPath("rarity"));
            applied.add("rarity");
        }
        if (node.contains("item_model")) {
            applyValued(stack, DataComponentTypes.ITEM_MODEL, node.raw("item_model"), node.childPath("item_model"));
            applied.add("item_model");
        }
        if (node.contains("repair_cost")) {
            applyValued(stack, DataComponentTypes.REPAIR_COST, node.raw("repair_cost"), node.childPath("repair_cost"));
            applied.add("repair_cost");
        }
        if (node.contains("hide_tooltip")) {
            applyHideTooltip(stack, node.raw("hide_tooltip"), node.childPath("hide_tooltip"));
            applied.add("hide_tooltip");
        }

        // Any other root key that matches a component id (except meta keys) is treated as a shortcut
        for (String key : node.keys()) {
            if (ROOT_META_KEYS.contains(key) || applied.contains(key) || key.equals("name") || key.equals("glint") || key.equals("enchants") || key.equals("stored_enchants")) {
                continue;
            }
            String componentId = key.startsWith("!") ? key.substring(1) : key;
            DataComponentType type = handlers.resolveType(componentId);
            if (type == null) {
                continue;
            }
            if (applied.contains(type.getKey().getKey())) {
                continue;
            }
            applyComponent(stack, type, key, node.raw(key), node.childPath(key));
            applied.add(type.getKey().getKey());
        }
    }

    private void applyComponentsSection(ItemStack stack, YamlNode components, Set<String> applied) {
        for (String key : components.keys()) {
            boolean unsetPrefix = key.startsWith("!");
            String componentId = unsetPrefix ? key.substring(1) : key;
            if (componentId.equals("hide_tooltip")) {
                if (applied.contains("hide_tooltip")) {
                    continue; // root shortcut wins
                }
                if (unsetPrefix) {
                    boolean shouldUnset = components.raw(key) == null || ValueUtil.asBoolean(components.raw(key), components.childPath(key));
                    if (shouldUnset) {
                        applyHideTooltip(stack, false, components.childPath(key));
                        applied.add("hide_tooltip");
                    }
                    continue;
                }
                applyHideTooltip(stack, components.raw(key), components.childPath(key));
                applied.add("hide_tooltip");
                continue;
            }
            if (componentId.equals("unbreakable")) {
                if (applied.contains("unbreakable")) {
                    continue; // root shortcut wins
                }
                applyUnbreakable(stack, components.raw(key), components.childPath(key), unsetPrefix);
                applied.add("unbreakable");
                continue;
            }
            DataComponentType type = handlers.requireType(componentId, components.childPath(key));
            String id = type.getKey().getKey();
            if (applied.contains(id)) {
                continue; // root shortcut wins
            }
            Object value = components.raw(key);
            if (unsetPrefix) {
                boolean shouldUnset = value == null || ValueUtil.asBoolean(value, components.childPath(key));
                if (shouldUnset) {
                    stack.unsetData(type);
                    applied.add(id);
                }
                continue;
            }
            applyComponent(stack, type, key, value, components.childPath(key));
            applied.add(id);
        }
    }

    public void applyUnbreakable(ItemStack stack, Object value, String path, boolean unsetPrefix) {
        if (unsetPrefix) {
            boolean shouldUnset = value == null || ValueUtil.asBoolean(value, path);
            if (shouldUnset) {
                stack.unsetData(DataComponentTypes.UNBREAKABLE);
            }
            return;
        }
        if (value == null) {
            stack.unsetData(DataComponentTypes.UNBREAKABLE);
            return;
        }

        boolean enabled = true;
        boolean showInTooltip = true;

        if (value instanceof Boolean bool) {
            enabled = bool;
        } else if (value instanceof ConfigurationSection section) {
            YamlNode mapNode = YamlNode.of(section, path);
            enabled = mapNode.contains("enabled") ? ValueUtil.boolOr(mapNode, "enabled", true) : true;
            showInTooltip = mapNode.contains("show_in_tooltip") ? ValueUtil.boolOr(mapNode, "show_in_tooltip", true)
                    : (mapNode.contains("show_tooltip") ? ValueUtil.boolOr(mapNode, "show_tooltip", true) : true);
        } else if (value instanceof Map<?, ?> map) {
            YamlNode mapNode = YamlNode.of(map, path);
            enabled = mapNode.contains("enabled") ? ValueUtil.boolOr(mapNode, "enabled", true) : true;
            showInTooltip = mapNode.contains("show_in_tooltip") ? ValueUtil.boolOr(mapNode, "show_in_tooltip", true)
                    : (mapNode.contains("show_tooltip") ? ValueUtil.boolOr(mapNode, "show_tooltip", true) : true);
        } else {
            enabled = ValueUtil.asBoolean(value, path);
        }

        if (enabled) {
            stack.setData(DataComponentTypes.UNBREAKABLE);
            if (!showInTooltip) {
                io.papermc.paper.datacomponent.item.TooltipDisplay existing = stack.getData(DataComponentTypes.TOOLTIP_DISPLAY);
                Set<DataComponentType> hidden = new HashSet<>(existing != null ? existing.hiddenComponents() : Set.of());
                hidden.add(DataComponentTypes.UNBREAKABLE);
                io.papermc.paper.datacomponent.item.TooltipDisplay.Builder builder = io.papermc.paper.datacomponent.item.TooltipDisplay.tooltipDisplay()
                        .hiddenComponents(hidden);
                if (existing != null) {
                    builder.hideTooltip(existing.hideTooltip());
                }
                stack.setData(DataComponentTypes.TOOLTIP_DISPLAY, builder.build());
            } else {
                io.papermc.paper.datacomponent.item.TooltipDisplay existing = stack.getData(DataComponentTypes.TOOLTIP_DISPLAY);
                if (existing != null && existing.hiddenComponents().contains(DataComponentTypes.UNBREAKABLE)) {
                    Set<DataComponentType> hidden = new HashSet<>(existing.hiddenComponents());
                    hidden.remove(DataComponentTypes.UNBREAKABLE);
                    io.papermc.paper.datacomponent.item.TooltipDisplay.Builder builder = io.papermc.paper.datacomponent.item.TooltipDisplay.tooltipDisplay()
                            .hiddenComponents(hidden)
                            .hideTooltip(existing.hideTooltip());
                    stack.setData(DataComponentTypes.TOOLTIP_DISPLAY, builder.build());
                }
            }
        } else {
            stack.unsetData(DataComponentTypes.UNBREAKABLE);
        }
    }

    private void applyFlagOrUnset(ItemStack stack, DataComponentType.NonValued type, Object value, String path) {
        boolean enabled = value == null || ValueUtil.asBoolean(value, path);
        if (enabled) {
            stack.setData(type);
        } else {
            stack.unsetData(type);
        }
    }

    private void applyHideTooltip(ItemStack stack, Object value, String path) {
        boolean hide = value == null || ValueUtil.asBoolean(value, path);
        io.papermc.paper.datacomponent.item.TooltipDisplay existing = stack.getData(DataComponentTypes.TOOLTIP_DISPLAY);
        io.papermc.paper.datacomponent.item.TooltipDisplay.Builder builder = io.papermc.paper.datacomponent.item.TooltipDisplay.tooltipDisplay()
                .hideTooltip(hide);
        if (existing != null) {
            builder.hiddenComponents(existing.hiddenComponents());
        }
        stack.setData(DataComponentTypes.TOOLTIP_DISPLAY, builder.build());
    }

    private void applyValued(ItemStack stack, DataComponentType type, Object value, String path) {
        applyComponent(stack, type, type.getKey().getKey(), value, path);
    }

    private void applyComponent(ItemStack stack, DataComponentType type, String key, Object value, String path) {
        boolean unsetPrefix = key != null && key.startsWith("!");
        if (unsetPrefix) {
            boolean shouldUnset = value == null || ValueUtil.asBoolean(value, path);
            if (shouldUnset) {
                stack.unsetData(type);
            }
            return;
        }
        if (value == null) {
            stack.unsetData(type);
            return;
        }
        if (type == DataComponentTypes.UNBREAKABLE) {
            applyUnbreakable(stack, value, path, false);
            return;
        }
        if (type instanceof DataComponentType.NonValued nonValued) {
            boolean enabled = ValueUtil.asBoolean(value, path);
            if (enabled) {
                stack.setData(nonValued);
            } else {
                stack.unsetData(nonValued);
            }
            return;
        }
        if (value instanceof Boolean bool && !bool) {
            stack.unsetData(type);
            return;
        }
        ComponentHandler handler = handlers.requireHandler(type, path);
        handler.apply(stack, type, value, path, this);
    }

    public ComponentHandlerRegistry handlers() {
        return handlers;
    }

    public ItemStack parseNestedItem(Object value, String path) {
        if (value instanceof ConfigurationSection section) {
            return parse(YamlNode.of(section, path));
        }
        if (value instanceof Map<?, ?> map) {
            return parse(YamlNode.of(map, path));
        }
        if (value instanceof String text) {
            Material material = Material.matchMaterial(text);
            if (material == null) {
                throw new YamlParseException(path, "unknown material: " + text);
            }
            return ItemStack.of(material);
        }
        throw new YamlParseException(path, "expected item map/section or material name");
    }

    public static String asMiniMessagePath(String path) {
        return path;
    }

    public static net.kyori.adventure.text.Component text(Object value, String path) {
        if (value instanceof net.kyori.adventure.text.Component component) {
            return component;
        }
        return TextUtil.miniMessage(String.valueOf(value));
    }
}
