package me.usainsrht.itemapi.itemtext;

import org.jspecify.annotations.Nullable;

/**
 * Enum representing the display mode for container content lore preview.
 */
public enum ContentLoreMode {
    TOTAL_STACK,
    GUI_VIEW;

    /**
     * Parses a raw string into a {@link ContentLoreMode}. Accepts case-insensitive values like
     * "TOTAL_STACK", "total-stack", "GUI_VIEW", "gui-view". Returns {@link #TOTAL_STACK} for unknown or null inputs.
     */
    public static ContentLoreMode fromString(@Nullable String raw) {
        return fromString(raw, TOTAL_STACK);
    }

    /**
     * Parses a raw string into a {@link ContentLoreMode}, or returns {@code fallback} if invalid or null.
     */
    public static ContentLoreMode fromString(@Nullable String raw, ContentLoreMode fallback) {
        if (raw == null || raw.isBlank()) {
            return fallback;
        }
        String normalized = raw.trim().toUpperCase().replace('-', '_');
        try {
            return ContentLoreMode.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            return fallback;
        }
    }
}
