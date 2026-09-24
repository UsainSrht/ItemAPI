package me.usainsrht.itemapi.itemtext;

import org.bukkit.configuration.ConfigurationSection;
import org.jspecify.annotations.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.UnaryOperator;

/**
 * Configuration options for container content lore preview.
 *
 * <p>
 * The {@code content} field is optional. When {@code null} (the default), the
 * rendering
 * engine inherits the parent {@link ItemTextOptions} and forces
 * {@code hoverEnabled=false}
 * and {@code contentLoreEnabled=false} to prevent recursion and unnecessary
 * computation.
 * </p>
 */
public final class ContentLoreOptions {
    private final boolean enabled;
    private final ContentLoreMode mode;
    private final List<String> header;
    private final List<String> footer;
    private final @Nullable ItemTextOptions content;
    private final @Nullable UnaryOperator<ItemTextOptions.Builder> contentModifier;
    private final String emptySlot;
    private final String separator;
    private final String emptyMessage;
    private final int maxLines;
    private final String contentLine;

    private ContentLoreOptions(Builder builder) {
        this.enabled = builder.enabled;
        this.mode = builder.mode;
        this.header = Collections.unmodifiableList(new ArrayList<>(builder.header));
        this.footer = Collections.unmodifiableList(new ArrayList<>(builder.footer));
        this.content = builder.content; // nullable — null means inherit from parent
        this.contentModifier = builder.contentModifier;
        this.emptySlot = Objects.requireNonNull(builder.emptySlot, "emptySlot");
        this.separator = Objects.requireNonNull(builder.separator, "separator");
        this.emptyMessage = Objects.requireNonNull(builder.emptyMessage, "emptyMessage");
        this.maxLines = builder.maxLines;
        this.contentLine = Objects.requireNonNull(builder.contentLine, "contentLine");
    }

    public static ContentLoreOptions defaults() {
        return builder().build();
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Parses {@link ContentLoreOptions} from a {@link ConfigurationSection}, or
     * returns {@link #defaults()} if {@code section} is null.
     */
    public static ContentLoreOptions fromConfig(@Nullable ConfigurationSection section) {
        if (section == null) {
            return defaults();
        }
        return builder().load(section).build();
    }

    /**
     * Parses {@link ContentLoreOptions} from a {@link Map}, or returns
     * {@link #defaults()} if {@code map} is null.
     */
    public static ContentLoreOptions fromMap(@Nullable Map<String, ?> map) {
        if (map == null) {
            return defaults();
        }
        return builder().load(map).build();
    }

    public boolean enabled() {
        return enabled;
    }

    public ContentLoreMode mode() {
        return mode;
    }

    public List<String> header() {
        return header;
    }

    public List<String> footer() {
        return footer;
    }

    /**
     * Explicit content formatting options, or {@code null} to inherit from the
     * parent {@link ItemTextOptions} (with hover and content-lore disabled).
     */
    public @Nullable ItemTextOptions content() {
        if (content != null) {
            return content;
        }
        if (contentModifier != null) {
            return contentModifier.apply(ItemTextOptions.builder()
                    .removeItalic(false)
                    .hoverEnabled(false)
                    .contentLore(lore -> lore.enabled(false)))
                    .build();
        }
        return null;
    }

    /**
     * Optional configurator that overrides only explicitly specified values
     * on the parent {@link ItemTextOptions}.
     */
    public @Nullable UnaryOperator<ItemTextOptions.Builder> contentModifier() {
        return contentModifier;
    }

    public String emptySlot() {
        return emptySlot;
    }

    public String separator() {
        return separator;
    }

    public String emptyMessage() {
        return emptyMessage;
    }

    public int maxLines() {
        return maxLines;
    }

    public String contentLine() {
        return contentLine;
    }

    /**
     * Resolves the effective content options to use when rendering inner items.
     *
     * <p>
     * If an explicit static {@link #content()} is present, it is used as the base.
     * Otherwise {@code parentOptions} is used with hover and content-lore disabled
     * to prevent recursion, and {@code removeItalic} set to {@code false} by default
     * for container content preview.
     * </p>
     * <p>
     * If a {@code contentModifier} is present (e.g. from {@link Builder#content(UnaryOperator)}
     * or a {@code content} section in YAML/Map), it is applied to override only the explicitly
     * specified values, leaving all other settings inherited from the parent.
     * </p>
     */
    ItemTextOptions resolveContent(ItemTextOptions parentOptions) {
        ItemTextOptions.Builder builder;
        if (content != null) {
            builder = content.toBuilder();
        } else {
            builder = parentOptions.toBuilder()
                    .hoverEnabled(false)
                    .contentLoreEnabled(false)
                    .removeItalic(false);
        }
        if (contentModifier != null) {
            builder = contentModifier.apply(builder);
        }
        return builder.build();
    }

    public Builder toBuilder() {
        return new Builder()
                .enabled(enabled)
                .mode(mode)
                .header(header)
                .footer(footer)
                .content(content)
                .contentModifier(contentModifier)
                .emptySlot(emptySlot)
                .separator(separator)
                .emptyMessage(emptyMessage)
                .maxLines(maxLines)
                .contentLine(contentLine);
    }

    public static final class Builder {
        private boolean enabled = true;
        private ContentLoreMode mode = ContentLoreMode.TOTAL_STACK;
        private List<String> header = List
                .of(" <white><!italic><translate_or:entity.minecraft.sulfur_cube.content:'%s':'<item>'>");
        private List<String> footer = List.of();
        /** {@code null} = inherit from parent ItemTextOptions at render time. */
        private @Nullable ItemTextOptions content = null;
        private @Nullable UnaryOperator<ItemTextOptions.Builder> contentModifier = null;
        private String emptySlot = "<sprite:gui:container/slot>";
        private String separator = "";
        private String emptyMessage = " <white><!italic><translate:item.minecraft.bundle.empty> ";
        private int maxLines = 27;
        private String contentLine = "  <white><!italic><content> ";

        public Builder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder mode(ContentLoreMode mode) {
            this.mode = Objects.requireNonNull(mode, "mode");
            return this;
        }

        public Builder mode(String modeName) {
            this.mode = ContentLoreMode.fromString(modeName, this.mode);
            return this;
        }

        public Builder header(List<String> header) {
            this.header = new ArrayList<>(Objects.requireNonNull(header, "header"));
            return this;
        }

        public Builder header(String... header) {
            if (header == null) {
                this.header = new ArrayList<>();
            } else {
                this.header = new ArrayList<>(List.of(header));
            }
            return this;
        }

        public Builder addHeader(String... lines) {
            if (lines != null) {
                for (String line : lines) {
                    if (line != null) {
                        this.header.add(line);
                    }
                }
            }
            return this;
        }

        public Builder footer(List<String> footer) {
            this.footer = new ArrayList<>(Objects.requireNonNull(footer, "footer"));
            return this;
        }

        public Builder footer(String... footer) {
            if (footer == null) {
                this.footer = new ArrayList<>();
            } else {
                this.footer = new ArrayList<>(List.of(footer));
            }
            return this;
        }

        public Builder addFooter(String... lines) {
            if (lines != null) {
                for (String line : lines) {
                    if (line != null) {
                        this.footer.add(line);
                    }
                }
            }
            return this;
        }

        /**
         * Loads configuration settings from a {@link ConfigurationSection}.
         */
        public Builder load(@Nullable ConfigurationSection section) {
            if (section == null) {
                return this;
            }
            if (section.isBoolean("enabled")) {
                this.enabled = section.getBoolean("enabled");
            }
            String modeStr = getString(section, "mode");
            if (modeStr != null) {
                this.mode = ContentLoreMode.fromString(modeStr, this.mode);
            }
            List<String> headerList = getStringListOrSingle(section, "header", "headers");
            if (headerList != null) {
                this.header = new ArrayList<>(headerList);
            }
            List<String> footerList = getStringListOrSingle(section, "footer", "footers");
            if (footerList != null) {
                this.footer = new ArrayList<>(footerList);
            }
            String emptySlotVal = getString(section, "empty-slot", "empty_slot", "emptySlot");
            if (emptySlotVal != null) {
                this.emptySlot = emptySlotVal;
            }
            String separatorVal = getString(section, "separator");
            if (separatorVal != null) {
                this.separator = separatorVal;
            }
            String emptyMsgVal = getString(section, "empty-message", "empty_message", "emptyMessage");
            if (emptyMsgVal != null) {
                this.emptyMessage = emptyMsgVal;
            }
            Integer maxLinesVal = getInt(section, "max-lines", "max_lines", "maxLines");
            if (maxLinesVal != null) {
                this.maxLines = maxLinesVal;
            }
            String contentLineVal = getString(section, "content-line", "content_line", "contentLine");
            if (contentLineVal != null) {
                this.contentLine = contentLineVal;
            }
            if (section.isConfigurationSection("content")) {
                ConfigurationSection contentSec = section.getConfigurationSection("content");
                this.content(b -> b.load(contentSec));
            }
            return this;
        }

        /**
         * Loads configuration settings from a {@link Map}.
         */
        public Builder load(@Nullable Map<String, ?> map) {
            if (map == null) {
                return this;
            }
            Object enabledVal = map.get("enabled");
            if (enabledVal instanceof Boolean b) {
                this.enabled = b;
            } else if (enabledVal instanceof String s) {
                this.enabled = Boolean.parseBoolean(s);
            }
            String modeStr = getMapString(map, "mode");
            if (modeStr != null) {
                this.mode = ContentLoreMode.fromString(modeStr, this.mode);
            }
            List<String> headerList = getMapStringListOrSingle(map, "header", "headers");
            if (headerList != null) {
                this.header = new ArrayList<>(headerList);
            }
            List<String> footerList = getMapStringListOrSingle(map, "footer", "footers");
            if (footerList != null) {
                this.footer = new ArrayList<>(footerList);
            }
            String emptySlotVal = getMapString(map, "empty-slot", "empty_slot", "emptySlot");
            if (emptySlotVal != null) {
                this.emptySlot = emptySlotVal;
            }
            String separatorVal = getMapString(map, "separator");
            if (separatorVal != null) {
                this.separator = separatorVal;
            }
            String emptyMsgVal = getMapString(map, "empty-message", "empty_message", "emptyMessage");
            if (emptyMsgVal != null) {
                this.emptyMessage = emptyMsgVal;
            }
            Integer maxLinesVal = getMapInt(map, "max-lines", "max_lines", "maxLines");
            if (maxLinesVal != null) {
                this.maxLines = maxLinesVal;
            }
            String contentLineVal = getMapString(map, "content-line", "content_line", "contentLine");
            if (contentLineVal != null) {
                this.contentLine = contentLineVal;
            }
            Object contentVal = map.get("content");
            if (contentVal instanceof Map<?, ?> m) {
                @SuppressWarnings("unchecked")
                Map<String, ?> typed = (Map<String, ?>) m;
                this.content(b -> b.load(typed));
            }
            return this;
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

        private static @Nullable List<String> getStringListOrSingle(ConfigurationSection section, String... keys) {
            for (String key : keys) {
                if (section.isList(key)) {
                    return section.getStringList(key);
                } else if (section.isString(key)) {
                    return List.of(section.getString(key));
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

        private static @Nullable List<String> getMapStringListOrSingle(Map<String, ?> map, String... keys) {
            for (String key : keys) {
                Object val = map.get(key);
                if (val instanceof List<?> list) {
                    List<String> res = new ArrayList<>();
                    for (Object elem : list) {
                        if (elem != null) {
                            res.add(elem.toString());
                        }
                    }
                    return res;
                } else if (val instanceof String s) {
                    return List.of(s);
                }
            }
            return null;
        }

        /**
         * Sets explicit static content options. Pass {@code null} to inherit from parent.
         */
        public Builder content(@Nullable ItemTextOptions content) {
            this.content = content;
            return this;
        }

        /**
         * Sets a content configurator that overrides only explicitly specified values
         * on the parent {@link ItemTextOptions} at render time.
         */
        public Builder contentModifier(@Nullable UnaryOperator<ItemTextOptions.Builder> contentModifier) {
            this.contentModifier = contentModifier;
            return this;
        }

        /**
         * Configures content options using a builder configurator.
         * Only the values modified by the configurator will override the parent options;
         * all other values fall back to the parent {@link ItemTextOptions}.
         */
        public Builder content(@Nullable UnaryOperator<ItemTextOptions.Builder> configurator) {
            if (configurator == null) {
                this.contentModifier = null;
                return this;
            }
            if (this.contentModifier == null) {
                this.contentModifier = configurator;
            } else {
                UnaryOperator<ItemTextOptions.Builder> prev = this.contentModifier;
                this.contentModifier = b -> configurator.apply(prev.apply(b));
            }
            return this;
        }

        public Builder emptySlot(String emptySlot) {
            this.emptySlot = Objects.requireNonNull(emptySlot, "emptySlot");
            return this;
        }

        public Builder separator(String separator) {
            this.separator = Objects.requireNonNull(separator, "separator");
            return this;
        }

        public Builder emptyMessage(String emptyMessage) {
            this.emptyMessage = Objects.requireNonNull(emptyMessage, "emptyMessage");
            return this;
        }

        public Builder maxLines(int maxLines) {
            this.maxLines = maxLines;
            return this;
        }

        public Builder contentLine(String contentLine) {
            this.contentLine = Objects.requireNonNull(contentLine, "contentLine");
            return this;
        }

        public ContentLoreOptions build() {
            return new ContentLoreOptions(this);
        }
    }
}
