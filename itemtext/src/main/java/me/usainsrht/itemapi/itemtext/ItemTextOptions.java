package me.usainsrht.itemapi.itemtext;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.ShadowColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.configuration.ConfigurationSection;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.UnaryOperator;

/**
 * Formatting options for {@link ItemText}.
 */
public final class ItemTextOptions {

    public static final String DEFAULT_PATTERN = "<item_sprite><subscript_number> <item_displayname>";

    /**
     * Vanilla-style text shadow: black at 25% opacity ({@code 0x40000000} ARGB).
     * Matches MiniMessage {@code <shadow>} default alpha of {@code 0.25}.
     */
    public static final ShadowColor DEFAULT_SHADOW_COLOR = ShadowColor.shadowColor(0x40000000);

    private final boolean displayBrackets;
    private final boolean displayCustomName;
    private final boolean displayCustomNameIfHasColor;
    private final boolean removeItalic;
    private final AmountDisplay amountDisplay;
    private final @Nullable Integer amount;
    private final boolean showAmountWhenOne;
    private final String pattern;
    private final boolean displayRarityColor;
    private final boolean shadowEnabled;
    private final ShadowColor shadowColor;
    private final @Nullable TextColor spriteColor;
    private final boolean hoverEnabled;
    private final boolean containerShowAsBundle;
    private final boolean usePlayerHeadsFor3DBlocks;
    private final ContentLoreOptions contentLore;

    private ItemTextOptions(Builder builder) {
        this.displayBrackets = builder.displayBrackets;
        this.displayCustomName = builder.displayCustomName;
        this.displayCustomNameIfHasColor = builder.displayCustomNameIfHasColor;
        this.removeItalic = builder.removeItalic;
        this.amountDisplay = builder.amountDisplay;
        this.amount = builder.amount;
        this.showAmountWhenOne = builder.showAmountWhenOne;
        this.displayRarityColor = builder.displayRarityColor;
        this.pattern = builder.pattern;
        this.shadowEnabled = builder.shadowEnabled;
        this.shadowColor = builder.shadowColor;
        this.spriteColor = builder.spriteColor;
        this.hoverEnabled = builder.hoverEnabled;
        this.containerShowAsBundle = builder.containerShowAsBundle;
        this.usePlayerHeadsFor3DBlocks = builder.usePlayerHeadsFor3DBlocks;
        this.contentLore = builder.contentLore;
    }

    public static ItemTextOptions defaults() {
        return builder().build();
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Parses {@link ItemTextOptions} from a {@link ConfigurationSection}, or
     * returns {@link #defaults()} if {@code section} is null.
     */
    public static ItemTextOptions fromConfig(@Nullable ConfigurationSection section) {
        if (section == null) {
            return defaults();
        }
        return builder().load(section).build();
    }

    /**
     * Parses {@link ItemTextOptions} from a {@link Map}, or returns
     * {@link #defaults()} if {@code map} is null.
     */
    public static ItemTextOptions fromMap(@Nullable Map<String, ?> map) {
        if (map == null) {
            return defaults();
        }
        return builder().load(map).build();
    }

    public boolean displayBrackets() {
        return displayBrackets;
    }

    public boolean displayCustomName() {
        return displayCustomName;
    }

    public boolean displayCustomNameIfHasColor() {
        return displayCustomNameIfHasColor;
    }

    public boolean removeItalic() {
        return removeItalic;
    }

    public AmountDisplay amountDisplay() {
        return amountDisplay;
    }

    /**
     * Display amount override. When {@code null},
     * {@link org.bukkit.inventory.ItemStack#getAmount()} is used.
     * Any {@code int} value is accepted (negative, zero, or above max stack size).
     */
    public @Nullable Integer amount() {
        return amount;
    }

    public boolean showAmountWhenOne() {
        return showAmountWhenOne;
    }

    public boolean displayRarityColor() {
        return displayRarityColor;
    }

    public String pattern() {
        return pattern;
    }

    public boolean shadowEnabled() {
        return shadowEnabled;
    }

    public ShadowColor shadowColor() {
        return shadowColor;
    }

    /**
     * Tint applied to {@code <item_sprite>}. {@code null} means no color (none).
     */
    public @Nullable TextColor spriteColor() {
        return spriteColor;
    }

    public boolean hoverEnabled() {
        return hoverEnabled;
    }

    /**
     * When {@code true} (the default), items that carry a {@code container} data
     * component
     * (e.g. shulker boxes, chests-as-items with NBT) are displayed with a bundle
     * sprite whose
     * contents mirror the container tag. All other item data and the display name
     * are kept from
     * the original item; if the original has no custom name or item-name override
     * the sprite uses
     * the original item's own translation key rather than the bundle's.
     */
    public boolean containerShowAsBundle() {
        return containerShowAsBundle;
    }

    /**
     * When {@code true} (the default), items and blocks with 3D or entity models
     * (such as
     * chests, mob heads, and heavy core) are rendered using 2D player head font
     * glyphs with
     * curated, asynchronous client-cached skin textures. When {@code false}, they
     * fall back
     * to standard block/item atlas sprites.
     */
    public boolean usePlayerHeadsFor3DBlocks() {
        return usePlayerHeadsFor3DBlocks;
    }

    public ContentLoreOptions contentLore() {
        return contentLore;
    }

    public Builder toBuilder() {
        return new Builder()
                .displayBrackets(displayBrackets)
                .displayCustomName(displayCustomName)
                .displayCustomNameIfHasColor(displayCustomNameIfHasColor)
                .removeItalic(removeItalic)
                .amountDisplay(amountDisplay)
                .amount(amount)
                .showAmountWhenOne(showAmountWhenOne)
                .displayRarityColor(displayRarityColor)
                .pattern(pattern)
                .shadowEnabled(shadowEnabled)
                .shadowColor(shadowColor)
                .spriteColor(spriteColor)
                .hoverEnabled(hoverEnabled)
                .containerShowAsBundle(containerShowAsBundle)
                .usePlayerHeadsFor3DBlocks(usePlayerHeadsFor3DBlocks)
                .contentLore(contentLore);
    }

    public static final class Builder {

        private boolean displayBrackets = false;
        private boolean displayCustomName = true;
        private boolean displayCustomNameIfHasColor = false;
        private boolean removeItalic = true;
        private AmountDisplay amountDisplay = AmountDisplay.SUBSCRIPT;
        private @Nullable Integer amount = null;
        private boolean showAmountWhenOne = false;
        private boolean displayRarityColor = false;
        private String pattern = DEFAULT_PATTERN;
        private boolean shadowEnabled = false;
        private ShadowColor shadowColor = DEFAULT_SHADOW_COLOR;
        private @Nullable TextColor spriteColor = NamedTextColor.WHITE;
        private boolean hoverEnabled = true;
        private boolean containerShowAsBundle = true;
        private boolean usePlayerHeadsFor3DBlocks = true;
        private ContentLoreOptions contentLore = ContentLoreOptions.defaults();

        public Builder displayBrackets(boolean displayBrackets) {
            this.displayBrackets = displayBrackets;
            return this;
        }

        public Builder displayCustomName(boolean displayCustomName) {
            this.displayCustomName = displayCustomName;
            return this;
        }

        public Builder displayCustomNameIfHasColor(boolean displayCustomNameIfHasColor) {
            this.displayCustomNameIfHasColor = displayCustomNameIfHasColor;
            return this;
        }

        public Builder removeItalic(boolean removeItalic) {
            this.removeItalic = removeItalic;
            return this;
        }

        public Builder amountDisplay(AmountDisplay amountDisplay) {
            this.amountDisplay = Objects.requireNonNull(amountDisplay, "amountDisplay");
            return this;
        }

        /**
         * Sets the displayed amount, overriding
         * {@link org.bukkit.inventory.ItemStack#getAmount()}.
         * Pass {@code null} to use the stack size again.
         */
        public Builder amount(@Nullable Integer amount) {
            this.amount = amount;
            return this;
        }

        public Builder showAmountWhenOne(boolean showAmountWhenOne) {
            this.showAmountWhenOne = showAmountWhenOne;
            return this;
        }

        public Builder displayRarityColor(boolean displayRarityColor) {
            this.displayRarityColor = displayRarityColor;
            return this;
        }

        public Builder pattern(String pattern) {
            this.pattern = Objects.requireNonNull(pattern, "pattern");
            return this;
        }

        public Builder shadowEnabled(boolean shadowEnabled) {
            this.shadowEnabled = shadowEnabled;
            return this;
        }

        public Builder shadowColor(ShadowColor shadowColor) {
            this.shadowColor = Objects.requireNonNull(shadowColor, "shadowColor");
            return this;
        }

        /**
         * Sets sprite tint. Pass {@code null} for none (no color applied).
         */
        public Builder spriteColor(@Nullable TextColor spriteColor) {
            this.spriteColor = spriteColor;
            return this;
        }

        public Builder hoverEnabled(boolean hoverEnabled) {
            this.hoverEnabled = hoverEnabled;
            return this;
        }

        public Builder containerShowAsBundle(boolean containerShowAsBundle) {
            this.containerShowAsBundle = containerShowAsBundle;
            return this;
        }

        public Builder usePlayerHeadsFor3DBlocks(boolean usePlayerHeadsFor3DBlocks) {
            this.usePlayerHeadsFor3DBlocks = usePlayerHeadsFor3DBlocks;
            return this;
        }

        public Builder contentLore(ContentLoreOptions contentLore) {
            this.contentLore = Objects.requireNonNull(contentLore, "contentLore");
            return this;
        }

        public Builder contentLore(UnaryOperator<ContentLoreOptions.Builder> configurator) {
            Objects.requireNonNull(configurator, "configurator");
            this.contentLore = configurator.apply(this.contentLore.toBuilder()).build();
            return this;
        }

        public Builder contentLoreEnabled(boolean enabled) {
            this.contentLore = this.contentLore.toBuilder().enabled(enabled).build();
            return this;
        }

        public Builder contentLoreMode(ContentLoreMode mode) {
            this.contentLore = this.contentLore.toBuilder().mode(mode).build();
            return this;
        }

        public Builder contentLoreMode(String modeName) {
            this.contentLore = this.contentLore.toBuilder().mode(modeName).build();
            return this;
        }

        public Builder contentLoreModeGuiView() {
            this.contentLore = this.contentLore.toBuilder().mode(ContentLoreMode.GUI_VIEW).build();
            return this;
        }

        public Builder contentLoreModeTotalStack() {
            this.contentLore = this.contentLore.toBuilder().mode(ContentLoreMode.TOTAL_STACK).build();
            return this;
        }

        public Builder contentLoreHeader(List<String> header) {
            this.contentLore = this.contentLore.toBuilder().header(header).build();
            return this;
        }

        public Builder contentLoreHeader(String... header) {
            this.contentLore = this.contentLore.toBuilder().header(header).build();
            return this;
        }

        public Builder contentLoreFooter(List<String> footer) {
            this.contentLore = this.contentLore.toBuilder().footer(footer).build();
            return this;
        }

        public Builder contentLoreFooter(String... footer) {
            this.contentLore = this.contentLore.toBuilder().footer(footer).build();
            return this;
        }

        public Builder contentLoreEmptySlot(String emptySlot) {
            this.contentLore = this.contentLore.toBuilder().emptySlot(emptySlot).build();
            return this;
        }

        public Builder contentLoreSeparator(String separator) {
            this.contentLore = this.contentLore.toBuilder().separator(separator).build();
            return this;
        }

        public Builder contentLoreEmptyMessage(String emptyMessage) {
            this.contentLore = this.contentLore.toBuilder().emptyMessage(emptyMessage).build();
            return this;
        }

        public Builder contentLoreMaxLines(int maxLines) {
            this.contentLore = this.contentLore.toBuilder().maxLines(maxLines).build();
            return this;
        }

        public Builder contentLoreContentLine(String contentLine) {
            this.contentLore = this.contentLore.toBuilder().contentLine(contentLine).build();
            return this;
        }

        public Builder contentLoreContent(@Nullable ItemTextOptions content) {
            this.contentLore = this.contentLore.toBuilder().content(content).build();
            return this;
        }

        public Builder contentLoreContent(UnaryOperator<ItemTextOptions.Builder> configurator) {
            this.contentLore = this.contentLore.toBuilder().content(configurator).build();
            return this;
        }

        /**
         * Loads only container-related settings (show-as-bundle and container-lore)
         * from a {@link ConfigurationSection}.
         */
        public Builder loadContainerSettings(@Nullable ConfigurationSection section) {
            if (section == null) {
                return this;
            }
            Boolean bundleVal = getBoolean(section, "container-show-as-bundle", "container_show_as_bundle",
                    "containerShowAsBundle", "show-as-bundle", "bundle");
            if (bundleVal != null) {
                this.containerShowAsBundle = bundleVal;
            }
            for (String key : new String[] { "container-lore", "container_lore", "containerLore", "content-lore",
                    "content_lore", "contentLore" }) {
                if (section.isConfigurationSection(key)) {
                    this.contentLore = this.contentLore.toBuilder().load(section.getConfigurationSection(key)).build();
                    break;
                } else if (section.isBoolean(key)) {
                    this.contentLore = this.contentLore.toBuilder().enabled(section.getBoolean(key)).build();
                    break;
                }
            }
            return this;
        }

        /**
         * Loads only container-related settings (show-as-bundle and container-lore)
         * from a {@link Map}.
         */
        public Builder loadContainerSettings(@Nullable Map<String, ?> map) {
            if (map == null) {
                return this;
            }
            Boolean bundleVal = getMapBoolean(map, "container-show-as-bundle", "container_show_as_bundle",
                    "containerShowAsBundle", "show-as-bundle", "bundle");
            if (bundleVal != null) {
                this.containerShowAsBundle = bundleVal;
            }
            for (String key : new String[] { "container-lore", "container_lore", "containerLore", "content-lore",
                    "content_lore", "contentLore" }) {
                Object val = map.get(key);
                if (val instanceof Map<?, ?> m) {
                    @SuppressWarnings("unchecked")
                    Map<String, ?> typed = (Map<String, ?>) m;
                    this.contentLore = this.contentLore.toBuilder().load(typed).build();
                    break;
                } else if (val instanceof Boolean b) {
                    this.contentLore = this.contentLore.toBuilder().enabled(b).build();
                    break;
                } else if (val instanceof String s && (s.equalsIgnoreCase("true") || s.equalsIgnoreCase("false"))) {
                    this.contentLore = this.contentLore.toBuilder().enabled(Boolean.parseBoolean(s)).build();
                    break;
                }
            }
            return this;
        }

        /**
         * Loads full options from a {@link ConfigurationSection}.
         */
        public Builder load(@Nullable ConfigurationSection section) {
            if (section == null) {
                return this;
            }
            loadContainerSettings(section);

            Boolean brackets = getBoolean(section, "brackets", "display-brackets", "display_brackets",
                    "displayBrackets");
            if (brackets != null) {
                this.displayBrackets = brackets;
            }

            Boolean customName = getBoolean(section, "custom-name", "custom_name", "display-custom-name",
                    "display_custom_name", "displayCustomName");
            if (customName != null) {
                this.displayCustomName = customName;
            }

            Boolean customNameColored = getBoolean(section, "custom-name-if-has-color", "custom_name_if_has_color",
                    "display-custom-name-if-has-color", "display_custom_name_if_has_color",
                    "displayCustomNameIfHasColor");
            if (customNameColored != null) {
                this.displayCustomNameIfHasColor = customNameColored;
            }

            Boolean removeItalicVal = getBoolean(section, "remove-italic", "remove_italic", "removeItalic");
            if (removeItalicVal != null) {
                this.removeItalic = removeItalicVal;
            } else {
                Boolean italicVal = getBoolean(section, "italic");
                if (italicVal != null) {
                    this.removeItalic = !italicVal;
                }
            }

            String amountDisp = getString(section, "amount-display", "amount_display", "amountDisplay");
            if (amountDisp != null) {
                switch (amountDisp.toLowerCase()) {
                    case "superscript", "super" -> this.amountDisplay = AmountDisplay.SUPERSCRIPT;
                    case "normal" -> this.amountDisplay = AmountDisplay.NORMAL;
                    case "subscript", "sub" -> this.amountDisplay = AmountDisplay.SUBSCRIPT;
                }
            }

            Integer amt = getInt(section, "amount");
            if (amt != null) {
                this.amount = amt;
            }

            Boolean showOne = getBoolean(section, "show-amount-when-one", "show_amount_when_one", "show-one",
                    "show_one", "showAmountWhenOne");
            if (showOne != null) {
                this.showAmountWhenOne = showOne;
            }

            Boolean rarityColor = getBoolean(section, "display-rarity-color", "display_rarity_color",
                    "displayRarityColor", "rarity-color", "rarity_color");
            if (rarityColor != null) {
                this.displayRarityColor = rarityColor;
            }

            String pat = getString(section, "pattern");
            if (pat != null) {
                this.pattern = pat;
            }

            Boolean shadow = getBoolean(section, "shadow", "shadow-enabled", "shadow_enabled", "shadowEnabled");
            if (shadow != null) {
                this.shadowEnabled = shadow;
            }

            String shadowCol = getString(section, "shadow-color", "shadow_color", "shadowColor");
            if (shadowCol != null) {
                ShadowColor parsed = parseShadowColor(shadowCol);
                if (parsed != null) {
                    this.shadowColor = parsed;
                }
            }

            String spriteCol = getString(section, "sprite-color", "sprite_color", "spriteColor");
            if (spriteCol != null) {
                this.spriteColor = parseTextColor(spriteCol);
            }

            Boolean hover = getBoolean(section, "hover", "hover-enabled", "hover_enabled", "hoverEnabled");
            if (hover != null) {
                this.hoverEnabled = hover;
            }

            Boolean playerHeads = getBoolean(section, "use-player-heads-for-3d-blocks",
                    "use_player_heads_for_3d_blocks", "usePlayerHeadsFor3DBlocks");
            if (playerHeads != null) {
                this.usePlayerHeadsFor3DBlocks = playerHeads;
            }

            return this;
        }

        /**
         * Loads full options from a {@link Map}.
         */
        public Builder load(@Nullable Map<String, ?> map) {
            if (map == null) {
                return this;
            }
            loadContainerSettings(map);

            Boolean brackets = getMapBoolean(map, "brackets", "display-brackets", "display_brackets",
                    "displayBrackets");
            if (brackets != null) {
                this.displayBrackets = brackets;
            }

            Boolean customName = getMapBoolean(map, "custom-name", "custom_name", "display-custom-name",
                    "display_custom_name", "displayCustomName");
            if (customName != null) {
                this.displayCustomName = customName;
            }

            Boolean customNameColored = getMapBoolean(map, "custom-name-if-has-color", "custom_name_if_has_color",
                    "display-custom-name-if-has-color", "display_custom_name_if_has_color",
                    "displayCustomNameIfHasColor");
            if (customNameColored != null) {
                this.displayCustomNameIfHasColor = customNameColored;
            }

            Boolean removeItalicVal = getMapBoolean(map, "remove-italic", "remove_italic", "removeItalic");
            if (removeItalicVal != null) {
                this.removeItalic = removeItalicVal;
            } else {
                Boolean italicVal = getMapBoolean(map, "italic");
                if (italicVal != null) {
                    this.removeItalic = !italicVal;
                }
            }

            String amountDisp = getMapString(map, "amount-display", "amount_display", "amountDisplay");
            if (amountDisp != null) {
                switch (amountDisp.toLowerCase()) {
                    case "superscript", "super" -> this.amountDisplay = AmountDisplay.SUPERSCRIPT;
                    case "normal" -> this.amountDisplay = AmountDisplay.NORMAL;
                    case "subscript", "sub" -> this.amountDisplay = AmountDisplay.SUBSCRIPT;
                }
            }

            Integer amt = getMapInt(map, "amount");
            if (amt != null) {
                this.amount = amt;
            }

            Boolean showOne = getMapBoolean(map, "show-amount-when-one", "show_amount_when_one", "show-one", "show_one",
                    "showAmountWhenOne");
            if (showOne != null) {
                this.showAmountWhenOne = showOne;
            }

            Boolean rarityColor = getMapBoolean(map, "display-rarity-color", "display_rarity_color",
                    "displayRarityColor", "rarity-color", "rarity_color");
            if (rarityColor != null) {
                this.displayRarityColor = rarityColor;
            }

            String pat = getMapString(map, "pattern");
            if (pat != null) {
                this.pattern = pat;
            }

            Boolean shadow = getMapBoolean(map, "shadow", "shadow-enabled", "shadow_enabled", "shadowEnabled");
            if (shadow != null) {
                this.shadowEnabled = shadow;
            }

            String shadowCol = getMapString(map, "shadow-color", "shadow_color", "shadowColor");
            if (shadowCol != null) {
                ShadowColor parsed = parseShadowColor(shadowCol);
                if (parsed != null) {
                    this.shadowColor = parsed;
                }
            }

            String spriteCol = getMapString(map, "sprite-color", "sprite_color", "spriteColor");
            if (spriteCol != null) {
                this.spriteColor = parseTextColor(spriteCol);
            }

            Boolean hover = getMapBoolean(map, "hover", "hover-enabled", "hover_enabled", "hoverEnabled");
            if (hover != null) {
                this.hoverEnabled = hover;
            }

            Boolean playerHeads = getMapBoolean(map, "use-player-heads-for-3d-blocks", "use_player_heads_for_3d_blocks",
                    "usePlayerHeadsFor3DBlocks");
            if (playerHeads != null) {
                this.usePlayerHeadsFor3DBlocks = playerHeads;
            }

            return this;
        }

        private static @Nullable Boolean getBoolean(ConfigurationSection section, String... keys) {
            for (String key : keys) {
                if (section.isBoolean(key)) {
                    return section.getBoolean(key);
                }
            }
            return null;
        }

        private static @Nullable String getString(ConfigurationSection section, String... keys) {
            for (String key : keys) {
                if (section.isString(key)) {
                    return section.getString(key);
                }
            }
            return null;
        }

        private static @Nullable Integer getInt(ConfigurationSection section, String... keys) {
            for (String key : keys) {
                if (section.isInt(key)) {
                    return section.getInt(key);
                }
            }
            return null;
        }

        private static @Nullable Boolean getMapBoolean(Map<String, ?> map, String... keys) {
            for (String key : keys) {
                Object val = map.get(key);
                if (val instanceof Boolean b) {
                    return b;
                } else if (val instanceof String s) {
                    if (s.equalsIgnoreCase("true"))
                        return true;
                    if (s.equalsIgnoreCase("false"))
                        return false;
                }
            }
            return null;
        }

        private static @Nullable String getMapString(Map<String, ?> map, String... keys) {
            for (String key : keys) {
                Object val = map.get(key);
                if (val != null) {
                    return val.toString();
                }
            }
            return null;
        }

        private static @Nullable Integer getMapInt(Map<String, ?> map, String... keys) {
            for (String key : keys) {
                Object val = map.get(key);
                if (val instanceof Number n) {
                    return n.intValue();
                } else if (val instanceof String s) {
                    try {
                        return Integer.parseInt(s);
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
            return null;
        }

        private static @Nullable ShadowColor parseShadowColor(String raw) {
            if (raw == null || raw.isBlank()) {
                return null;
            }
            String lower = raw.trim().toLowerCase();
            if (lower.equals("default") || lower.equals("minecraft")) {
                return DEFAULT_SHADOW_COLOR;
            }
            if (lower.equals("none")) {
                return ShadowColor.none();
            }
            String hex = lower.startsWith("#") ? lower : "#" + lower;
            if (hex.length() == 7) {
                hex = hex + "40";
            }
            ShadowColor fromHex = ShadowColor.fromHexString(hex);
            if (fromHex != null) {
                return fromHex;
            }
            TextColor textColor = parseTextColor(raw);
            if (textColor != null) {
                return ShadowColor.shadowColor(textColor, 64);
            }
            return null;
        }

        private static @Nullable TextColor parseTextColor(String raw) {
            if (raw == null || raw.isBlank()) {
                return null;
            }
            String val = raw.trim();
            if (val.equalsIgnoreCase("none")) {
                return null;
            }
            if (val.startsWith("#")) {
                return TextColor.fromHexString(val);
            }
            if (val.length() == 6 || val.length() == 8) {
                TextColor hex = TextColor.fromHexString("#" + val);
                if (hex != null) {
                    return hex;
                }
            }
            return NamedTextColor.NAMES.value(val.toLowerCase());
        }

        public ItemTextOptions build() {
            return new ItemTextOptions(this);
        }
    }
}
