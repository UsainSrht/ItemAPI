package me.usainsrht.itemapi.itemtext;

import org.jetbrains.annotations.NotNull;

/**
 * Enum representing the display mode for container content lore preview.
 */
public enum ContentLoreMode {
    TOTAL_STACK,
    GUI_VIEW;

    /**
     * Parses a raw string into a {@link ContentLoreMode}. Accepts case-insensitive values like
     * "TOTAL_STACK", "total-stack", "GUI_VIEW", "gui-view". Returns {@link #TOTAL_STACK} for unknown inputs.
     */
    @NotNull
    public static ContentLoreMode fromString(@NotNull String raw) {
        String normalized = raw.trim().toUpperCase().replace('-', '_');
        try {
            return ContentLoreMode.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            return TOTAL_STACK;
        }
    }
}
