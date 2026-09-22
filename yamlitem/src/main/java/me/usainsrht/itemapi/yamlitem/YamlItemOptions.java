package me.usainsrht.itemapi.yamlitem;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.UnaryOperator;

/**
 * Configuration options for parsing YAML items with {@link YamlItem} or {@link YamlItemParser}.
 */
public final class YamlItemOptions {

    private static final YamlItemOptions DEFAULT = new YamlItemOptions(TagResolver.empty(), null, null);

    private final TagResolver tagResolver;
    private final @Nullable UnaryOperator<String> stringPreprocessor;
    private final @Nullable MiniMessage miniMessage;

    private YamlItemOptions(TagResolver tagResolver,
                            @Nullable UnaryOperator<String> stringPreprocessor,
                            @Nullable MiniMessage miniMessage) {
        this.tagResolver = Objects.requireNonNull(tagResolver, "tagResolver");
        this.stringPreprocessor = stringPreprocessor;
        this.miniMessage = miniMessage;
    }

    public static YamlItemOptions empty() {
        return DEFAULT;
    }

    public static YamlItemOptions of(TagResolver... resolvers) {
        if (resolvers == null || resolvers.length == 0) {
            return DEFAULT;
        }
        return builder().tagResolvers(resolvers).build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public TagResolver tagResolver() {
        return tagResolver;
    }

    public @Nullable UnaryOperator<String> stringPreprocessor() {
        return stringPreprocessor;
    }

    public @Nullable MiniMessage miniMessage() {
        return miniMessage;
    }

    public static final class Builder {
        private TagResolver tagResolver = TagResolver.empty();
        private @Nullable UnaryOperator<String> stringPreprocessor;
        private @Nullable MiniMessage miniMessage;

        public Builder tagResolver(TagResolver tagResolver) {
            this.tagResolver = Objects.requireNonNull(tagResolver, "tagResolver");
            return this;
        }

        public Builder tagResolvers(TagResolver... resolvers) {
            if (resolvers == null || resolvers.length == 0) {
                this.tagResolver = TagResolver.empty();
            } else {
                this.tagResolver = TagResolver.resolver(resolvers);
            }
            return this;
        }

        public Builder stringPreprocessor(@Nullable UnaryOperator<String> stringPreprocessor) {
            this.stringPreprocessor = stringPreprocessor;
            return this;
        }

        public Builder miniMessage(@Nullable MiniMessage miniMessage) {
            this.miniMessage = miniMessage;
            return this;
        }

        public YamlItemOptions build() {
            if (tagResolver == TagResolver.empty() && stringPreprocessor == null && miniMessage == null) {
                return DEFAULT;
            }
            return new YamlItemOptions(tagResolver, stringPreprocessor, miniMessage);
        }
    }
}
