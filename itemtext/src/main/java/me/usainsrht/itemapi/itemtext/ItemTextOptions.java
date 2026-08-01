package me.usainsrht.itemapi.itemtext;



import net.kyori.adventure.text.format.NamedTextColor;

import net.kyori.adventure.text.format.ShadowColor;

import net.kyori.adventure.text.format.TextColor;

import org.jspecify.annotations.Nullable;



import java.util.Objects;



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

    private final boolean removeItalic;

    private final AmountDisplay amountDisplay;

    private final boolean showAmountWhenOne;

    private final String pattern;

    private final boolean displayRarityColor;

    private final boolean shadowEnabled;

    private final ShadowColor shadowColor;

    private final @Nullable TextColor spriteColor;

    private final boolean hoverEnabled;

    private final boolean containerShowAsBundle;



    private ItemTextOptions(Builder builder) {

        this.displayBrackets = builder.displayBrackets;

        this.displayCustomName = builder.displayCustomName;

        this.removeItalic = builder.removeItalic;

        this.amountDisplay = builder.amountDisplay;

        this.showAmountWhenOne = builder.showAmountWhenOne;

        this.displayRarityColor = builder.displayRarityColor;

        this.pattern = builder.pattern;

        this.shadowEnabled = builder.shadowEnabled;

        this.shadowColor = builder.shadowColor;

        this.spriteColor = builder.spriteColor;

        this.hoverEnabled = builder.hoverEnabled;

        this.containerShowAsBundle = builder.containerShowAsBundle;

    }



    public static ItemTextOptions defaults() {

        return builder().build();

    }



    public static Builder builder() {

        return new Builder();

    }



    public boolean displayBrackets() {

        return displayBrackets;

    }



    public boolean displayCustomName() {

        return displayCustomName;

    }



    public boolean removeItalic() {

        return removeItalic;

    }



    public AmountDisplay amountDisplay() {

        return amountDisplay;

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

     * When {@code true} (the default), items that carry a {@code container} data component

     * (e.g. shulker boxes, chests-as-items with NBT) are displayed with a bundle sprite whose

     * contents mirror the container tag.  All other item data and the display name are kept from

     * the original item; if the original has no custom name or item-name override the sprite uses

     * the original item's own translation key rather than the bundle's.

     */

    public boolean containerShowAsBundle() {

        return containerShowAsBundle;

    }



    public Builder toBuilder() {

        return new Builder()

                .displayBrackets(displayBrackets)

                .displayCustomName(displayCustomName)

                .removeItalic(removeItalic)

                .amountDisplay(amountDisplay)

                .showAmountWhenOne(showAmountWhenOne)

                .displayRarityColor(displayRarityColor)

                .pattern(pattern)

                .shadowEnabled(shadowEnabled)

                .shadowColor(shadowColor)

                .spriteColor(spriteColor)

                .hoverEnabled(hoverEnabled)

                .containerShowAsBundle(containerShowAsBundle);

    }



    public static final class Builder {

        private boolean displayBrackets = false;

        private boolean displayCustomName = true;

        private boolean removeItalic = true;

        private AmountDisplay amountDisplay = AmountDisplay.SUBSCRIPT;

        private boolean showAmountWhenOne = false;

        private boolean displayRarityColor = false;

        private String pattern = DEFAULT_PATTERN;

        private boolean shadowEnabled = false;

        private ShadowColor shadowColor = DEFAULT_SHADOW_COLOR;

        private @Nullable TextColor spriteColor = NamedTextColor.WHITE;

        private boolean hoverEnabled = true;

        private boolean containerShowAsBundle = true;



        public Builder displayBrackets(boolean displayBrackets) {

            this.displayBrackets = displayBrackets;

            return this;

        }



        public Builder displayCustomName(boolean displayCustomName) {

            this.displayCustomName = displayCustomName;

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



        public ItemTextOptions build() {

            return new ItemTextOptions(this);

        }

    }

}


