package me.usainsrht.itemapi.yamlitem.internal;

import org.bukkit.Bukkit;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class ItemLogger {

    private static final Logger LOGGER = Logger.getLogger("ItemAPI");

    private ItemLogger() {
    }

    public static void warn(String message) {
        try {
            if (Bukkit.getServer() != null) {
                Bukkit.getLogger().warning("[ItemAPI] " + message);
                return;
            }
        } catch (Throwable ignored) {
        }
        LOGGER.warning("[ItemAPI] " + message);
    }

    public static void warn(String message, Throwable throwable) {
        try {
            if (Bukkit.getServer() != null) {
                Bukkit.getLogger().log(Level.WARNING, "[ItemAPI] " + message, throwable);
                return;
            }
        } catch (Throwable ignored) {
        }
        LOGGER.log(Level.WARNING, "[ItemAPI] " + message, throwable);
    }
}
