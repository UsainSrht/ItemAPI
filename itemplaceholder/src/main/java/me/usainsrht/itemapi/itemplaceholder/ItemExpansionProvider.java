package me.usainsrht.itemapi.itemplaceholder;

import io.github.miniplaceholders.api.Expansion;
import io.github.miniplaceholders.api.provider.ExpansionProvider;
import io.github.miniplaceholders.api.provider.LoadRequirement;
import io.github.miniplaceholders.api.types.Platform;
import me.usainsrht.itemapi.itemtext.AmountDisplay;
import me.usainsrht.itemapi.itemtext.ItemText;
import me.usainsrht.itemapi.itemtext.ItemTextOptions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.ShadowColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

/**
 * MiniPlaceholders v3 expansion provider for ItemText formatting.
 *
 * <p>Args (colon-separated), e.g. {@code <item_hand:shadow:sprite_color:red:no_hover>}:
 * <ul>
 *   <li>{@code brackets} / {@code no_brackets}</li>
 *   <li>{@code custom_name} / {@code no_custom_name} ({@code translate})</li>
 *   <li>{@code italic} / {@code no_italic}</li>
 *   <li>{@code superscript} / {@code subscript} / {@code normal_amount}</li>
 *   <li>{@code show_one}</li>
 *   <li>{@code shadow} / {@code no_shadow}</li>
 *   <li>{@code shadow_color:<color>} — named, {@code #RRGGBB}, {@code #RRGGBBAA}, {@code default}, {@code none}</li>
 *   <li>{@code sprite_color:<color>} — named, hex, or {@code none}</li>
 *   <li>{@code sprite_none} / {@code no_sprite_color}</li>
 *   <li>{@code hover} / {@code no_hover}</li>
 * </ul>
 */
public final class ItemExpansionProvider implements ExpansionProvider {

    @Override
    public Expansion provideExpansion() {
        return Expansion.builder("item")
                .author("UsainSRHT")
                .version("1.0.0")
                .audiencePlaceholder(Player.class, "hand", (player, queue, ctx) ->
                        Tag.selfClosingInserting(format(player, EquipmentSlot.HAND, queue)))
                .audiencePlaceholder(Player.class, "offhand", (player, queue, ctx) ->
                        Tag.selfClosingInserting(format(player, EquipmentSlot.OFF_HAND, queue)))
                .build();
    }

    @Override
    public LoadRequirement loadRequirement() {
        return LoadRequirement.platform(Platform.PAPER);
    }

    private static Component format(Player player, EquipmentSlot slot, ArgumentQueue queue) {
        ItemStack item = player.getInventory().getItem(slot);
        if (item == null || item.getType().isAir() || item.getAmount() <= 0) {
            return Component.empty();
        }
        return ItemText.format(item, optionsFromArgs(queue));
    }

    private static ItemTextOptions optionsFromArgs(ArgumentQueue queue) {
        ItemTextOptions.Builder builder = ItemTextOptions.builder();
        while (queue.hasNext()) {
            String arg = queue.pop().lowerValue();
            switch (arg) {
                case "brackets", "bracket" -> builder.displayBrackets(true);
                case "no_brackets" -> builder.displayBrackets(false);
                case "no_custom_name", "translate" -> builder.displayCustomName(false);
                case "custom_name" -> builder.displayCustomName(true);
                case "italic" -> builder.removeItalic(false);
                case "no_italic" -> builder.removeItalic(true);
                case "superscript", "super" -> builder.amountDisplay(AmountDisplay.SUPERSCRIPT);
                case "subscript", "sub" -> builder.amountDisplay(AmountDisplay.SUBSCRIPT);
                case "normal_amount", "normal" -> builder.amountDisplay(AmountDisplay.NORMAL);
                case "show_one", "force_amount" -> builder.showAmountWhenOne(true);
                case "shadow" -> builder.shadowEnabled(true);
                case "no_shadow" -> builder.shadowEnabled(false);
                case "hover" -> builder.hoverEnabled(true);
                case "no_hover" -> builder.hoverEnabled(false);
                case "sprite_none", "no_sprite_color" -> builder.spriteColor(null);
                case "shadow_color" -> {
                    if (queue.hasNext()) {
                        ShadowColor color = parseShadowColor(queue.pop().lowerValue());
                        if (color != null) {
                            builder.shadowColor(color);
                        }
                    }
                }
                case "sprite_color" -> {
                    if (queue.hasNext()) {
                        String value = queue.pop().lowerValue();
                        if (value.equals("none")) {
                            builder.spriteColor(null);
                        } else {
                            TextColor color = parseTextColor(value);
                            if (color != null) {
                                builder.spriteColor(color);
                            }
                        }
                    }
                }
                default -> {
                    // ignore unknown args for forward compatibility
                }
            }
        }
        return builder.build();
    }

    private static ShadowColor parseShadowColor(String raw) {
        if (raw.equals("default") || raw.equals("minecraft")) {
            return ItemTextOptions.DEFAULT_SHADOW_COLOR;
        }
        if (raw.equals("none")) {
            return ShadowColor.none();
        }
        String hex = raw.startsWith("#") ? raw : "#" + raw;
        ShadowColor fromHex = ShadowColor.fromHexString(normalizeShadowHex(hex));
        if (fromHex != null) {
            return fromHex;
        }
        TextColor textColor = parseTextColor(raw);
        if (textColor != null) {
            return ShadowColor.shadowColor(textColor, 64);
        }
        return null;
    }

    private static String normalizeShadowHex(String hex) {
        // ShadowColor expects #RRGGBBAA; accept #RRGGBB by appending default alpha
        if (hex.length() == 7) {
            return hex + "40";
        }
        return hex;
    }

    private static TextColor parseTextColor(String raw) {
        if (raw.startsWith("#")) {
            return TextColor.fromHexString(raw);
        }
        if (raw.length() == 6 || raw.length() == 8) {
            TextColor hex = TextColor.fromHexString("#" + raw);
            if (hex != null) {
                return hex;
            }
        }
        return NamedTextColor.NAMES.value(raw);
    }
}
