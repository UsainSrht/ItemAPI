package me.usainsrht.itemapi.itemtext;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.ShadowColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
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

    public static Component format(ItemStack item) {
        return format(item, defaultOptions);
    }

    public static Component format(ItemStack item, ItemTextOptions options) {
        Objects.requireNonNull(item, "item");
        Objects.requireNonNull(options, "options");

        if (item.getType().isAir() || item.getAmount() <= 0) {
            return Component.empty();
        }

        // subscript_number follows amountDisplay so the default pattern honors options
        TagResolver resolver = TagResolver.builder()
                .tag("item_sprite", (args, ctx) -> Tag.selfClosingInserting(sprite(item, options)))
                .tag("item_displayname", (args, ctx) -> Tag.selfClosingInserting(displayName(item, options)))
                .tag("subscript_number", (args, ctx) -> amountTag(args, item, options, options.amountDisplay()))
                .tag("superscript_number", (args, ctx) -> amountTag(args, item, options, AmountDisplay.SUPERSCRIPT))
                .tag("normal_number", (args, ctx) -> amountTag(args, item, options, AmountDisplay.NORMAL))
                .tag("item_amount", (args, ctx) -> amountTag(args, item, options, options.amountDisplay()))
                .build();

        Component result = MINI_MESSAGE.deserialize(options.pattern(), resolver);
        if (options.displayBrackets()) {
            result = Component.text()
                    .append(Component.text('['))
                    .append(result)
                    .append(Component.text(']'))
                    .build();
        }
        if (options.hoverEnabled()) {
            result = result.hoverEvent(item);
        }
        return result;
    }

    private static Component sprite(ItemStack item, ItemTextOptions options) {
        Component sprite = ItemSpriteFactory.create(item);
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

    private static Tag amountTag(ArgumentQueue args, ItemStack item, ItemTextOptions options, AmountDisplay display) {
        int amount = item.getAmount();
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
}
