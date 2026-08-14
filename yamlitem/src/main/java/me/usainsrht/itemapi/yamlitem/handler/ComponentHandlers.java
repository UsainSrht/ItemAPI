package me.usainsrht.itemapi.yamlitem.handler;

import io.papermc.paper.block.BlockPredicate;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.AttackRange;
import io.papermc.paper.datacomponent.item.BannerPatternLayers;
import io.papermc.paper.datacomponent.item.BlocksAttacks;
import io.papermc.paper.datacomponent.item.BundleContents;
import io.papermc.paper.datacomponent.item.ChargedProjectiles;
import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.CustomModelData;
import io.papermc.paper.datacomponent.item.DamageResistant;
import io.papermc.paper.datacomponent.item.DeathProtection;
import io.papermc.paper.datacomponent.item.DyedItemColor;
import io.papermc.paper.datacomponent.item.Enchantable;
import io.papermc.paper.datacomponent.item.Equippable;
import io.papermc.paper.datacomponent.item.Fireworks;
import io.papermc.paper.datacomponent.item.FoodProperties;
import io.papermc.paper.datacomponent.item.ItemAdventurePredicate;
import io.papermc.paper.datacomponent.item.ItemArmorTrim;
import io.papermc.paper.datacomponent.item.ItemAttributeModifiers;
import io.papermc.paper.datacomponent.item.ItemContainerContents;
import io.papermc.paper.datacomponent.item.ItemEnchantments;
import io.papermc.paper.datacomponent.item.ItemLore;
import io.papermc.paper.datacomponent.item.JukeboxPlayable;
import io.papermc.paper.datacomponent.item.KineticWeapon;
import io.papermc.paper.datacomponent.item.LodestoneTracker;
import io.papermc.paper.datacomponent.item.MapDecorations;
import io.papermc.paper.datacomponent.item.MapId;
import io.papermc.paper.datacomponent.item.MapItemColor;
import io.papermc.paper.datacomponent.item.OminousBottleAmplifier;
import io.papermc.paper.datacomponent.item.PiercingWeapon;
import io.papermc.paper.datacomponent.item.PotDecorations;
import io.papermc.paper.datacomponent.item.PotionContents;
import io.papermc.paper.datacomponent.item.Repairable;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import io.papermc.paper.datacomponent.item.SeededContainerLoot;
import io.papermc.paper.datacomponent.item.SulfurCubeContent;
import io.papermc.paper.datacomponent.item.SuspiciousStewEffects;
import io.papermc.paper.datacomponent.item.SwingAnimation;
import io.papermc.paper.datacomponent.item.Tool;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import io.papermc.paper.datacomponent.item.UseCooldown;
import io.papermc.paper.datacomponent.item.UseEffects;
import io.papermc.paper.datacomponent.item.UseRemainder;
import io.papermc.paper.datacomponent.item.Weapon;
import io.papermc.paper.datacomponent.item.WritableBookContent;
import io.papermc.paper.datacomponent.item.WrittenBookContent;
import io.papermc.paper.datacomponent.item.blocksattacks.DamageReduction;
import io.papermc.paper.datacomponent.item.blocksattacks.ItemDamageFunction;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import io.papermc.paper.item.MapPostProcessing;
import io.papermc.paper.potion.SuspiciousEffectEntry;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import me.usainsrht.itemapi.yamlitem.YamlParseException;
import me.usainsrht.itemapi.yamlitem.internal.RegistryUtil;
import me.usainsrht.itemapi.yamlitem.internal.ValueUtil;
import me.usainsrht.itemapi.yamlitem.internal.YamlNode;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.TriState;
import org.bukkit.Art;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.JukeboxSong;
import org.bukkit.MusicInstrument;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Axolotl;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Frog;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Llama;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Salmon;
import org.bukkit.entity.TropicalFish;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.ZombieNautilus;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.map.MapCursor;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Registers handlers for every {@link DataComponentTypes} entry in Paper 26.2.
 */
public final class ComponentHandlers {

    private ComponentHandlers() {
    }

    public static void registerAll(ComponentHandlerRegistry registry) {
        // Non-valued flags
        registry.register(DataComponentTypes.UNBREAKABLE, (stack, type, value, path, parser) ->
                parser.applyUnbreakable(stack, value, path, false));
        registry.register(DataComponentTypes.INTANGIBLE_PROJECTILE, flag());
        registry.register(DataComponentTypes.GLIDER, flag());

        // Scalars
        registry.register(DataComponentTypes.MAX_STACK_SIZE, integer());
        registry.register(DataComponentTypes.MAX_DAMAGE, integer());
        registry.register(DataComponentTypes.DAMAGE, integer());
        registry.register(DataComponentTypes.REPAIR_COST, integer());
        registry.register(DataComponentTypes.MINIMUM_ATTACK_CHARGE, floatValue());
        registry.register(DataComponentTypes.POTION_DURATION_SCALE, floatValue());
        registry.register(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, booleanValue());

        // Text
        registry.register(DataComponentTypes.CUSTOM_NAME, (stack, type, value, path, parser) ->
                HandlerSupport.set(stack, type, HandlerSupport.component(value, path)));
        registry.register(DataComponentTypes.ITEM_NAME, (stack, type, value, path, parser) ->
                HandlerSupport.set(stack, type, HandlerSupport.component(value, path)));
        registry.register(DataComponentTypes.LORE, ComponentHandlers::lore);

        // Keys
        registry.register(DataComponentTypes.ITEM_MODEL, keyValue());
        registry.register(DataComponentTypes.TOOLTIP_STYLE, keyValue());
        registry.register(DataComponentTypes.BREAK_SOUND, keyValue());
        registry.register(DataComponentTypes.NOTE_BLOCK_SOUND, keyValue());

        // Enums / simple registry
        registry.register(DataComponentTypes.RARITY, (stack, type, value, path, parser) ->
                HandlerSupport.set(stack, type, ValueUtil.enumValue(ItemRarity.class, value, path)));
        registry.register(DataComponentTypes.MAP_POST_PROCESSING, (stack, type, value, path, parser) ->
                HandlerSupport.set(stack, type, ValueUtil.enumValue(MapPostProcessing.class, value, path)));
        registry.register(DataComponentTypes.DAMAGE_TYPE, (stack, type, value, path, parser) ->
                HandlerSupport.set(stack, type, RegistryUtil.require(RegistryKey.DAMAGE_TYPE, value, path)));
        registry.register(DataComponentTypes.INSTRUMENT, (stack, type, value, path, parser) ->
                HandlerSupport.set(stack, type, RegistryUtil.require(Registry.INSTRUMENT, value, path)));
        registry.register(DataComponentTypes.PROVIDES_TRIM_MATERIAL, (stack, type, value, path, parser) ->
                HandlerSupport.set(stack, type, RegistryUtil.require(Registry.TRIM_MATERIAL, value, path)));
        registry.register(DataComponentTypes.PAINTING_VARIANT, (stack, type, value, path, parser) ->
                HandlerSupport.set(stack, type, RegistryUtil.require(Registry.ART, value, path)));

        // Colors / dyes
        registry.register(DataComponentTypes.DYE, dyeColor());
        registry.register(DataComponentTypes.BASE_COLOR, dyeColor());
        registry.register(DataComponentTypes.CAT_COLLAR, dyeColor());
        registry.register(DataComponentTypes.WOLF_COLLAR, dyeColor());
        registry.register(DataComponentTypes.SHEEP_COLOR, dyeColor());
        registry.register(DataComponentTypes.SHULKER_COLOR, dyeColor());
        registry.register(DataComponentTypes.TROPICAL_FISH_BASE_COLOR, dyeColor());
        registry.register(DataComponentTypes.TROPICAL_FISH_PATTERN_COLOR, dyeColor());
        registry.register(DataComponentTypes.DYED_COLOR, ComponentHandlers::dyedColor);
        registry.register(DataComponentTypes.MAP_COLOR, ComponentHandlers::mapColor);

        // Enchantments
        registry.register(DataComponentTypes.ENCHANTMENTS, enchantments());
        registry.register(DataComponentTypes.STORED_ENCHANTMENTS, enchantments());
        registry.register(DataComponentTypes.ENCHANTABLE, (stack, type, value, path, parser) -> {
            int level = value instanceof Map || value instanceof org.bukkit.configuration.ConfigurationSection
                    ? ValueUtil.requireInt(HandlerSupport.asNode(value, path), "value")
                    : ValueUtil.asInt(value, path);
            HandlerSupport.set(stack, type, Enchantable.enchantable(level));
        });

        // Common complex
        registry.register(DataComponentTypes.CUSTOM_MODEL_DATA, ComponentHandlers::customModelData);
        registry.register(DataComponentTypes.FOOD, ComponentHandlers::food);
        registry.register(DataComponentTypes.CONSUMABLE, ComponentHandlers::consumable);
        registry.register(DataComponentTypes.USE_COOLDOWN, ComponentHandlers::useCooldown);
        registry.register(DataComponentTypes.USE_EFFECTS, ComponentHandlers::useEffects);
        registry.register(DataComponentTypes.USE_REMAINDER, ComponentHandlers::useRemainder);
        registry.register(DataComponentTypes.WEAPON, ComponentHandlers::weapon);
        registry.register(DataComponentTypes.TOOL, ComponentHandlers::tool);
        registry.register(DataComponentTypes.EQUIPPABLE, ComponentHandlers::equippable);
        registry.register(DataComponentTypes.REPAIRABLE, ComponentHandlers::repairable);
        registry.register(DataComponentTypes.DAMAGE_RESISTANT, ComponentHandlers::damageResistant);
        registry.register(DataComponentTypes.ATTRIBUTE_MODIFIERS, ComponentHandlers::attributes);
        registry.register(DataComponentTypes.POTION_CONTENTS, ComponentHandlers::potionContents);
        registry.register(DataComponentTypes.PROFILE, ComponentHandlers::profile);
        registry.register(DataComponentTypes.FIREWORKS, ComponentHandlers::fireworks);
        registry.register(DataComponentTypes.FIREWORK_EXPLOSION, (stack, type, value, path, parser) ->
                HandlerSupport.set(stack, type, HandlerSupport.fireworkEffect(value, path)));
        registry.register(DataComponentTypes.TRIM, ComponentHandlers::trim);
        registry.register(DataComponentTypes.JUKEBOX_PLAYABLE, ComponentHandlers::jukebox);
        registry.register(DataComponentTypes.TOOLTIP_DISPLAY, ComponentHandlers::tooltipDisplay);
        registry.register(DataComponentTypes.LODESTONE_TRACKER, ComponentHandlers::lodestone);
        registry.register(DataComponentTypes.BUNDLE_CONTENTS, nestedItems(BundleContents::bundleContents));
        registry.register(DataComponentTypes.CONTAINER, nestedItems(ItemContainerContents::containerContents));
        registry.register(DataComponentTypes.CHARGED_PROJECTILES, nestedItems(ChargedProjectiles::chargedProjectiles));
        registry.register(DataComponentTypes.SULFUR_CUBE_CONTENT, (stack, type, value, path, parser) ->
                HandlerSupport.set(stack, type, SulfurCubeContent.sulfurCubeContent(parser.parseNestedItem(value, path))));
        registry.register(DataComponentTypes.CONTAINER_LOOT, ComponentHandlers::containerLoot);
        registry.register(DataComponentTypes.MAP_ID, (stack, type, value, path, parser) ->
                HandlerSupport.set(stack, type, MapId.mapId(ValueUtil.asInt(value, path))));
        registry.register(DataComponentTypes.MAP_DECORATIONS, ComponentHandlers::mapDecorations);
        registry.register(DataComponentTypes.OMINOUS_BOTTLE_AMPLIFIER, (stack, type, value, path, parser) ->
                HandlerSupport.set(stack, type, OminousBottleAmplifier.amplifier(ValueUtil.asInt(value, path))));
        registry.register(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS, ComponentHandlers::suspiciousStew);
        registry.register(DataComponentTypes.WRITABLE_BOOK_CONTENT, ComponentHandlers::writableBook);
        registry.register(DataComponentTypes.WRITTEN_BOOK_CONTENT, ComponentHandlers::writtenBook);
        registry.register(DataComponentTypes.BANNER_PATTERNS, ComponentHandlers::bannerPatterns);
        registry.register(DataComponentTypes.POT_DECORATIONS, ComponentHandlers::potDecorations);
        registry.register(DataComponentTypes.CAN_PLACE_ON, adventurePredicate());
        registry.register(DataComponentTypes.CAN_BREAK, adventurePredicate());
        registry.register(DataComponentTypes.DEATH_PROTECTION, ComponentHandlers::deathProtection);
        registry.register(DataComponentTypes.BLOCKS_ATTACKS, ComponentHandlers::blocksAttacks);
        registry.register(DataComponentTypes.ATTACK_RANGE, ComponentHandlers::attackRange);
        registry.register(DataComponentTypes.PIERCING_WEAPON, ComponentHandlers::piercingWeapon);
        registry.register(DataComponentTypes.KINETIC_WEAPON, ComponentHandlers::kineticWeapon);
        registry.register(DataComponentTypes.SWING_ANIMATION, ComponentHandlers::swingAnimation);
        registry.register(DataComponentTypes.RECIPES, ComponentHandlers::recipes);
        registry.register(DataComponentTypes.PROVIDES_BANNER_PATTERNS, (stack, type, value, path, parser) ->
                HandlerSupport.set(stack, type, RegistryUtil.keySet(RegistryKey.BANNER_PATTERN, value, path)));
        registry.register(DataComponentTypes.BLOCK_DATA, (stack, type, value, path, parser) ->
                HandlerSupport.set(stack, type, io.papermc.paper.datacomponent.item.BlockItemDataProperties.blockItemStateProperties().build()));

        // Entity variants
        registerVariants(registry);
    }

    private static void registerVariants(ComponentHandlerRegistry registry) {
        registry.register(DataComponentTypes.FOX_VARIANT, enumComponent(Fox.Type.class));
        registry.register(DataComponentTypes.SALMON_SIZE, enumComponent(Salmon.Variant.class));
        registry.register(DataComponentTypes.PARROT_VARIANT, enumComponent(Parrot.Variant.class));
        registry.register(DataComponentTypes.TROPICAL_FISH_PATTERN, enumComponent(TropicalFish.Pattern.class));
        registry.register(DataComponentTypes.MOOSHROOM_VARIANT, enumComponent(MushroomCow.Variant.class));
        registry.register(DataComponentTypes.RABBIT_VARIANT, enumComponent(Rabbit.Type.class));
        registry.register(DataComponentTypes.HORSE_VARIANT, enumComponent(Horse.Color.class));
        registry.register(DataComponentTypes.LLAMA_VARIANT, enumComponent(Llama.Color.class));
        registry.register(DataComponentTypes.AXOLOTL_VARIANT, enumComponent(Axolotl.Variant.class));

        registry.register(DataComponentTypes.VILLAGER_VARIANT, registryComponent(RegistryKey.VILLAGER_TYPE));
        registry.register(DataComponentTypes.WOLF_VARIANT, registryComponent(RegistryKey.WOLF_VARIANT));
        registry.register(DataComponentTypes.WOLF_SOUND_VARIANT, registryComponent(RegistryKey.WOLF_SOUND_VARIANT));
        registry.register(DataComponentTypes.CAT_VARIANT, registryComponent(RegistryKey.CAT_VARIANT));
        registry.register(DataComponentTypes.CAT_SOUND_VARIANT, registryComponent(RegistryKey.CAT_SOUND_VARIANT));
        registry.register(DataComponentTypes.FROG_VARIANT, registryComponent(RegistryKey.FROG_VARIANT));
        registry.register(DataComponentTypes.PIG_VARIANT, registryComponent(RegistryKey.PIG_VARIANT));
        registry.register(DataComponentTypes.PIG_SOUND_VARIANT, registryComponent(RegistryKey.PIG_SOUND_VARIANT));
        registry.register(DataComponentTypes.COW_VARIANT, registryComponent(RegistryKey.COW_VARIANT));
        registry.register(DataComponentTypes.COW_SOUND_VARIANT, registryComponent(RegistryKey.COW_SOUND_VARIANT));
        registry.register(DataComponentTypes.CHICKEN_VARIANT, registryComponent(RegistryKey.CHICKEN_VARIANT));
        registry.register(DataComponentTypes.CHICKEN_SOUND_VARIANT, registryComponent(RegistryKey.CHICKEN_SOUND_VARIANT));
        registry.register(DataComponentTypes.ZOMBIE_NAUTILUS_VARIANT, registryComponent(RegistryKey.ZOMBIE_NAUTILUS_VARIANT));
    }

    private static ComponentHandler flag() {
        return (stack, type, value, path, parser) -> {
            boolean enabled = value == null || ValueUtil.asBoolean(value, path);
            if (enabled) {
                stack.setData((DataComponentType.NonValued) type);
            } else {
                stack.unsetData(type);
            }
        };
    }

    private static ComponentHandler integer() {
        return (stack, type, value, path, parser) ->
                HandlerSupport.set(stack, type, ValueUtil.asInt(value, path));
    }

    private static ComponentHandler floatValue() {
        return (stack, type, value, path, parser) ->
                HandlerSupport.set(stack, type, ValueUtil.asFloat(value, path));
    }

    private static ComponentHandler booleanValue() {
        return (stack, type, value, path, parser) ->
                HandlerSupport.set(stack, type, ValueUtil.asBoolean(value, path));
    }

    private static ComponentHandler keyValue() {
        return (stack, type, value, path, parser) ->
                HandlerSupport.set(stack, type, ValueUtil.key(value, path));
    }

    private static ComponentHandler dyeColor() {
        return (stack, type, value, path, parser) ->
                HandlerSupport.set(stack, type, ValueUtil.enumValue(DyeColor.class, value, path));
    }

    private static <E extends Enum<E>> ComponentHandler enumComponent(Class<E> enumType) {
        return (stack, type, value, path, parser) ->
                HandlerSupport.set(stack, type, ValueUtil.enumValue(enumType, value, path));
    }

    private static <T extends org.bukkit.Keyed> ComponentHandler registryComponent(RegistryKey<T> registryKey) {
        return (stack, type, value, path, parser) ->
                HandlerSupport.set(stack, type, RegistryUtil.require(registryKey, value, path));
    }

    private static ComponentHandler enchantments() {
        return (stack, type, value, path, parser) -> {
            ItemEnchantments.Builder builder = ItemEnchantments.itemEnchantments();
            YamlNode node = HandlerSupport.asNodeOrNull(value, path);
            if (node != null) {
                for (String key : node.keys()) {
                    Enchantment enchantment = RegistryUtil.require(Registry.ENCHANTMENT, key, node.childPath(key));
                    builder.add(enchantment, ValueUtil.asInt(node.raw(key), node.childPath(key)));
                }
            } else if (value instanceof List<?> list) {
                for (int i = 0; i < list.size(); i++) {
                    YamlNode entry = HandlerSupport.asNode(list.get(i), path + "[" + i + "]");
                    Enchantment enchantment = RegistryUtil.require(Registry.ENCHANTMENT,
                            entry.contains("enchantment") ? entry.raw("enchantment") : entry.raw("id"),
                            entry.childPath(entry.contains("enchantment") ? "enchantment" : "id"));
                    builder.add(enchantment, ValueUtil.requireInt(entry, "level"));
                }
            } else {
                throw new YamlParseException(path, "expected enchantments map or list");
            }
            HandlerSupport.set(stack, type, builder.build());
        };
    }

    private static void lore(ItemStack stack, DataComponentType type, Object value, String path, me.usainsrht.itemapi.yamlitem.YamlItemParser parser) {
        List<Component> lines = HandlerSupport.components(value, path);
        HandlerSupport.set(stack, type, ItemLore.lore(lines));
    }

    private static void customModelData(ItemStack stack, DataComponentType type, Object value, String path, me.usainsrht.itemapi.yamlitem.YamlItemParser parser) {
        CustomModelData.Builder builder = CustomModelData.customModelData();
        if (value instanceof Number number) {
            builder.addFloat(number.floatValue());
            HandlerSupport.set(stack, type, builder.build());
            return;
        }
        YamlNode node = HandlerSupport.asNode(value, path);
        if (node.contains("floats")) {
            for (Object element : node.list("floats")) {
                builder.addFloat(ValueUtil.asFloat(element, node.childPath("floats")));
            }
        }
        if (node.contains("flags")) {
            for (Object element : node.list("flags")) {
                builder.addFlag(ValueUtil.asBoolean(element, node.childPath("flags")));
            }
        }
        if (node.contains("strings")) {
            for (Object element : node.list("strings")) {
                builder.addString(String.valueOf(element));
            }
        }
        if (node.contains("colors")) {
            for (Object element : node.list("colors")) {
                builder.addColor(ValueUtil.color(element, node.childPath("colors")));
            }
        }
        // shorthand: custom_model_data: {floats:[1], strings:["x"]}
        HandlerSupport.set(stack, type, builder.build());
    }

    private static void food(ItemStack stack, DataComponentType type, Object value, String path, me.usainsrht.itemapi.yamlitem.YamlItemParser parser) {
        YamlNode node = HandlerSupport.asNode(value, path);
        FoodProperties.Builder builder = FoodProperties.food()
                .nutrition(ValueUtil.intOr(node, "nutrition", 0))
                .saturation(ValueUtil.floatOr(node, "saturation", 0f))
                .canAlwaysEat(ValueUtil.boolOr(node, "can_always_eat", ValueUtil.boolOr(node, "canAlwaysEat", false)));
        HandlerSupport.set(stack, type, builder.build());
    }

    private static void consumable(ItemStack stack, DataComponentType type, Object value, String path, me.usainsrht.itemapi.yamlitem.YamlItemParser parser) {
        YamlNode node = HandlerSupport.asNode(value, path);
        Consumable.Builder builder = Consumable.consumable();
        if (node.contains("consume_seconds") || node.contains("consumeSeconds")) {
            builder.consumeSeconds(ValueUtil.floatOr(node, node.contains("consume_seconds") ? "consume_seconds" : "consumeSeconds", 1.6f));
        }
        if (node.contains("animation")) {
            builder.animation(ValueUtil.enumValue(node, "animation", ItemUseAnimation.class));
        }
        if (node.contains("sound")) {
            builder.sound(ValueUtil.key(node, "sound"));
        }
        if (node.contains("has_consume_particles") || node.contains("particles")) {
            String key = node.contains("has_consume_particles") ? "has_consume_particles" : "particles";
            builder.hasConsumeParticles(ValueUtil.boolOr(node, key, true));
        }
        if (node.contains("effects")) {
            builder.effects(HandlerSupport.consumeEffects(node.raw("effects"), node.childPath("effects")));
        }
        HandlerSupport.set(stack, type, builder.build());
    }

    private static void useCooldown(ItemStack stack, DataComponentType type, Object value, String path, me.usainsrht.itemapi.yamlitem.YamlItemParser parser) {
        if (value instanceof Number number) {
            HandlerSupport.set(stack, type, UseCooldown.useCooldown(number.floatValue()).build());
            return;
        }
        YamlNode node = HandlerSupport.asNode(value, path);
        float seconds = ValueUtil.floatOr(node, "seconds", ValueUtil.requireFloat(node, "value"));
        UseCooldown.Builder builder = UseCooldown.useCooldown(seconds);
        Key group = ValueUtil.keyOrNull(node, "cooldown_group");
        if (group == null) {
            group = ValueUtil.keyOrNull(node, "group");
        }
        if (group != null) {
            builder.cooldownGroup(group);
        }
        HandlerSupport.set(stack, type, builder.build());
    }

    private static void useEffects(ItemStack stack, DataComponentType type, Object value, String path, me.usainsrht.itemapi.yamlitem.YamlItemParser parser) {
        YamlNode node = HandlerSupport.asNode(value, path);
        UseEffects.Builder builder = UseEffects.useEffects()
                .canSprint(ValueUtil.boolOr(node, "can_sprint", true))
                .interactVibrations(ValueUtil.boolOr(node, "interact_vibrations", true))
                .speedMultiplier(ValueUtil.floatOr(node, "speed_multiplier", 1f));
        HandlerSupport.set(stack, type, builder.build());
    }

    private static void useRemainder(ItemStack stack, DataComponentType type, Object value, String path, me.usainsrht.itemapi.yamlitem.YamlItemParser parser) {
        ItemStack remainder = parser.parseNestedItem(value, path);
        HandlerSupport.set(stack, type, UseRemainder.useRemainder(remainder));
    }

    private static void weapon(ItemStack stack, DataComponentType type, Object value, String path, me.usainsrht.itemapi.yamlitem.YamlItemParser parser) {
        YamlNode node = HandlerSupport.asNode(value, path);
        Weapon.Builder builder = Weapon.weapon()
                .itemDamagePerAttack(ValueUtil.intOr(node, "item_damage_per_attack", ValueUtil.intOr(node, "damage_per_attack", 1)))
                .disableBlockingForSeconds(ValueUtil.floatOr(node, "disable_blocking_for_seconds", 0f));
        HandlerSupport.set(stack, type, builder.build());
    }

    private static void tool(ItemStack stack, DataComponentType type, Object value, String path, me.usainsrht.itemapi.yamlitem.YamlItemParser parser) {
        YamlNode node = HandlerSupport.asNode(value, path);
        Tool.Builder builder = Tool.tool()
                .defaultMiningSpeed(ValueUtil.floatOr(node, "default_mining_speed", 1f))
                .damagePerBlock(ValueUtil.intOr(node, "damage_per_block", 1))
                .canDestroyBlocksInCreative(ValueUtil.boolOr(node, "can_destroy_blocks_in_creative", true));
        if (node.contains("rules")) {
            List<?> rules = node.list("rules");
            for (int i = 0; i < rules.size(); i++) {
                YamlNode ruleNode = HandlerSupport.asNode(rules.get(i), node.childPath("rules") + "[" + i + "]");
                RegistryKeySet<org.bukkit.block.BlockType> blocks = RegistryUtil.keySet(RegistryKey.BLOCK, ruleNode, "blocks");
                Float speed = ValueUtil.boxedFloat(ruleNode, "speed");
                TriState correctForDrops = TriState.NOT_SET;
                if (ruleNode.contains("correct_for_drops")) {
                    correctForDrops = ValueUtil.asBoolean(ruleNode.raw("correct_for_drops"), ruleNode.childPath("correct_for_drops"))
                            ? TriState.TRUE : TriState.FALSE;
                }
                builder.addRule(Tool.rule(blocks, speed, correctForDrops));
            }
        }
        HandlerSupport.set(stack, type, builder.build());
    }

    private static void equippable(ItemStack stack, DataComponentType type, Object value, String path, me.usainsrht.itemapi.yamlitem.YamlItemParser parser) {
        YamlNode node = HandlerSupport.asNode(value, path);
        EquipmentSlot slot = ValueUtil.enumValue(node, "slot", EquipmentSlot.class);
        Equippable.Builder builder = Equippable.equippable(slot);
        Key equipSound = ValueUtil.keyOrNull(node, "equip_sound");
        if (equipSound != null) builder.equipSound(equipSound);
        Key assetId = ValueUtil.keyOrNull(node, "asset_id");
        if (assetId != null) builder.assetId(assetId);
        Key cameraOverlay = ValueUtil.keyOrNull(node, "camera_overlay");
        if (cameraOverlay != null) builder.cameraOverlay(cameraOverlay);
        if (node.contains("allowed_entities")) {
            builder.allowedEntities(RegistryUtil.keySet(RegistryKey.ENTITY_TYPE, node, "allowed_entities"));
        }
        builder.dispensable(ValueUtil.boolOr(node, "dispensable", true));
        builder.swappable(ValueUtil.boolOr(node, "swappable", true));
        builder.damageOnHurt(ValueUtil.boolOr(node, "damage_on_hurt", true));
        builder.equipOnInteract(ValueUtil.boolOr(node, "equip_on_interact", false));
        builder.canBeSheared(ValueUtil.boolOr(node, "can_be_sheared", false));
        Key shearSound = ValueUtil.keyOrNull(node, "shear_sound");
        if (shearSound != null) builder.shearSound(shearSound);
        HandlerSupport.set(stack, type, builder.build());
    }

    private static void repairable(ItemStack stack, DataComponentType type, Object value, String path, me.usainsrht.itemapi.yamlitem.YamlItemParser parser) {
        RegistryKeySet<ItemType> types = RegistryUtil.keySet(RegistryKey.ITEM, value, path);
        HandlerSupport.set(stack, type, Repairable.repairable(types));
    }

    private static void damageResistant(ItemStack stack, DataComponentType type, Object value, String path, me.usainsrht.itemapi.yamlitem.YamlItemParser parser) {
        Object raw = value;
        if (value instanceof Map || value instanceof org.bukkit.configuration.ConfigurationSection) {
            YamlNode node = HandlerSupport.asNode(value, path);
            raw = node.contains("types") ? node.raw("types") : node.raw("damage_types");
            path = node.contains("types") ? node.childPath("types") : node.childPath("damage_types");
        }
        HandlerSupport.set(stack, type, DamageResistant.damageResistant(RegistryUtil.keySet(RegistryKey.DAMAGE_TYPE, raw, path)));
    }

    private static void attributes(ItemStack stack, DataComponentType type, Object value, String path, me.usainsrht.itemapi.yamlitem.YamlItemParser parser) {
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.itemAttributes();
        List<?> list;
        if (value instanceof List<?> asList) {
            list = asList;
        } else {
            YamlNode node = HandlerSupport.asNode(value, path);
            if (node.contains("modifiers")) {
                list = node.list("modifiers");
                path = node.childPath("modifiers");
            } else {
                throw new YamlParseException(path, "expected attribute modifiers list");
            }
        }
        for (int i = 0; i < list.size(); i++) {
            YamlNode entry = HandlerSupport.asNode(list.get(i), path + "[" + i + "]");
            Attribute attribute = RegistryUtil.require(Registry.ATTRIBUTE,
                    entry.contains("attribute") ? entry.raw("attribute") : entry.raw("type"),
                    entry.childPath(entry.contains("attribute") ? "attribute" : "type"));
            AttributeModifier modifier = HandlerSupport.attributeModifier(entry);
            EquipmentSlotGroup group = modifier.getSlotGroup();
            builder.addModifier(attribute, modifier, group);
        }
        HandlerSupport.set(stack, type, builder.build());
    }

    private static void potionContents(ItemStack stack, DataComponentType type, Object value, String path, me.usainsrht.itemapi.yamlitem.YamlItemParser parser) {
        PotionContents.Builder builder = PotionContents.potionContents();
        if (value instanceof String) {
            builder.potion(RegistryUtil.require(Registry.POTION, value, path));
            HandlerSupport.set(stack, type, builder.build());
            return;
        }
        YamlNode node = HandlerSupport.asNode(value, path);
        if (node.contains("potion") || node.contains("type")) {
            String key = node.contains("potion") ? "potion" : "type";
            builder.potion(RegistryUtil.require(Registry.POTION, node.raw(key), node.childPath(key)));
        }
        if (node.contains("custom_color") || node.contains("color")) {
            String key = node.contains("custom_color") ? "custom_color" : "color";
            builder.customColor(ValueUtil.color(node, key));
        }
        if (node.contains("custom_name")) {
            builder.customName(ValueUtil.requireString(node, "custom_name"));
        }
        if (node.contains("custom_effects") || node.contains("effects")) {
            String key = node.contains("custom_effects") ? "custom_effects" : "effects";
            builder.addCustomEffects(HandlerSupport.potionEffects(node.raw(key), node.childPath(key)));
        }
        HandlerSupport.set(stack, type, builder.build());
    }

    private static void profile(ItemStack stack, DataComponentType type, Object value, String path, me.usainsrht.itemapi.yamlitem.YamlItemParser parser) {
        ResolvableProfile.Builder builder = ResolvableProfile.resolvableProfile();
        if (value instanceof String text) {
            try {
                builder.uuid(UUID.fromString(text));
            } catch (IllegalArgumentException ex) {
                builder.name(text);
            }
            HandlerSupport.set(stack, type, builder.build());
            return;
        }
        YamlNode node = HandlerSupport.asNode(value, path);
        if (node.contains("name")) {
            builder.name(ValueUtil.requireString(node, "name"));
        }
        if (node.contains("uuid") || node.contains("id")) {
            String key = node.contains("uuid") ? "uuid" : "id";
            builder.uuid(ValueUtil.uuid(node.raw(key), node.childPath(key)));
        }
        if (node.contains("properties")) {
            for (Object property : node.list("properties")) {
                YamlNode prop = HandlerSupport.asNode(property, node.childPath("properties"));
                String name = ValueUtil.requireString(prop, "name");
                String propValue = ValueUtil.requireString(prop, "value");
                String signature = ValueUtil.string(prop, "signature");
                if (signature != null) {
                    builder.addProperty(new com.destroystokyo.paper.profile.ProfileProperty(name, propValue, signature));
                } else {
                    builder.addProperty(new com.destroystokyo.paper.profile.ProfileProperty(name, propValue));
                }
            }
        }
        HandlerSupport.set(stack, type, builder.build());
    }

    private static void fireworks(ItemStack stack, DataComponentType type, Object value, String path, me.usainsrht.itemapi.yamlitem.YamlItemParser parser) {
        YamlNode node = HandlerSupport.asNode(value, path);
        Fireworks.Builder builder = Fireworks.fireworks()
                .flightDuration(ValueUtil.intOr(node, "flight_duration", ValueUtil.intOr(node, "flight", 1)));
        if (node.contains("explosions") || node.contains("effects")) {
            String key = node.contains("explosions") ? "explosions" : "effects";
            List<?> list = node.list(key);
            for (int i = 0; i < list.size(); i++) {
                builder.addEffect(HandlerSupport.fireworkEffect(list.get(i), node.childPath(key) + "[" + i + "]"));
            }
        }
        HandlerSupport.set(stack, type, builder.build());
    }

    private static void trim(ItemStack stack, DataComponentType type, Object value, String path, me.usainsrht.itemapi.yamlitem.YamlItemParser parser) {
        YamlNode node = HandlerSupport.asNode(value, path);
        TrimMaterial material = RegistryUtil.require(Registry.TRIM_MATERIAL,
                node.contains("material") ? node.raw("material") : node.raw("trim_material"),
                node.childPath(node.contains("material") ? "material" : "trim_material"));
        TrimPattern pattern = RegistryUtil.require(Registry.TRIM_PATTERN,
                node.contains("pattern") ? node.raw("pattern") : node.raw("trim_pattern"),
                node.childPath(node.contains("pattern") ? "pattern" : "trim_pattern"));
        HandlerSupport.set(stack, type, ItemArmorTrim.itemArmorTrim(new ArmorTrim(material, pattern)).build());
    }

    private static void jukebox(ItemStack stack, DataComponentType type, Object value, String path, me.usainsrht.itemapi.yamlitem.YamlItemParser parser) {
        Object songValue = value;
        if (value instanceof Map || value instanceof org.bukkit.configuration.ConfigurationSection) {
            YamlNode node = HandlerSupport.asNode(value, path);
            songValue = node.contains("song") ? node.raw("song") : node.raw("jukebox_song");
            path = node.contains("song") ? node.childPath("song") : node.childPath("jukebox_song");
        }
        JukeboxSong song = RegistryUtil.require(Registry.JUKEBOX_SONG, songValue, path);
        HandlerSupport.set(stack, type, JukeboxPlayable.jukeboxPlayable(song).build());
    }

    private static void tooltipDisplay(ItemStack stack, DataComponentType type, Object value, String path, me.usainsrht.itemapi.yamlitem.YamlItemParser parser) {
        YamlNode node = HandlerSupport.asNode(value, path);
        TooltipDisplay existing = stack.getData(DataComponentTypes.TOOLTIP_DISPLAY);
        boolean defaultHideTooltip = existing != null && existing.hideTooltip();
        TooltipDisplay.Builder builder = TooltipDisplay.tooltipDisplay()
                .hideTooltip(ValueUtil.boolOr(node, "hide_tooltip", defaultHideTooltip));
        if (node.contains("hidden_components")) {
            Set<DataComponentType> hidden = new HashSet<>();
            for (Object element : node.list("hidden_components")) {
                hidden.add(parser.handlers().requireType(String.valueOf(element), node.childPath("hidden_components")));
            }
            builder.hiddenComponents(hidden);
        } else if (existing != null) {
            builder.hiddenComponents(existing.hiddenComponents());
        }
        HandlerSupport.set(stack, type, builder.build());
    }

    private static void lodestone(ItemStack stack, DataComponentType type, Object value, String path, me.usainsrht.itemapi.yamlitem.YamlItemParser parser) {
        YamlNode node = HandlerSupport.asNode(value, path);
        LodestoneTracker.Builder builder = LodestoneTracker.lodestoneTracker()
                .tracked(ValueUtil.boolOr(node, "tracked", true));
        if (node.contains("location") || (node.contains("x") && node.contains("y") && node.contains("z"))) {
            Object loc = node.contains("location") ? node.raw("location") : node;
            String locPath = node.contains("location") ? node.childPath("location") : path;
            builder.location(HandlerSupport.location(loc, locPath));
        }
        HandlerSupport.set(stack, type, builder.build());
    }

    private static ComponentHandler nestedItems(java.util.function.Function<List<ItemStack>, Object> factory) {
        return (stack, type, value, path, parser) -> {
            List<ItemStack> items = new ArrayList<>();
            if (value instanceof List<?> list) {
                for (int i = 0; i < list.size(); i++) {
                    items.add(parser.parseNestedItem(list.get(i), path + "[" + i + "]"));
                }
            } else {
                items.add(parser.parseNestedItem(value, path));
            }
            HandlerSupport.set(stack, type, factory.apply(items));
        };
    }

    private static void containerLoot(ItemStack stack, DataComponentType type, Object value, String path, me.usainsrht.itemapi.yamlitem.YamlItemParser parser) {
        YamlNode node = HandlerSupport.asNode(value, path);
        Key lootTable = ValueUtil.key(node.contains("loot_table") ? node.raw("loot_table") : node.raw("table"),
                node.childPath(node.contains("loot_table") ? "loot_table" : "table"));
        long seed = ValueUtil.longOr(node, "seed", 0L);
        HandlerSupport.set(stack, type, SeededContainerLoot.seededContainerLoot(lootTable, seed));
    }

    private static void mapDecorations(ItemStack stack, DataComponentType type, Object value, String path, me.usainsrht.itemapi.yamlitem.YamlItemParser parser) {
        MapDecorations.Builder builder = MapDecorations.mapDecorations();
        YamlNode node = HandlerSupport.asNode(value, path);
        for (String key : node.keys()) {
            YamlNode entry = node.requireChild(key);
            MapCursor.Type cursorType = RegistryUtil.require(Registry.MAP_DECORATION_TYPE,
                    entry.contains("type") ? entry.raw("type") : entry.raw("decoration"),
                    entry.childPath(entry.contains("type") ? "type" : "decoration"));
            double x = ValueUtil.asDouble(entry.raw("x"), entry.childPath("x"));
            double z = ValueUtil.asDouble(entry.contains("z") ? entry.raw("z") : entry.raw("y"),
                    entry.childPath(entry.contains("z") ? "z" : "y"));
            float rotation = ValueUtil.floatOr(entry, "rotation", 0f);
            builder.put(key, MapDecorations.decorationEntry(cursorType, x, z, rotation));
        }
        HandlerSupport.set(stack, type, builder.build());
    }

    private static void mapColor(ItemStack stack, DataComponentType type, Object value, String path, me.usainsrht.itemapi.yamlitem.YamlItemParser parser) {
        org.bukkit.Color color;
        if (value instanceof Map || value instanceof org.bukkit.configuration.ConfigurationSection) {
            color = ValueUtil.color(HandlerSupport.asNode(value, path), "color");
        } else {
            color = ValueUtil.color(value, path);
        }
        HandlerSupport.set(stack, type, MapItemColor.mapItemColor().color(color).build());
    }

    private static void dyedColor(ItemStack stack, DataComponentType type, Object value, String path, me.usainsrht.itemapi.yamlitem.YamlItemParser parser) {
        org.bukkit.Color color;
        if (value instanceof Map || value instanceof org.bukkit.configuration.ConfigurationSection) {
            YamlNode node = HandlerSupport.asNode(value, path);
            color = ValueUtil.color(node.contains("rgb") ? node.raw("rgb") : node.raw("color"),
                    node.childPath(node.contains("rgb") ? "rgb" : "color"));
        } else {
            color = ValueUtil.color(value, path);
        }
        HandlerSupport.set(stack, type, DyedItemColor.dyedItemColor().color(color).build());
    }

    private static void suspiciousStew(ItemStack stack, DataComponentType type, Object value, String path, me.usainsrht.itemapi.yamlitem.YamlItemParser parser) {
        SuspiciousStewEffects.Builder builder = SuspiciousStewEffects.suspiciousStewEffects();
        List<?> list = value instanceof List<?> asList ? asList : List.of(value);
        for (int i = 0; i < list.size(); i++) {
            YamlNode entry = HandlerSupport.asNode(list.get(i), path + "[" + i + "]");
            var effect = RegistryUtil.require(RegistryKey.MOB_EFFECT,
                    entry.contains("effect") ? entry.raw("effect") : entry.raw("type"),
                    entry.childPath(entry.contains("effect") ? "effect" : "type"));
            int duration = ValueUtil.intOr(entry, "duration", 160);
            builder.add(SuspiciousEffectEntry.create(effect, duration));
        }
        HandlerSupport.set(stack, type, builder.build());
    }

    private static void writableBook(ItemStack stack, DataComponentType type, Object value, String path, me.usainsrht.itemapi.yamlitem.YamlItemParser parser) {
        WritableBookContent.Builder builder = WritableBookContent.writeableBookContent();
        List<?> pages;
        if (value instanceof List<?> list) {
            pages = list;
        } else {
            YamlNode node = HandlerSupport.asNode(value, path);
            pages = node.list("pages");
        }
        for (Object page : pages) {
            builder.addPage(String.valueOf(page));
        }
        HandlerSupport.set(stack, type, builder.build());
    }

    private static void writtenBook(ItemStack stack, DataComponentType type, Object value, String path, me.usainsrht.itemapi.yamlitem.YamlItemParser parser) {
        YamlNode node = HandlerSupport.asNode(value, path);
        String title = ValueUtil.requireString(node, "title");
        String author = ValueUtil.requireString(node, "author");
        WrittenBookContent.Builder builder = WrittenBookContent.writtenBookContent(title, author)
                .generation(ValueUtil.intOr(node, "generation", 0))
                .resolved(ValueUtil.boolOr(node, "resolved", false));
        if (node.contains("pages")) {
            for (Object page : node.list("pages")) {
                builder.addPage(HandlerSupport.component(page, node.childPath("pages")));
            }
        }
        HandlerSupport.set(stack, type, builder.build());
    }

    private static void bannerPatterns(ItemStack stack, DataComponentType type, Object value, String path, me.usainsrht.itemapi.yamlitem.YamlItemParser parser) {
        BannerPatternLayers.Builder builder = BannerPatternLayers.bannerPatternLayers();
        List<?> list = value instanceof List<?> asList ? asList : HandlerSupport.asNode(value, path).list("patterns");
        for (int i = 0; i < list.size(); i++) {
            YamlNode entry = HandlerSupport.asNode(list.get(i), path + "[" + i + "]");
            PatternType patternType = RegistryUtil.require(Registry.BANNER_PATTERN,
                    entry.contains("pattern") ? entry.raw("pattern") : entry.raw("type"),
                    entry.childPath(entry.contains("pattern") ? "pattern" : "type"));
            DyeColor color = ValueUtil.enumValue(entry, "color", DyeColor.class);
            builder.add(new Pattern(color, patternType));
        }
        HandlerSupport.set(stack, type, builder.build());
    }

    private static void potDecorations(ItemStack stack, DataComponentType type, Object value, String path, me.usainsrht.itemapi.yamlitem.YamlItemParser parser) {
        YamlNode node = HandlerSupport.asNode(value, path);
        PotDecorations.Builder builder = PotDecorations.potDecorations();
        if (node.contains("back")) builder.back(RegistryUtil.require(RegistryKey.ITEM, node.raw("back"), node.childPath("back")));
        if (node.contains("left")) builder.left(RegistryUtil.require(RegistryKey.ITEM, node.raw("left"), node.childPath("left")));
        if (node.contains("right")) builder.right(RegistryUtil.require(RegistryKey.ITEM, node.raw("right"), node.childPath("right")));
        if (node.contains("front")) builder.front(RegistryUtil.require(RegistryKey.ITEM, node.raw("front"), node.childPath("front")));
        HandlerSupport.set(stack, type, builder.build());
    }

    private static ComponentHandler adventurePredicate() {
        return (stack, type, value, path, parser) -> {
            ItemAdventurePredicate.Builder builder = ItemAdventurePredicate.itemAdventurePredicate();
            List<?> list;
            if (value instanceof List<?> asList) {
                list = asList;
            } else {
                YamlNode node = HandlerSupport.asNode(value, path);
                list = node.contains("predicates") ? node.list("predicates") : node.contains("blocks")
                        ? List.of(node) : List.of(value);
                if (node.contains("predicates")) {
                    path = node.childPath("predicates");
                }
            }
            for (int i = 0; i < list.size(); i++) {
                Object element = list.get(i);
                String elementPath = path + "[" + i + "]";
                if (element instanceof String || (element instanceof List<?>)) {
                    builder.addPredicate(BlockPredicate.predicate()
                            .blocks(RegistryUtil.keySet(RegistryKey.BLOCK, element, elementPath))
                            .build());
                } else {
                    YamlNode entry = HandlerSupport.asNode(element, elementPath);
                    builder.addPredicate(BlockPredicate.predicate()
                            .blocks(RegistryUtil.keySet(RegistryKey.BLOCK, entry, "blocks"))
                            .build());
                }
            }
            HandlerSupport.set(stack, type, builder.build());
        };
    }

    private static void deathProtection(ItemStack stack, DataComponentType type, Object value, String path, me.usainsrht.itemapi.yamlitem.YamlItemParser parser) {
        Object effects = value;
        if (value instanceof Map || value instanceof org.bukkit.configuration.ConfigurationSection) {
            YamlNode node = HandlerSupport.asNode(value, path);
            effects = node.contains("effects") ? node.raw("effects") : node.raw("death_effects");
            path = node.contains("effects") ? node.childPath("effects") : node.childPath("death_effects");
        }
        HandlerSupport.set(stack, type, DeathProtection.deathProtection(HandlerSupport.consumeEffects(effects, path)));
    }

    private static void blocksAttacks(ItemStack stack, DataComponentType type, Object value, String path, me.usainsrht.itemapi.yamlitem.YamlItemParser parser) {
        YamlNode node = HandlerSupport.asNode(value, path);
        BlocksAttacks.Builder builder = BlocksAttacks.blocksAttacks()
                .blockDelaySeconds(ValueUtil.floatOr(node, "block_delay_seconds", 0f))
                .disableCooldownScale(ValueUtil.floatOr(node, "disable_cooldown_scale", 1f));
        if (node.contains("bypassed_by")) {
            builder.bypassedBy(RegistryUtil.keySet(RegistryKey.DAMAGE_TYPE, node, "bypassed_by"));
        }
        Key blockSound = ValueUtil.keyOrNull(node, "block_sound");
        if (blockSound != null) builder.blockSound(blockSound);
        Key disableSound = ValueUtil.keyOrNull(node, "disable_sound");
        if (disableSound != null) builder.disableSound(disableSound);
        if (node.contains("item_damage")) {
            YamlNode damageNode = node.requireChild("item_damage");
            builder.itemDamage(ItemDamageFunction.itemDamageFunction()
                    .threshold(ValueUtil.floatOr(damageNode, "threshold", 0f))
                    .base(ValueUtil.floatOr(damageNode, "base", 0f))
                    .factor(ValueUtil.floatOr(damageNode, "factor", 1f))
                    .build());
        }
        if (node.contains("damage_reductions")) {
            List<?> list = node.list("damage_reductions");
            for (int i = 0; i < list.size(); i++) {
                YamlNode reduction = HandlerSupport.asNode(list.get(i), node.childPath("damage_reductions") + "[" + i + "]");
                DamageReduction.Builder reductionBuilder = DamageReduction.damageReduction()
                        .horizontalBlockingAngle(ValueUtil.floatOr(reduction, "horizontal_blocking_angle", 90f))
                        .base(ValueUtil.floatOr(reduction, "base", 0f))
                        .factor(ValueUtil.floatOr(reduction, "factor", 1f));
                if (reduction.contains("type") || reduction.contains("types")) {
                    String key = reduction.contains("type") ? "type" : "types";
                    reductionBuilder.type(RegistryUtil.keySet(RegistryKey.DAMAGE_TYPE, reduction, key));
                }
                builder.addDamageReduction(reductionBuilder.build());
            }
        }
        HandlerSupport.set(stack, type, builder.build());
    }

    private static void attackRange(ItemStack stack, DataComponentType type, Object value, String path, me.usainsrht.itemapi.yamlitem.YamlItemParser parser) {
        YamlNode node = HandlerSupport.asNode(value, path);
        AttackRange.Builder builder = AttackRange.attackRange()
                .minReach(ValueUtil.floatOr(node, "min_reach", 0f))
                .maxReach(ValueUtil.floatOr(node, "max_reach", 3f))
                .minCreativeReach(ValueUtil.floatOr(node, "min_creative_reach", 0f))
                .maxCreativeReach(ValueUtil.floatOr(node, "max_creative_reach", 5f))
                .hitboxMargin(ValueUtil.floatOr(node, "hitbox_margin", 0f))
                .mobFactor(ValueUtil.floatOr(node, "mob_factor", 1f));
        HandlerSupport.set(stack, type, builder.build());
    }

    private static void piercingWeapon(ItemStack stack, DataComponentType type, Object value, String path, me.usainsrht.itemapi.yamlitem.YamlItemParser parser) {
        YamlNode node = HandlerSupport.asNode(value, path);
        PiercingWeapon.Builder builder = PiercingWeapon.piercingWeapon()
                .dealsKnockback(ValueUtil.boolOr(node, "deals_knockback", true))
                .dismounts(ValueUtil.boolOr(node, "dismounts", false));
        Key sound = ValueUtil.keyOrNull(node, "sound");
        if (sound != null) builder.sound(sound);
        Key hitSound = ValueUtil.keyOrNull(node, "hit_sound");
        if (hitSound != null) builder.hitSound(hitSound);
        HandlerSupport.set(stack, type, builder.build());
    }

    private static void kineticWeapon(ItemStack stack, DataComponentType type, Object value, String path, me.usainsrht.itemapi.yamlitem.YamlItemParser parser) {
        YamlNode node = HandlerSupport.asNode(value, path);
        KineticWeapon.Builder builder = KineticWeapon.kineticWeapon()
                .contactCooldownTicks(ValueUtil.intOr(node, "contact_cooldown_ticks", 0))
                .delayTicks(ValueUtil.intOr(node, "delay_ticks", 0))
                .forwardMovement(ValueUtil.floatOr(node, "forward_movement", 0f))
                .damageMultiplier(ValueUtil.floatOr(node, "damage_multiplier", 1f));
        if (node.contains("dismount_conditions")) {
            builder.dismountConditions(condition(node.requireChild("dismount_conditions")));
        }
        if (node.contains("knockback_conditions")) {
            builder.knockbackConditions(condition(node.requireChild("knockback_conditions")));
        }
        if (node.contains("damage_conditions")) {
            builder.damageConditions(condition(node.requireChild("damage_conditions")));
        }
        Key sound = ValueUtil.keyOrNull(node, "sound");
        if (sound != null) builder.sound(sound);
        Key hitSound = ValueUtil.keyOrNull(node, "hit_sound");
        if (hitSound != null) builder.hitSound(hitSound);
        HandlerSupport.set(stack, type, builder.build());
    }

    private static KineticWeapon.Condition condition(YamlNode node) {
        return KineticWeapon.condition(
                ValueUtil.intOr(node, "max_duration_ticks", 0),
                ValueUtil.floatOr(node, "min_speed", 0f),
                ValueUtil.floatOr(node, "min_relative_speed", 0f)
        );
    }

    private static void swingAnimation(ItemStack stack, DataComponentType type, Object value, String path, me.usainsrht.itemapi.yamlitem.YamlItemParser parser) {
        SwingAnimation.Builder builder = SwingAnimation.swingAnimation();
        if (value instanceof String) {
            builder.type(ValueUtil.enumValue(SwingAnimation.Animation.class, value, path));
        } else {
            YamlNode node = HandlerSupport.asNode(value, path);
            if (node.contains("type")) {
                builder.type(ValueUtil.enumValue(node, "type", SwingAnimation.Animation.class));
            }
            builder.duration(ValueUtil.intOr(node, "duration", 6));
        }
        HandlerSupport.set(stack, type, builder.build());
    }

    private static void recipes(ItemStack stack, DataComponentType type, Object value, String path, me.usainsrht.itemapi.yamlitem.YamlItemParser parser) {
        List<Key> keys = new ArrayList<>();
        if (value instanceof List<?> list) {
            for (int i = 0; i < list.size(); i++) {
                keys.add(ValueUtil.key(list.get(i), path + "[" + i + "]"));
            }
        } else {
            keys.add(ValueUtil.key(value, path));
        }
        HandlerSupport.set(stack, type, keys);
    }
}
