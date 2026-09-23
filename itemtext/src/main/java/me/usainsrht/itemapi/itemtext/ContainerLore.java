package me.usainsrht.itemapi.itemtext;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.BundleContents;
import io.papermc.paper.datacomponent.item.ItemContainerContents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Material;
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
     * Renders container content lore for {@code item} using default {@link ItemText#defaultOptions()}.
     *
     * @param item the container item
     * @return rendered lore lines
     */
    public static List<Component> render(ItemStack item) {
        ItemTextOptions defaults = ItemText.defaultOptions();
        return render(item, defaults.contentLore(), defaults);
    }

    /**
     * Renders container content lore for {@code item} using explicit {@link ContentLoreOptions}
     * and default {@link ItemText#defaultOptions()} as parent fallback.
     */
    public static List<Component> render(ItemStack item, ContentLoreOptions options) {
        return render(item, options, ItemText.defaultOptions());
    }

    /**
     * Renders container content lore for {@code item} using {@code options.contentLore()} and {@code options}.
     */
    public static List<Component> render(ItemStack item, ItemTextOptions options) {
        Objects.requireNonNull(options, "options");
        return render(item, options.contentLore(), options);
    }

    /**
     * Applies container content lore to a clone of {@code item} using default options, replacing any existing lore.
     */
    public static ItemStack apply(ItemStack item) {
        return apply(item, false);
    }

    /**
     * Applies container content lore to a clone of {@code item} using default options.
     *
     * @param item   the item
     * @param append if {@code true}, appends to existing lore; if {@code false}, replaces existing lore
     */
    public static ItemStack apply(ItemStack item, boolean append) {
        ItemTextOptions defaults = ItemText.defaultOptions();
        return apply(item, defaults.contentLore(), defaults, append);
    }

    /**
     * Applies container content lore to a clone of {@code item} using {@code options}, replacing existing lore.
     */
    public static ItemStack apply(ItemStack item, ContentLoreOptions options) {
        return apply(item, options, false);
    }

    /**
     * Applies container content lore to a clone of {@code item} using {@code options}.
     */
    public static ItemStack apply(ItemStack item, ContentLoreOptions options, boolean append) {
        return apply(item, options, ItemText.defaultOptions(), append);
    }

    /**
     * Applies container content lore to a clone of {@code item} using {@code options}, replacing existing lore.
     */
    public static ItemStack apply(ItemStack item, ItemTextOptions options) {
        return apply(item, options, false);
    }

    /**
     * Applies container content lore to a clone of {@code item} using {@code options}.
     */
    public static ItemStack apply(ItemStack item, ItemTextOptions options, boolean append) {
        Objects.requireNonNull(options, "options");
        return apply(item, options.contentLore(), options, append);
    }

    /**
     * Applies container content lore to a clone of {@code item} using {@code options} and {@code parentOptions}.
     *
     * @param item          the container item
     * @param options       content lore options
     * @param parentOptions parent item text options
     * @param append        whether to append to existing lore or replace
     * @return cloned item with container lore applied, or {@code item} itself if disabled or not a container
     */
    public static ItemStack apply(ItemStack item, ContentLoreOptions options, ItemTextOptions parentOptions, boolean append) {
        Objects.requireNonNull(item, "item");
        Objects.requireNonNull(options, "options");
        Objects.requireNonNull(parentOptions, "parentOptions");

        if (!options.enabled() || !isContainer(item)) {
            return item;
        }

        List<Component> loreLines = render(item, options, parentOptions);
        if (loreLines.isEmpty()) {
            return item;
        }

        ItemStack clone = item.clone();
        List<Component> existing = clone.lore();
        if (append && existing != null && !existing.isEmpty()) {
            List<Component> combined = new ArrayList<>(existing);
            combined.addAll(loreLines);
            clone.lore(combined);
        } else {
            clone.lore(loreLines);
        }
        return clone;
    }

    /**
     * Returns {@code item} itself when it has no {@link DataComponentTypes#CONTAINER} component,
     * otherwise returns a virtual {@link Material#BUNDLE} that:
     * <ul>
     *   <li>carries all data components of the original item (via {@link ItemStack#withType})</li>
     *   <li>has {@code BUNDLE_CONTENTS} populated from the container's non-empty items</li>
     *   <li>has the {@code CONTAINER} component removed (bundles don't carry it)</li>
     *   <li>has {@code ITEM_NAME} set to the original item's translation key when the original
     *       has neither a {@code CUSTOM_NAME} nor an {@code ITEM_NAME} override, so the hover
     *       tooltip reads e.g. "Shulker Box" instead of "Bundle"</li>
     * </ul>
     */
    public static ItemStack toBundle(ItemStack item) {
        if (item == null) {
            return null;
        }
        ItemContainerContents container = item.getData(DataComponentTypes.CONTAINER);
        if (container == null) {
            return item;
        }

        // withType copies all data components from the original item onto a new BUNDLE stack.
        ItemStack bundle = item.withType(Material.BUNDLE);

        // Swap CONTAINER → BUNDLE_CONTENTS.
        List<ItemStack> contents = container.contents().stream()
                .filter(c -> c != null && !c.getType().isAir() && c.getAmount() > 0)
                .toList();
        bundle.unsetData(DataComponentTypes.CONTAINER);
        try {
            bundle.setData(DataComponentTypes.BUNDLE_CONTENTS,
                    BundleContents.bundleContents().addAll(contents).build());
        } catch (Throwable ignored) {
            // In unit tests or environments where Paper server bridge is unavailable
        }

        // If the original had no explicit user-set name, stamp an ITEM_NAME with the original's
        // translation key so the hover tooltip doesn't just read "Bundle".
        boolean hasCustomName = item.hasItemMeta() && (item.getItemMeta().hasDisplayName() || item.getItemMeta().hasItemName());
        if (!hasCustomName) {
            Component translateComp = Component.translatable(item.getType().translationKey());
            bundle.setData(DataComponentTypes.ITEM_NAME, translateComp);
            bundle.editMeta(meta -> meta.itemName(translateComp));
        }

        return bundle;
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

    /**
     * Returns {@code true} if the given item has a {@code CONTAINER} data component with at least one non-empty item.
     */
    public static boolean hasContents(ItemStack item) {
        if (item == null) {
            return false;
        }
        ItemContainerContents container = item.getData(DataComponentTypes.CONTAINER);
        if (container == null) {
            return false;
        }
        return container.contents().stream().anyMatch(s -> s != null && !s.getType().isAir() && s.getAmount() > 0);
    }
}
