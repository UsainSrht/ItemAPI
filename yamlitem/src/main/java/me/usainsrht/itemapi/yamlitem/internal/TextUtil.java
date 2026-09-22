package me.usainsrht.itemapi.yamlitem.internal;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jspecify.annotations.Nullable;

public final class TextUtil {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private TextUtil() {
    }

    public static Component miniMessage(String input) {
        return deserialize(null, input, null);
    }

    public static Component miniMessage(String input, @Nullable TagResolver tagResolver) {
        return deserialize(null, input, tagResolver);
    }

    public static Component miniMessageNullable(@Nullable String input) {
        if (input == null) {
            return null;
        }
        return miniMessage(input);
    }

    public static Component deserialize(@Nullable MiniMessage miniMessage, String input, @Nullable TagResolver tagResolver) {
        if (input == null) {
            return Component.empty();
        }
        MiniMessage mm = miniMessage != null ? miniMessage : MINI_MESSAGE;
        Component component = (tagResolver != null && tagResolver != TagResolver.empty())
                ? mm.deserialize(input, tagResolver)
                : mm.deserialize(input);
        return component.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }
}
