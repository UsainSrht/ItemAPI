package me.usainsrht.itemapi.yamlitem.internal;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;

public final class TextUtil {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private TextUtil() {
    }

    public static Component miniMessage(String input) {
        Component component = MINI_MESSAGE.deserialize(input);
        return component.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }

    public static Component miniMessageNullable(String input) {
        if (input == null) {
            return null;
        }
        return miniMessage(input);
    }
}
