package me.usainsrht.itemapi.yamlitem.handler;

import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.AttackRange;
import io.papermc.paper.datacomponent.item.BlocksAttacks;
import io.papermc.paper.datacomponent.item.KineticWeapon;
import io.papermc.paper.datacomponent.item.MobVisibility;
import io.papermc.paper.datacomponent.item.PiercingWeapon;
import io.papermc.paper.datacomponent.item.SignText;
import io.papermc.paper.datacomponent.item.SulfurCubeContent;
import io.papermc.paper.datacomponent.item.SwingAnimation;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import io.papermc.paper.datacomponent.item.UseEffects;
import io.papermc.paper.datacomponent.item.VillagerFood;
import io.papermc.paper.datacomponent.item.Weapon;
import io.papermc.paper.datacomponent.item.blocksattacks.DamageReduction;
import io.papermc.paper.datacomponent.item.blocksattacks.ItemDamageFunction;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
import me.usainsrht.itemapi.yamlitem.YamlItemParser;
import me.usainsrht.itemapi.yamlitem.internal.RegistryUtil;
import me.usainsrht.itemapi.yamlitem.internal.ValueUtil;
import me.usainsrht.itemapi.yamlitem.internal.YamlNode;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.DyeColor;
import org.bukkit.Registry;
import org.bukkit.entity.Axolotl;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.EntityType;
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
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Isolated handlers for features and components present in Paper 26.x.
 * <p>
 * This class is loaded lazily only on servers where 26.x components are present in
 * the runtime registry, ensuring Paper 1.21.4 servers never encounter class linkage errors.
 */
public final class Modern26Handlers {

    private Modern26Handlers() {
    }

    public static void registerAll(ComponentHandlerRegistry registry) {
        registerIfPresent(registry, "blocks_attacks", Modern26Handlers::blocksAttacks);
        registerIfPresent(registry, "attack_range", Modern26Handlers::attackRange);
        registerIfPresent(registry, "piercing_weapon", Modern26Handlers::piercingWeapon);
        registerIfPresent(registry, "kinetic_weapon", Modern26Handlers::kineticWeapon);
        registerIfPresent(registry, "attack_animation", Modern26Handlers::swingAnimation);
        registerIfPresent(registry, "interact_animation", Modern26Handlers::swingAnimation);
        registerIfPresent(registry, "swing_animation", Modern26Handlers::swingAnimation);
        registerIfPresent(registry, "weapon", Modern26Handlers::weapon);
        registerIfPresent(registry, "use_effects", Modern26Handlers::useEffects);
        registerIfPresent(registry, "tooltip_display", Modern26Handlers::tooltipDisplay);
        registerIfPresent(registry, "sulfur_cube_content", (stack, type, value, path, parser) ->
                HandlerSupport.set(stack, type, SulfurCubeContent.sulfurCubeContent(parser.parseNestedItem(value, path))));

        registerIfPresent(registry, "mob_visibility", Modern26Handlers::mobVisibility);
        registerIfPresent(registry, "villager_food", Modern26Handlers::villagerFood);
        registerIfPresent(registry, "sign_text_front", Modern26Handlers::signText);
        registerIfPresent(registry, "sign_text_back", Modern26Handlers::signText);
        registerIfPresent(registry, "waxed", ComponentHandlers.flag());
        registerIfPresent(registry, "cushion_color", ComponentHandlers.dyeColor());
        registerIfPresent(registry, "provides_pottery_pattern", (stack, type, value, path, parser) ->
                HandlerSupport.set(stack, type, RegistryUtil.require(RegistryKey.DECORATED_POT_PATTERN, value, path)));

        registerIfPresent(registry, "minimum_attack_charge", ComponentHandlers.floatValue());
        registerIfPresent(registry, "potion_duration_scale", ComponentHandlers.floatValue());
        registerIfPresent(registry, "damage_type", (stack, type, value, path, parser) ->
                HandlerSupport.set(stack, type, RegistryUtil.require(RegistryKey.DAMAGE_TYPE, value, path)));
        registerIfPresent(registry, "provides_trim_material", (stack, type, value, path, parser) ->
                HandlerSupport.set(stack, type, RegistryUtil.require(Registry.TRIM_MATERIAL, value, path)));
        registerIfPresent(registry, "provides_banner_patterns", (stack, type, value, path, parser) ->
                HandlerSupport.set(stack, type, RegistryUtil.keySet(RegistryKey.BANNER_PATTERN, value, path)));
        registerIfPresent(registry, "break_sound", ComponentHandlers.keyValue());
        registerIfPresent(registry, "dye", ComponentHandlers.dyeColor());

        registerEntityVariants(registry);
    }

    public static void applyUnbreakable(ItemStack stack, boolean enabled, boolean showInTooltip) {
        if (stack == null) {
            return;
        }
        try {
            if (enabled) {
                stack.setData(DataComponentTypes.UNBREAKABLE);
                if (!showInTooltip) {
                    TooltipDisplay existing = stack.getData(DataComponentTypes.TOOLTIP_DISPLAY);
                    Set<DataComponentType> hidden = new HashSet<>(existing != null ? existing.hiddenComponents() : Set.of());
                    hidden.add(DataComponentTypes.UNBREAKABLE);
                    TooltipDisplay.Builder builder = TooltipDisplay.tooltipDisplay()
                            .hiddenComponents(hidden);
                    if (existing != null) {
                        builder.hideTooltip(existing.hideTooltip());
                    }
                    stack.setData(DataComponentTypes.TOOLTIP_DISPLAY, builder.build());
                } else {
                    TooltipDisplay existing = stack.getData(DataComponentTypes.TOOLTIP_DISPLAY);
                    if (existing != null && existing.hiddenComponents().contains(DataComponentTypes.UNBREAKABLE)) {
                        Set<DataComponentType> hidden = new HashSet<>(existing.hiddenComponents());
                        hidden.remove(DataComponentTypes.UNBREAKABLE);
                        TooltipDisplay.Builder builder = TooltipDisplay.tooltipDisplay()
                                .hiddenComponents(hidden)
                                .hideTooltip(existing.hideTooltip());
                        stack.setData(DataComponentTypes.TOOLTIP_DISPLAY, builder.build());
                    }
                }
            } else {
                stack.unsetData(DataComponentTypes.UNBREAKABLE);
            }
        } catch (Throwable t) {
            me.usainsrht.itemapi.yamlitem.internal.ItemLogger.warn("Failed to apply 26.x unbreakable: " + t.getMessage());
        }
    }

    public static void applyHideTooltip(ItemStack stack, boolean hide) {
        if (stack == null) {
            return;
        }
        try {
            TooltipDisplay existing = stack.getData(DataComponentTypes.TOOLTIP_DISPLAY);
            TooltipDisplay.Builder builder = TooltipDisplay.tooltipDisplay()
                    .hideTooltip(hide);
            if (existing != null) {
                builder.hiddenComponents(existing.hiddenComponents());
            }
            stack.setData(DataComponentTypes.TOOLTIP_DISPLAY, builder.build());
        } catch (Throwable t) {
            me.usainsrht.itemapi.yamlitem.internal.ItemLogger.warn("Failed to apply 26.x hide tooltip: " + t.getMessage());
        }
    }

    private static void registerEntityVariants(ComponentHandlerRegistry registry) {
        registerIfPresent(registry, "fox_variant", ComponentHandlers.enumComponent(Fox.Type.class));
        registerIfPresent(registry, "salmon_size", ComponentHandlers.enumComponent(Salmon.Variant.class));
        registerIfPresent(registry, "parrot_variant", ComponentHandlers.enumComponent(Parrot.Variant.class));
        registerIfPresent(registry, "tropical_fish_pattern", ComponentHandlers.enumComponent(TropicalFish.Pattern.class));
        registerIfPresent(registry, "mooshroom_variant", ComponentHandlers.enumComponent(MushroomCow.Variant.class));
        registerIfPresent(registry, "rabbit_variant", ComponentHandlers.enumComponent(Rabbit.Type.class));
        registerIfPresent(registry, "horse_variant", ComponentHandlers.enumComponent(Horse.Color.class));
        registerIfPresent(registry, "llama_variant", ComponentHandlers.enumComponent(Llama.Color.class));
        registerIfPresent(registry, "axolotl_variant", ComponentHandlers.enumComponent(Axolotl.Variant.class));

        registerIfPresent(registry, "cat_collar", ComponentHandlers.dyeColor());
        registerIfPresent(registry, "wolf_collar", ComponentHandlers.dyeColor());
        registerIfPresent(registry, "sheep_color", ComponentHandlers.dyeColor());
        registerIfPresent(registry, "shulker_color", ComponentHandlers.dyeColor());
        registerIfPresent(registry, "tropical_fish_base_color", ComponentHandlers.dyeColor());
        registerIfPresent(registry, "tropical_fish_pattern_color", ComponentHandlers.dyeColor());

        registerIfPresent(registry, "villager_variant", ComponentHandlers.registryComponent(RegistryKey.VILLAGER_TYPE));
        registerIfPresent(registry, "wolf_variant", ComponentHandlers.registryComponent(RegistryKey.WOLF_VARIANT));
        registerIfPresent(registry, "cat_variant", ComponentHandlers.registryComponent(RegistryKey.CAT_VARIANT));
        registerIfPresent(registry, "frog_variant", ComponentHandlers.registryComponent(RegistryKey.FROG_VARIANT));
        registerIfPresent(registry, "pig_variant", ComponentHandlers.registryComponent(RegistryKey.PIG_VARIANT));
        registerIfPresent(registry, "cow_variant", ComponentHandlers.registryComponent(RegistryKey.COW_VARIANT));
        registerIfPresent(registry, "chicken_variant", ComponentHandlers.registryComponent(RegistryKey.CHICKEN_VARIANT));

        registerSoundVariants(registry);
    }

    private static void registerSoundVariants(ComponentHandlerRegistry registry) {
        registerIfPresent(registry, "wolf_sound_variant", ComponentHandlers.registryComponent(RegistryKey.WOLF_SOUND_VARIANT));
        registerIfPresent(registry, "cat_sound_variant", ComponentHandlers.registryComponent(RegistryKey.CAT_SOUND_VARIANT));
        registerIfPresent(registry, "pig_sound_variant", ComponentHandlers.registryComponent(RegistryKey.PIG_SOUND_VARIANT));
        registerIfPresent(registry, "cow_sound_variant", ComponentHandlers.registryComponent(RegistryKey.COW_SOUND_VARIANT));
        registerIfPresent(registry, "chicken_sound_variant", ComponentHandlers.registryComponent(RegistryKey.CHICKEN_SOUND_VARIANT));
        registerIfPresent(registry, "zombie_nautilus_variant", ComponentHandlers.registryComponent(RegistryKey.ZOMBIE_NAUTILUS_VARIANT));
    }

    private static void registerIfPresent(ComponentHandlerRegistry registry, String id, ComponentHandler handler) {
        try {
            DataComponentType type = registry.resolveType(id);
            if (type != null) {
                registry.register(type, handler);
            }
        } catch (Throwable t) {
            me.usainsrht.itemapi.yamlitem.internal.ItemLogger.warn("Failed to register 26.x component '" + id + "': " + t.getMessage());
        }
    }

    private static void blocksAttacks(ItemStack stack, DataComponentType type, Object value, String path, YamlItemParser parser) {
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

    private static void attackRange(ItemStack stack, DataComponentType type, Object value, String path, YamlItemParser parser) {
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

    private static void piercingWeapon(ItemStack stack, DataComponentType type, Object value, String path, YamlItemParser parser) {
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

    private static void kineticWeapon(ItemStack stack, DataComponentType type, Object value, String path, YamlItemParser parser) {
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

    private static void swingAnimation(ItemStack stack, DataComponentType type, Object value, String path, YamlItemParser parser) {
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

    private static void weapon(ItemStack stack, DataComponentType type, Object value, String path, YamlItemParser parser) {
        YamlNode node = HandlerSupport.asNode(value, path);
        Weapon.Builder builder = Weapon.weapon()
                .itemDamagePerAttack(ValueUtil.intOr(node, "item_damage_per_attack", ValueUtil.intOr(node, "damage_per_attack", 1)))
                .disableBlockingForSeconds(ValueUtil.floatOr(node, "disable_blocking_for_seconds", 0f));
        HandlerSupport.set(stack, type, builder.build());
    }

    private static void useEffects(ItemStack stack, DataComponentType type, Object value, String path, YamlItemParser parser) {
        YamlNode node = HandlerSupport.asNode(value, path);
        UseEffects.Builder builder = UseEffects.useEffects()
                .canSprint(ValueUtil.boolOr(node, "can_sprint", true))
                .interactVibrations(ValueUtil.boolOr(node, "interact_vibrations", true))
                .speedMultiplier(ValueUtil.floatOr(node, "speed_multiplier", 1f));
        HandlerSupport.set(stack, type, builder.build());
    }

    private static void tooltipDisplay(ItemStack stack, DataComponentType type, Object value, String path, YamlItemParser parser) {
        YamlNode node = HandlerSupport.asNode(value, path);
        TooltipDisplay existing = stack.getData(DataComponentTypes.TOOLTIP_DISPLAY);
        boolean defaultHideTooltip = existing != null && existing.hideTooltip();
        TooltipDisplay.Builder builder = TooltipDisplay.tooltipDisplay()
                .hideTooltip(ValueUtil.boolOr(node, "hide_tooltip", defaultHideTooltip));
        if (node.contains("hidden_components")) {
            Set<DataComponentType> hidden = new HashSet<>();
            for (Object element : node.list("hidden_components")) {
                DataComponentType compType = parser.handlers().resolveType(String.valueOf(element));
                if (compType != null) {
                    hidden.add(compType);
                } else {
                    me.usainsrht.itemapi.yamlitem.internal.ItemLogger.warn("Unknown or unsupported data component '" + element + "' in hidden_components at path '" + node.childPath("hidden_components") + "'. Skipping.");
                }
            }
            builder.hiddenComponents(hidden);
        } else if (existing != null) {
            builder.hiddenComponents(existing.hiddenComponents());
        }
        HandlerSupport.set(stack, type, builder.build());
    }

    private static void mobVisibility(ItemStack stack, DataComponentType type, Object value, String path, YamlItemParser parser) {
        if (value instanceof Number num) {
            HandlerSupport.set(stack, type, MobVisibility.mobVisibility(
                    RegistrySet.keySet(RegistryKey.ENTITY_TYPE, List.of()),
                    num.floatValue()
            ));
            return;
        }
        YamlNode node = HandlerSupport.asNode(value, path);
        float visibility = ValueUtil.floatOr(node, "visibility", 1f);
        RegistryKeySet<EntityType> targeting;
        if (node.contains("targeting_entity_types")) {
            targeting = RegistryUtil.keySet(RegistryKey.ENTITY_TYPE, node, "targeting_entity_types");
        } else if (node.contains("targeting_entity")) {
            targeting = RegistryUtil.keySet(RegistryKey.ENTITY_TYPE, node, "targeting_entity");
        } else if (node.contains("types")) {
            targeting = RegistryUtil.keySet(RegistryKey.ENTITY_TYPE, node, "types");
        } else {
            targeting = RegistrySet.keySet(RegistryKey.ENTITY_TYPE, List.of());
        }
        HandlerSupport.set(stack, type, MobVisibility.mobVisibility(targeting, visibility));
    }

    private static void villagerFood(ItemStack stack, DataComponentType type, Object value, String path, YamlItemParser parser) {
        int nutrition;
        if (value instanceof Number num) {
            nutrition = num.intValue();
        } else {
            YamlNode node = HandlerSupport.asNode(value, path);
            nutrition = ValueUtil.intOr(node, "nutrition", 1);
        }
        HandlerSupport.set(stack, type, VillagerFood.villagerFood(nutrition));
    }

    private static void signText(ItemStack stack, DataComponentType type, Object value, String path, YamlItemParser parser) {
        SignText.Builder builder = SignText.signText();
        if (value instanceof List<?> list) {
            List<Component> lines = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                lines.add(HandlerSupport.component(list.get(i), path + "[" + i + "]", parser));
            }
            builder.lines(lines);
        } else if (value instanceof String || value instanceof Number || value instanceof Boolean) {
            builder.lines(List.of(HandlerSupport.component(value, path, parser)));
        } else {
            YamlNode node = HandlerSupport.asNode(value, path);
            if (node.contains("lines")) {
                List<Component> lines = new ArrayList<>();
                List<?> list = node.list("lines");
                for (int i = 0; i < list.size(); i++) {
                    lines.add(HandlerSupport.component(list.get(i), node.childPath("lines") + "[" + i + "]", parser));
                }
                builder.lines(lines);
            } else if (node.contains("text")) {
                builder.lines(List.of(HandlerSupport.component(node.raw("text"), node.childPath("text"), parser)));
            }
            if (node.contains("color")) {
                builder.color(ValueUtil.enumValue(node, "color", DyeColor.class));
            }
            if (node.contains("has_glowing_text") || node.contains("glowing")) {
                String glowingKey = node.contains("has_glowing_text") ? "has_glowing_text" : "glowing";
                builder.hasGlowingText(ValueUtil.boolOr(node, glowingKey, true));
            }
        }
        HandlerSupport.set(stack, type, builder.build());
    }
}
