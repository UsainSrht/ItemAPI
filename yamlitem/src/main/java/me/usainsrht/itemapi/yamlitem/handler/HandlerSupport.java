package me.usainsrht.itemapi.yamlitem.handler;

import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import io.papermc.paper.registry.RegistryKey;
import me.usainsrht.itemapi.yamlitem.YamlItemParser;
import me.usainsrht.itemapi.yamlitem.YamlParseException;
import me.usainsrht.itemapi.yamlitem.internal.RegistryUtil;
import me.usainsrht.itemapi.yamlitem.internal.TextUtil;
import me.usainsrht.itemapi.yamlitem.internal.ValueUtil;
import me.usainsrht.itemapi.yamlitem.internal.YamlNode;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

final class HandlerSupport {

    private HandlerSupport() {
    }

    static YamlNode asNode(Object value, String path) {
        if (value instanceof ConfigurationSection section) {
            return YamlNode.of(section, path);
        }
        if (value instanceof Map<?, ?> map) {
            return YamlNode.of(map, path);
        }
        throw new YamlParseException(path, "expected a map/section");
    }

    static @Nullable YamlNode asNodeOrNull(Object value, String path) {
        if (value == null) {
            return null;
        }
        if (value instanceof String || value instanceof Number || value instanceof Boolean) {
            return null;
        }
        return asNode(value, path);
    }

    @SuppressWarnings("unchecked")
    static <T> void set(ItemStack stack, DataComponentType type, T value) {
        stack.setData((DataComponentType.Valued<T>) type, value);
    }

    static Component component(Object value, String path) {
        return YamlItemParser.text(value, path);
    }

    static List<Component> components(Object value, String path) {
        if (!(value instanceof List<?> list)) {
            return List.of(component(value, path));
        }
        List<Component> result = new ArrayList<>(list.size());
        for (int i = 0; i < list.size(); i++) {
            result.add(component(list.get(i), path + "[" + i + "]"));
        }
        return result;
    }

    static PotionEffect potionEffect(Object value, String path) {
        if (value instanceof PotionEffect effect) {
            return effect;
        }
        YamlNode node = asNode(value, path);
        PotionEffectType type = RegistryUtil.require(RegistryKey.MOB_EFFECT,
                node.contains("type") ? node.raw("type") : node.raw("effect"),
                node.contains("type") ? node.childPath("type") : node.childPath("effect"));
        int duration = ValueUtil.intOr(node, "duration", 200);
        int amplifier = ValueUtil.intOr(node, "amplifier", 0);
        boolean ambient = ValueUtil.boolOr(node, "ambient", false);
        boolean particles = ValueUtil.boolOr(node, "particles", true);
        boolean icon = ValueUtil.boolOr(node, "icon", true);
        return new PotionEffect(type, duration, amplifier, ambient, particles, icon);
    }

    static List<PotionEffect> potionEffects(Object value, String path) {
        if (!(value instanceof List<?> list)) {
            return List.of(potionEffect(value, path));
        }
        List<PotionEffect> effects = new ArrayList<>(list.size());
        for (int i = 0; i < list.size(); i++) {
            effects.add(potionEffect(list.get(i), path + "[" + i + "]"));
        }
        return effects;
    }

    static ConsumeEffect consumeEffect(Object value, String path) {
        YamlNode node = asNode(value, path);
        String type = ValueUtil.requireString(node, "type").toLowerCase();
        return switch (type) {
            case "teleport_randomly", "teleport-randomly" ->
                    ConsumeEffect.teleportRandomlyEffect(ValueUtil.floatOr(node, "diameter", 16.0f));
            case "remove_effects", "remove-effects" ->
                    ConsumeEffect.removeEffects(RegistryUtil.keySet(RegistryKey.MOB_EFFECT, node, "effects"));
            case "play_sound", "play-sound" ->
                    ConsumeEffect.playSoundConsumeEffect(ValueUtil.key(node, "sound"));
            case "clear_all_status_effects", "clear-all-status-effects", "clear_all" ->
                    ConsumeEffect.clearAllStatusEffects();
            case "apply_effects", "apply-effects", "apply_status_effects" ->
                    ConsumeEffect.applyStatusEffects(
                            potionEffects(node.raw("effects"), node.childPath("effects")),
                            ValueUtil.floatOr(node, "probability", 1.0f)
                    );
            default -> throw new YamlParseException(path, "unknown consume effect type: " + type);
        };
    }

    static List<ConsumeEffect> consumeEffects(Object value, String path) {
        if (value == null) {
            return List.of();
        }
        if (!(value instanceof List<?> list)) {
            return List.of(consumeEffect(value, path));
        }
        List<ConsumeEffect> effects = new ArrayList<>(list.size());
        for (int i = 0; i < list.size(); i++) {
            effects.add(consumeEffect(list.get(i), path + "[" + i + "]"));
        }
        return effects;
    }

    static FireworkEffect fireworkEffect(Object value, String path) {
        if (value instanceof FireworkEffect effect) {
            return effect;
        }
        YamlNode node = asNode(value, path);
        FireworkEffect.Builder builder = FireworkEffect.builder();
        FireworkEffect.Type type = ValueUtil.enumOrNull(node, "type", FireworkEffect.Type.class);
        if (type != null) {
            builder.with(type);
        }
        if (node.contains("colors")) {
            for (Object color : node.list("colors")) {
                builder.withColor(ValueUtil.color(color, node.childPath("colors")));
            }
        } else if (node.contains("color")) {
            builder.withColor(ValueUtil.color(node, "color"));
        }
        if (node.contains("fade_colors") || node.contains("fade")) {
            String key = node.contains("fade_colors") ? "fade_colors" : "fade";
            for (Object color : node.list(key)) {
                builder.withFade(ValueUtil.color(color, node.childPath(key)));
            }
        }
        builder.flicker(ValueUtil.boolOr(node, "flicker", false));
        builder.trail(ValueUtil.boolOr(node, "trail", false));
        return builder.build();
    }

    static AttributeModifier attributeModifier(YamlNode node) {
        Key key = node.contains("id") ? ValueUtil.key(node, "id")
                : node.contains("key") ? ValueUtil.key(node, "key")
                : Key.key("itemapi", "modifier");
        double amount = node.contains("amount") ? ValueUtil.requireFloat(node, "amount")
                : ValueUtil.requireFloat(node, "value");
        AttributeModifier.Operation operation = ValueUtil.enumValue(node, "operation", AttributeModifier.Operation.class);
        org.bukkit.inventory.EquipmentSlotGroup group = null;
        if (node.contains("slot") || node.contains("slot_group")) {
            String slotKey = node.contains("slot_group") ? "slot_group" : "slot";
            group = org.bukkit.inventory.EquipmentSlotGroup.getByName(
                    String.valueOf(node.raw(slotKey)).toUpperCase().replace('-', '_'));
            if (group == null) {
                throw new YamlParseException(node.childPath(slotKey), "unknown equipment slot group");
            }
        }
        if (group != null) {
            return new AttributeModifier(RegistryUtil.asNamespaced(key), amount, operation, group);
        }
        return new AttributeModifier(RegistryUtil.asNamespaced(key), amount, operation);
    }

    static Location location(Object value, String path) {
        YamlNode node = asNode(value, path);
        String worldName = ValueUtil.string(node, "world");
        World world = worldName == null ? null : org.bukkit.Bukkit.getWorld(worldName);
        double x = ValueUtil.asDouble(node.raw("x"), node.childPath("x"));
        double y = ValueUtil.asDouble(node.raw("y"), node.childPath("y"));
        double z = ValueUtil.asDouble(node.raw("z"), node.childPath("z"));
        float yaw = ValueUtil.floatOr(node, "yaw", 0f);
        float pitch = ValueUtil.floatOr(node, "pitch", 0f);
        return new Location(world, x, y, z, yaw, pitch);
    }

    static <T> ComponentHandler valued(BiConsumer<ItemStack, T> applier) {
        return (stack, type, value, path, parser) -> {
            @SuppressWarnings("unchecked")
            T typed = (T) value;
            applier.accept(stack, typed);
        };
    }

    static Color requireColor(Object value, String path) {
        return ValueUtil.color(value, path);
    }

    static String mm(Object value) {
        return String.valueOf(value);
    }

    static Component mmComponent(Object value) {
        return TextUtil.miniMessage(String.valueOf(value));
    }
}
