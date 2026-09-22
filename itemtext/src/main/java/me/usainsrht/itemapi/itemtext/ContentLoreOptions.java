package me.usainsrht.itemapi.itemtext;

import org.jspecify.annotations.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
     * parent
     * {@link ItemTextOptions} (with hover and content-lore disabled).
     */
    public @Nullable ItemTextOptions content() {
        return content;
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
     * If {@link #content()} is non-null it is returned as-is.
     * Otherwise {@code parentOptions} is used with hover and content-lore disabled
     * to prevent recursion.
     * </p>
     */
    ItemTextOptions resolveContent(ItemTextOptions parentOptions) {
        if (content != null) {
            return content;
        }
        return parentOptions.toBuilder()
                .hoverEnabled(false)
                .contentLoreEnabled(false)
                .build();
    }

    public Builder toBuilder() {
        return new Builder()
                .enabled(enabled)
                .mode(mode)
                .header(header)
                .footer(footer)
                .content(content)
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
                .of(" <white><!italic><translate:entity.minecraft.sulfur_cube.content:'<item>'>");
        private List<String> footer = List.of();
        /** {@code null} = inherit from parent ItemTextOptions at render time. */
        private @Nullable ItemTextOptions content = null;
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

        public Builder header(List<String> header) {
            this.header = new ArrayList<>(header);
            return this;
        }

        public Builder footer(List<String> footer) {
            this.footer = new ArrayList<>(footer);
            return this;
        }

        /**
         * Sets explicit content options. Pass {@code null} to inherit from parent.
         */
        public Builder content(@Nullable ItemTextOptions content) {
            this.content = content;
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
