package me.usainsrht.itemapi.itemtext;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemContainerContents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Generates the content lore preview for container items based on {@link ContentLoreOptions}.
 * Returns a {@code List<Component>} — one entry per lore line — ready to be passed to
 * {@link org.bukkit.inventory.meta.ItemMeta#lore(List)}.
 */
public final class ContainerLore {
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private ContainerLore() {
        // utility class
    }

    /**
     * Renders the container content lore for {@code item}.
     *
     * @param item          the container item to preview
     * @param options       content lore configuration
     * @param parentOptions the outer {@link ItemTextOptions} used as a fallback for
     *                      {@link ContentLoreOptions#resolveContent(ItemTextOptions)}
     *                      when {@code options.content()} is {@code null}
     * @return lore lines (one {@link Component} per line), or an empty list if disabled / not a container
     */
    public static List<Component> render(ItemStack item, ContentLoreOptions options, ItemTextOptions parentOptions) {
        Objects.requireNonNull(item, "item");
        Objects.requireNonNull(options, "options");
        Objects.requireNonNull(parentOptions, "parentOptions");

        if (!options.enabled()) {
            return List.of();
        }

        ItemContainerContents container = item.getData(DataComponentTypes.CONTAINER);
        if (container == null) {
            return List.of();
        }


        // Resolve effective content options once
        ItemTextOptions effectiveContent = options.resolveContent(parentOptions);

        // Build the <item> tag resolver: formats the container item itself (hover + content-lore disabled)
        ItemTextOptions containerItemOpts = parentOptions.toBuilder()
                .hoverEnabled(false)
                .contentLoreEnabled(false)
                .build();
        Component containerItemText = ItemText.format(item, containerItemOpts);
        TagResolver itemResolver = TagResolver.resolver("item", Tag.selfClosingInserting(containerItemText));

        List<Component> lines = new ArrayList<>();

        // Header — each string is one lore line; <item> resolved to container item text
        for (String header : options.header()) {
            lines.add(MINI_MESSAGE.deserialize(header, itemResolver));
        }

        // Body
        List<Component> body;
        if (options.mode() == ContentLoreMode.TOTAL_STACK) {
            // Filtered list: only non-empty items for aggregation
            List<ItemStack> nonEmpty = container.contents().stream()
                    .filter(s -> s != null && !s.getType().isAir() && s.getAmount() > 0)
                    .toList();
            body = renderTotalStack(nonEmpty, options, effectiveContent);
        } else {
            // Full slot list (including nulls/air) so row count matches the actual container size
            body = renderGuiView(container.contents(), options, effectiveContent);
        }

        if (body.isEmpty()) {
            // Empty container — add emptyMessage as a single line
            lines.add(MINI_MESSAGE.deserialize(options.emptyMessage()));
        } else {
            // Insert separators between body lines if defined
            if (!options.separator().isEmpty() && body.size() > 1) {
                Component sep = MINI_MESSAGE.deserialize(options.separator());
                for (int i = 0; i < body.size(); i++) {
                    lines.add(body.get(i));
                    if (i < body.size() - 1) {
                        lines.add(sep);
                    }
                }
            } else {
                lines.addAll(body);
            }
        }

        // Footer — each string is one lore line
        for (String footer : options.footer()) {
            lines.add(MINI_MESSAGE.deserialize(footer, itemResolver));
        }

        return lines;
    }

    private static List<Component> renderTotalStack(
            List<ItemStack> contents, ContentLoreOptions options, ItemTextOptions effectiveContent) {
        // Aggregate similar items using ItemStack#isSimilar()
        Map<ItemStack, Integer> aggregate = new LinkedHashMap<>();
        for (ItemStack stack : contents) {
            boolean merged = false;
            for (Map.Entry<ItemStack, Integer> entry : aggregate.entrySet()) {
                if (stack.isSimilar(entry.getKey())) {
                    aggregate.put(entry.getKey(), entry.getValue() + stack.getAmount());
                    merged = true;
                    break;
                }
            }
            if (!merged) {
                aggregate.put(stack.clone(), stack.getAmount());
            }
        }

        List<Component> lines = new ArrayList<>();
        int count = 0;
        for (Map.Entry<ItemStack, Integer> entry : aggregate.entrySet()) {
            if (options.maxLines() > 0 && count >= options.maxLines()) {
                break;
            }
            ItemStack exemplar = entry.getKey().clone();
            int total = entry.getValue();
            // Override amount with cumulative total
            ItemTextOptions contentOpts = effectiveContent.toBuilder().amount(total).build();
            Component itemComp = ItemText.format(exemplar, contentOpts);
            // Resolve <content> in contentLine template — each resolved line is one lore line
            TagResolver resolver = TagResolver.resolver("content", Tag.selfClosingInserting(itemComp));
            lines.add(MINI_MESSAGE.deserialize(options.contentLine(), resolver));
            count++;
        }
        return lines;
    }

    private static List<Component> renderGuiView(
            List<ItemStack> allSlots, ContentLoreOptions options, ItemTextOptions effectiveContent) {
        // Render all slots row by row (9 per row); each row is wrapped with contentLine
        List<Component> lines = new ArrayList<>();
        int slotCount = allSlots.size();
        int rows = slotCount > 0 ? (slotCount + 8) / 9 : 1;
        for (int row = 0; row < rows; row++) {
            StringBuilder sb = new StringBuilder();
            for (int col = 0; col < 9; col++) {
                int idx = row * 9 + col;
                ItemStack slot = idx < slotCount ? allSlots.get(idx) : null;
                if (slot != null && !slot.getType().isAir() && slot.getAmount() > 0) {
                    Component sprite = ItemSpriteFactory.create(slot, effectiveContent);
                    sb.append(MINI_MESSAGE.serialize(sprite));
                } else {
                    sb.append(options.emptySlot());
                }
                if (col < 8) {
                    sb.append(options.separator());
                }
            }
            Component rowContent = MINI_MESSAGE.deserialize(sb.toString());
            TagResolver resolver = TagResolver.resolver("content", Tag.selfClosingInserting(rowContent));
            lines.add(MINI_MESSAGE.deserialize(options.contentLine(), resolver));
        }
        return lines;
    }

    /**
     * Returns {@code true} if the given item has a {@code CONTAINER} data component.
     */
    public static boolean isContainer(ItemStack item) {
        return item != null && item.getData(DataComponentTypes.CONTAINER) != null;
    }
}
