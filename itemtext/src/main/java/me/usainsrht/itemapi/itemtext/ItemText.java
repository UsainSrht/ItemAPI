package me.usainsrht.itemapi.itemtext;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.MapDecorations;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.ShadowColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.UnaryOperator;

/**
 * Formats {@link ItemStack}s as Adventure {@link Component}s.
 */
public final class ItemText {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static volatile ItemTextOptions defaultOptions = ItemTextOptions.defaults();

    private ItemText() {
    }

    public static ItemTextOptions defaultOptions() {
        return defaultOptions;
    }

    public static void setDefaultOptions(ItemTextOptions options) {
        defaultOptions = Objects.requireNonNull(options, "options");
    }

    public static void setDefaultOptions(UnaryOperator<ItemTextOptions.Builder> configurator) {
        Objects.requireNonNull(configurator, "configurator");
        defaultOptions = configurator.apply(defaultOptions.toBuilder()).build();
    }

    /**
     * Sets whether container items should be displayed as a virtual bundle by default.
     */
    public static void setContainerShowAsBundle(boolean enabled) {
        setDefaultOptions(builder -> builder.containerShowAsBundle(enabled));
    }

    /**
     * Sets whether map decorations should be stripped from hover tooltips by default.
     */
    public static void setStripMapDecorations(boolean enabled) {
        setDefaultOptions(builder -> builder.stripMapDecorations(enabled));
    }

    /**
     * Sets global default container lore options.
     */
    public static void setContentLore(ContentLoreOptions options) {
        setDefaultOptions(builder -> builder.contentLore(options));
    }

    /**
     * Modifies global default container lore options using a builder configurator.
     */
    public static void setContentLore(UnaryOperator<ContentLoreOptions.Builder> configurator) {
        setDefaultOptions(builder -> builder.contentLore(configurator));
    }

    public static Component format(ItemStack item) {
        return format(item, defaultOptions);
    }

    public static Component format(ItemStack item, int amount) {
        return format(item, defaultOptions.toBuilder().amount(amount).build());
    }

    public static Component format(ItemStack item, int amount, ItemTextOptions options) {
        Objects.requireNonNull(options, "options");
        return format(item, options.toBuilder().amount(amount).build());
    }

    public static Component format(ItemStack item, int amount, UnaryOperator<ItemTextOptions.Builder> configurator) {
        Objects.requireNonNull(configurator, "configurator");
        return format(item, configurator.apply(defaultOptions.toBuilder()).amount(amount).build());
    }

    public static Component format(ItemStack item, ItemTextOptions options) {
        Objects.requireNonNull(item, "item");
        Objects.requireNonNull(options, "options");

        int effectiveAmount = options.amount() != null ? options.amount() : item.getAmount();
        if (item.getType().isAir() || effectiveAmount <= 0) {
            return Component.empty();
        }

        Component sprite = sprite(item, options);
        Component name = displayName(item, options);
        Component result = renderPattern(sprite, name, effectiveAmount, options);

        if (options.hoverEnabled()) {
            try {
                // Determine base hover item (bundle representation or original)
                ItemStack baseHoverItem = options.containerShowAsBundle()
                        ? ContainerLore.toBundle(item)
                        : item;

                ItemStack hoverItem = baseHoverItem;
                // If content lore preview is enabled and the item is a container, generate preview lore
                if (options.contentLore() != null && options.contentLore().enabled() && ContainerLore.isContainer(item)) {
                    // Clone to avoid mutating original item
                    ItemStack previewItem = baseHoverItem.clone();
                    // Render lore lines (one Component per line)
                    List<Component> loreLines = ContainerLore.render(item, options.contentLore(), options);
                    if (!loreLines.isEmpty()) {
                        previewItem.lore(loreLines);
                    }
                    hoverItem = previewItem;
                }

                if (options.stripMapDecorations()) {
                    hoverItem = sanitizeHoverItem(item, hoverItem);
                }

                result = result.hoverEvent(hoverItem);
            } catch (Throwable ignored) {
            }
        }
        return result;
    }

    public static Component format(Entity entity) {
        return format(entity, 1, defaultOptions);
    }

    public static Component format(Entity entity, int amount) {
        return format(entity, amount, defaultOptions);
    }

    public static Component format(Entity entity, ItemTextOptions options) {
        return format(entity, 1, options);
    }

    public static Component format(Entity entity, int amount, ItemTextOptions options) {
        Objects.requireNonNull(entity, "entity");
        Objects.requireNonNull(options, "options");

        int effectiveAmount = options.amount() != null ? options.amount() : amount;
        if (effectiveAmount <= 0) {
            return Component.empty();
        }

        Component sprite = mobSprite(entity, options);
        Component name = displayName(entity, options);
        Component result = renderPattern(sprite, name, effectiveAmount, options);

        if (options.hoverEnabled()) {
            result = applyEntityHover(result, entity, name);
        }
        return result;
    }

    public static Component format(Entity entity, UnaryOperator<ItemTextOptions.Builder> configurator) {
        Objects.requireNonNull(configurator, "configurator");
        return format(entity, 1, configurator.apply(defaultOptions.toBuilder()).build());
    }

    public static Component format(Entity entity, int amount, UnaryOperator<ItemTextOptions.Builder> configurator) {
        Objects.requireNonNull(configurator, "configurator");
        return format(entity, amount, configurator.apply(defaultOptions.toBuilder()).build());
    }

    public static Component format(EntityType entityType) {
        return format(entityType, 1, defaultOptions);
    }

    public static Component format(EntityType entityType, int amount) {
        return format(entityType, amount, defaultOptions);
    }

    public static Component format(EntityType entityType, ItemTextOptions options) {
        return format(entityType, 1, options);
    }

    public static Component format(EntityType entityType, int amount, ItemTextOptions options) {
        Objects.requireNonNull(entityType, "entityType");
        Objects.requireNonNull(options, "options");

        int effectiveAmount = options.amount() != null ? options.amount() : amount;
        if (effectiveAmount <= 0) {
            return Component.empty();
        }

        Component sprite = mobSprite(entityType, options);
        Component name = displayName(entityType, options);
        Component result = renderPattern(sprite, name, effectiveAmount, options);

        if (options.hoverEnabled()) {
            result = applyEntityTypeHover(result, entityType, name);
        }
        return result;
    }

    public static Component format(EntityType entityType, UnaryOperator<ItemTextOptions.Builder> configurator) {
        Objects.requireNonNull(configurator, "configurator");
        return format(entityType, 1, configurator.apply(defaultOptions.toBuilder()).build());
    }

    public static Component format(EntityType entityType, int amount, UnaryOperator<ItemTextOptions.Builder> configurator) {
        Objects.requireNonNull(configurator, "configurator");
        return format(entityType, amount, configurator.apply(defaultOptions.toBuilder()).build());
    }

    public static Component format(String entityKey) {
        return format(entityKey, 1, defaultOptions);
    }

    public static Component format(String entityKey, int amount) {
        return format(entityKey, amount, defaultOptions);
    }

    public static Component format(String entityKey, ItemTextOptions options) {
        return format(entityKey, 1, options);
    }

    public static Component format(String entityKey, int amount, ItemTextOptions options) {
        Objects.requireNonNull(entityKey, "entityKey");
        Objects.requireNonNull(options, "options");

        int effectiveAmount = options.amount() != null ? options.amount() : amount;
        if (effectiveAmount <= 0) {
            return Component.empty();
        }

        Component sprite = mobSprite(entityKey, options);
        Component name = displayName(entityKey, options);
        Component result = renderPattern(sprite, name, effectiveAmount, options);

        if (options.hoverEnabled()) {
            result = applyEntityKeyHover(result, entityKey, name);
        }
        return result;
    }

    public static Component format(String entityKey, UnaryOperator<ItemTextOptions.Builder> configurator) {
        Objects.requireNonNull(configurator, "configurator");
        return format(entityKey, 1, configurator.apply(defaultOptions.toBuilder()).build());
    }

    public static Component format(String entityKey, int amount, UnaryOperator<ItemTextOptions.Builder> configurator) {
        Objects.requireNonNull(configurator, "configurator");
        return format(entityKey, amount, configurator.apply(defaultOptions.toBuilder()).build());
    }

    private static Component renderPattern(
            Component sprite,
            Component name,
            int amount,
            ItemTextOptions options
    ) {
        TagResolver resolver = TagResolver.builder()
                .tag("item_sprite", (args, ctx) -> Tag.selfClosingInserting(sprite))
                .tag("mob_sprite", (args, ctx) -> Tag.selfClosingInserting(sprite))
                .tag("entity_sprite", (args, ctx) -> Tag.selfClosingInserting(sprite))
                .tag("mob_head", (args, ctx) -> Tag.selfClosingInserting(sprite))
                .tag("item_displayname", (args, ctx) -> Tag.selfClosingInserting(name))
                .tag("mob_name", (args, ctx) -> Tag.selfClosingInserting(name))
                .tag("entity_name", (args, ctx) -> Tag.selfClosingInserting(name))
                .tag("entity_displayname", (args, ctx) -> Tag.selfClosingInserting(name))
                .tag("mob_displayname", (args, ctx) -> Tag.selfClosingInserting(name))
                .tag("subscript_number", (args, ctx) -> amountTag(args, amount, options, options.amountDisplay()))
                .tag("superscript_number", (args, ctx) -> amountTag(args, amount, options, AmountDisplay.SUPERSCRIPT))
                .tag("normal_number", (args, ctx) -> amountTag(args, amount, options, AmountDisplay.NORMAL))
                .tag("item_amount", (args, ctx) -> amountTag(args, amount, options, options.amountDisplay()))
                .tag("mob_amount", (args, ctx) -> amountTag(args, amount, options, options.amountDisplay()))
                .tag("entity_amount", (args, ctx) -> amountTag(args, amount, options, options.amountDisplay()))
                .resolver(mobHeadTagResolver())
                .build();

        Component result = MINI_MESSAGE.deserialize(options.pattern(), resolver);
        if (options.displayBrackets()) {
            result = Component.text()
                    .append(Component.text('['))
                    .append(result)
                    .append(Component.text(']'))
                    .build();
        }
        return result;
    }

    /**
     * Converts a container {@link ItemStack} into its virtual {@link Material#BUNDLE} representation,
     * or returns the original {@code item} if not a container.
     */
    public static ItemStack toBundle(ItemStack item) {
        return ContainerLore.toBundle(item);
    }

    private static Component sprite(ItemStack item, ItemTextOptions options) {
        Component sprite = ItemSpriteFactory.create(item, options);
        return styleSprite(sprite, options);
    }

    private static Component mobSprite(Entity entity, ItemTextOptions options) {
        Component sprite = MobHeadRegistry.create(entity, options.mobHeadHat());
        return styleSprite(sprite, options);
    }

    private static Component mobSprite(EntityType entityType, ItemTextOptions options) {
        Component sprite = MobHeadRegistry.create(entityType, options.mobHeadHat());
        return styleSprite(sprite, options);
    }

    private static Component mobSprite(String entityKey, ItemTextOptions options) {
        Component sprite = MobHeadRegistry.create(entityKey, options.mobHeadHat());
        return styleSprite(sprite, options);
    }

    private static Component styleSprite(Component sprite, ItemTextOptions options) {
        if (sprite.equals(Component.empty())) {
            return sprite;
        }
        TextColor color = options.spriteColor();
        if (color != null) {
            sprite = sprite.color(color);
        }
        if (options.shadowEnabled()) {
            sprite = sprite.shadowColor(options.shadowColor());
        } else {
            sprite = sprite.shadowColor(ShadowColor.none());
        }
        return sprite;
    }

    private static Tag amountTag(ArgumentQueue args, int amount, ItemTextOptions options, AmountDisplay display) {
        if (amount == 1 && !options.showAmountWhenOne()) {
            return Tag.selfClosingInserting(Component.empty());
        }
        String suffix = args.hasNext() ? args.pop().value() : "";
        String rendered = AmountRenderer.render(amount, display) + suffix;
        return Tag.selfClosingInserting(Component.text(rendered));
    }

    private static Component displayName(ItemStack item, ItemTextOptions options) {
        Component name = null;
        if (options.displayCustomName()) {
            name = item.getData(DataComponentTypes.CUSTOM_NAME);
        } else if (options.displayCustomNameIfHasColor()) {
            Component customName = item.getData(DataComponentTypes.CUSTOM_NAME);
            if (customName != null && hasColor(customName)) {
                name = customName;
            }
        }
        if (name == null) {
            Component itemName = item.getData(DataComponentTypes.ITEM_NAME);
            if (itemName != null) {
                name = itemName;
            } else {
                name = Component.translatable(item.getType().translationKey());
            }
        }
        if (options.displayRarityColor()) {
            ItemRarity rarity = getItemRarity(item);
            TextColor rarityColor = getRarityColor(rarity);
            name = name.colorIfAbsent(rarityColor);
        }
        if (options.removeItalic()) {
            name = name.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
        }
        return name;
    }

    private static Component displayName(Entity entity, ItemTextOptions options) {
        Component name = null;
        if (options.displayCustomName()) {
            name = entity.customName();
        } else if (options.displayCustomNameIfHasColor()) {
            Component customName = entity.customName();
            if (customName != null && hasColor(customName)) {
                name = customName;
            }
        }
        if (name == null) {
            if (entity instanceof org.bukkit.entity.Player player) {
                name = Component.text(player.getName());
            } else {
                name = Component.translatable(getTranslationKey(entity.getType()));
            }
        }
        if (options.displayRarityColor()) {
            name = name.colorIfAbsent(getRarityColor(ItemRarity.COMMON));
        }
        if (options.removeItalic()) {
            name = name.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
        }
        return name;
    }

    private static Component displayName(EntityType entityType, ItemTextOptions options) {
        Component name = Component.translatable(getTranslationKey(entityType));
        if (options.displayRarityColor()) {
            name = name.colorIfAbsent(getRarityColor(ItemRarity.COMMON));
        }
        if (options.removeItalic()) {
            name = name.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
        }
        return name;
    }

    private static Component displayName(String entityKey, ItemTextOptions options) {
        String normalized = MobHeadRegistry.normalize(entityKey);
        EntityType type = resolveEntityType(normalized);
        Component name = type != null
                ? Component.translatable(getTranslationKey(type))
                : Component.translatable("entity.minecraft." + normalized);
        if (options.displayRarityColor()) {
            name = name.colorIfAbsent(getRarityColor(ItemRarity.COMMON));
        }
        if (options.removeItalic()) {
            name = name.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
        }
        return name;
    }

    private static String getTranslationKey(EntityType entityType) {
        try {
            return entityType.translationKey();
        } catch (Throwable ignored) {
            return "entity.minecraft." + entityType.name().toLowerCase(Locale.ROOT);
        }
    }

    private static @Nullable EntityType resolveEntityType(String normalized) {
        try {
            return EntityType.valueOf(normalized.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    private static Component applyEntityHover(Component component, Entity entity, Component name) {
        try {
            var hover = entity.asHoverEvent();
            if (hover != null) {
                return component.hoverEvent(hover);
            }
        } catch (Throwable ignored) {
        }
        Key key;
        try {
            key = entity.getType().key();
        } catch (Throwable ignored) {
            key = Key.key("minecraft", entity.getType().name().toLowerCase(Locale.ROOT));
        }
        return component.hoverEvent(HoverEvent.showEntity(
                key,
                entity.getUniqueId(),
                name
        ));
    }

    private static Component applyEntityTypeHover(Component component, EntityType entityType, Component name) {
        UUID uuid = UUID.nameUUIDFromBytes(("itemapi:entity:" + entityType.name()).getBytes(StandardCharsets.UTF_8));
        Key key;
        try {
            key = entityType.key();
        } catch (Throwable ignored) {
            key = Key.key("minecraft", entityType.name().toLowerCase(Locale.ROOT));
        }
        return component.hoverEvent(HoverEvent.showEntity(
                key,
                uuid,
                name
        ));
    }

    private static Component applyEntityKeyHover(Component component, String entityKey, Component name) {
        String normalized = MobHeadRegistry.normalize(entityKey);
        EntityType type = resolveEntityType(normalized);
        Key key;
        if (type != null) {
            try {
                key = type.key();
            } catch (Throwable ignored) {
                key = Key.key("minecraft", normalized);
            }
        } else {
            key = Key.key("minecraft", normalized);
        }
        UUID uuid = UUID.nameUUIDFromBytes(("itemapi:entity:" + normalized).getBytes(StandardCharsets.UTF_8));
        return component.hoverEvent(HoverEvent.showEntity(
                key,
                uuid,
                name
        ));
    }

    private static boolean hasColor(@Nullable Component component) {
        if (component == null) {
            return false;
        }
        if (component.color() != null) {
            return true;
        }
        for (Component child : component.children()) {
            if (hasColor(child)) {
                return true;
            }
        }
        return false;
    }

    private static ItemRarity getItemRarity(ItemStack item) {
        ItemRarity rarity = item.getData(DataComponentTypes.RARITY);
        if (rarity != null) {
            return rarity;
        }
        if (item.hasItemMeta()) {
            rarity = item.getItemMeta().getRarity();
            if (rarity != null) {
                return rarity;
            }
        }
        return ItemRarity.COMMON;
    }

    private static TextColor getRarityColor(ItemRarity rarity) {
        switch (rarity) {
            case UNCOMMON:
                return NamedTextColor.YELLOW;
            case RARE:
                return NamedTextColor.AQUA;
            case EPIC:
                return NamedTextColor.LIGHT_PURPLE;
            case COMMON:
            default:
                return NamedTextColor.WHITE;
        }
    }

    /**
     * Convenience: format with a modified copy of the current default options.
     */
    public static Component format(ItemStack item, UnaryOperator<ItemTextOptions.Builder> configurator) {
        Objects.requireNonNull(configurator, "configurator");
        return format(item, configurator.apply(defaultOptions.toBuilder()).build());
    }

    /**
     * Renders container content lore lines for {@code item} using global default options.
     */
    public static List<Component> containerLore(ItemStack item) {
        return ContainerLore.render(item);
    }

    /**
     * Renders container content lore lines for {@code item} using explicit {@link ContentLoreOptions}.
     */
    public static List<Component> containerLore(ItemStack item, ContentLoreOptions options) {
        return ContainerLore.render(item, options);
    }

    /**
     * Renders container content lore lines for {@code item} using explicit {@link ItemTextOptions}.
     */
    public static List<Component> containerLore(ItemStack item, ItemTextOptions options) {
        return ContainerLore.render(item, options);
    }

    /**
     * Applies container content lore to a clone of {@code item} using global default options, replacing any existing lore.
     */
    public static ItemStack applyContainerLore(ItemStack item) {
        return ContainerLore.apply(item);
    }

    /**
     * Applies container content lore to a clone of {@code item} using global default options.
     *
     * @param append if {@code true}, appends to existing lore; otherwise replaces existing lore
     */
    public static ItemStack applyContainerLore(ItemStack item, boolean append) {
        return ContainerLore.apply(item, append);
    }

    /**
     * Applies container content lore to a clone of {@code item} using explicit {@link ContentLoreOptions}, replacing existing lore.
     */
    public static ItemStack applyContainerLore(ItemStack item, ContentLoreOptions options) {
        return ContainerLore.apply(item, options);
    }

    /**
     * Applies container content lore to a clone of {@code item} using explicit {@link ContentLoreOptions}.
     */
    public static ItemStack applyContainerLore(ItemStack item, ContentLoreOptions options, boolean append) {
        return ContainerLore.apply(item, options, append);
    }

    /**
     * Applies container content lore to a clone of {@code item} using explicit {@link ItemTextOptions}, replacing existing lore.
     */
    public static ItemStack applyContainerLore(ItemStack item, ItemTextOptions options) {
        return ContainerLore.apply(item, options);
    }

    /**
     * Applies container content lore to a clone of {@code item} using explicit {@link ItemTextOptions}.
     */
    public static ItemStack applyContainerLore(ItemStack item, ItemTextOptions options, boolean append) {
        return ContainerLore.apply(item, options, append);
    }

    /**
     * Creates a player head component displaying the face/head of the specified entity with hat enabled.
     *
     * @param entity the entity
     * @return the mob head component, or {@link Component#empty()} if unsupported or unavailable
     */
    public static Component mobHead(Entity entity) {
        return MobHeadRegistry.create(entity);
    }

    /**
     * Creates a player head component displaying the face/head of the specified entity.
     *
     * @param entity the entity
     * @param hat whether the outer hat/overlay layer should be rendered
     * @return the mob head component, or {@link Component#empty()} if unsupported or unavailable
     */
    public static Component mobHead(Entity entity, boolean hat) {
        return MobHeadRegistry.create(entity, hat);
    }

    /**
     * Creates a player head component displaying the face/head of the specified entity type with hat enabled.
     *
     * @param entityType the entity type
     * @return the mob head component, or {@link Component#empty()} if unsupported or unavailable
     */
    public static Component mobHead(EntityType entityType) {
        return MobHeadRegistry.create(entityType);
    }

    /**
     * Creates a player head component displaying the face/head of the specified entity type.
     *
     * @param entityType the entity type
     * @param hat whether the outer hat/overlay layer should be rendered
     * @return the mob head component, or {@link Component#empty()} if unsupported or unavailable
     */
    public static Component mobHead(EntityType entityType, boolean hat) {
        return MobHeadRegistry.create(entityType, hat);
    }

    /**
     * Creates a player head component displaying the face/head of the specified entity name or key with hat enabled.
     *
     * @param entityKey the entity type name or key (e.g. "zombie", "minecraft:cow")
     * @return the mob head component, or {@link Component#empty()} if unsupported or unavailable
     */
    public static Component mobHead(String entityKey) {
        return MobHeadRegistry.create(entityKey);
    }

    /**
     * Creates a player head component displaying the face/head of the specified entity name or key.
     *
     * @param entityKey the entity type name or key (e.g. "zombie", "minecraft:cow")
     * @param hat whether the outer hat/overlay layer should be rendered
     * @return the mob head component, or {@link Component#empty()} if unsupported or unavailable
     */
    public static Component mobHead(String entityKey, boolean hat) {
        return MobHeadRegistry.create(entityKey, hat);
    }

    /**
     * Returns a {@link TagResolver} resolving {@code <mob_head:<entity>>} and {@code <mob_head:<entity>:<hat>>} tags.
     */
    public static TagResolver mobHeadTagResolver() {
        return TagResolver.resolver("mob_head", (args, ctx) -> {
            if (!args.hasNext()) {
                return Tag.selfClosingInserting(Component.empty());
            }
            String entityKey = args.pop().value();
            boolean hat = true;
            if (args.hasNext()) {
                String hatArg = args.pop().value().trim().toLowerCase(Locale.ROOT);
                if (hatArg.equals("false") || hatArg.equals("no_hat") || hatArg.equals("nohat") || hatArg.equals("off")) {
                    hat = false;
                }
            }
            return Tag.selfClosingInserting(mobHead(entityKey, hat));
        });
    }

    /**
     * Sanitizes an {@link ItemStack} for hover tooltip display by stripping
     * {@link DataComponentTypes#MAP_DECORATIONS} if present.
     * <p>
     * Map decorations are not displayed in item tooltips, but leak structure coordinates
     * and contain decoration types (such as {@code minecraft:abandoned_camp} introduced in 26.3)
     * that cause clients on older versions (&lt; 26.3) to fail decoding the chat packet and get kicked.
     *
     * @param original the original item
     * @param hoverItem the item being prepared for hover
     * @return the sanitized item (cloned if modified, or untouched)
     */
    static ItemStack sanitizeHoverItem(ItemStack original, ItemStack hoverItem) {
        if (hoverItem == null) {
            return null;
        }
        try {
            if (hoverItem.hasData(DataComponentTypes.MAP_DECORATIONS)) {
                if (hoverItem == original) {
                    hoverItem = hoverItem.clone();
                }
                hoverItem.setData(DataComponentTypes.MAP_DECORATIONS, MapDecorations.mapDecorations(Map.of()));
            }
        } catch (Throwable ignored) {
        }
        return hoverItem;
    }
}
