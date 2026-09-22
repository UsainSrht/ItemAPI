package me.usainsrht.itemapi.itemtext;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.object.ObjectContents;
import net.kyori.adventure.text.object.PlayerHeadObjectContents;
import net.kyori.adventure.text.object.SpriteObjectContents;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jspecify.annotations.Nullable;

final class ItemSpriteFactory {

    private static final Key ITEMS_ATLAS = Key.key("minecraft", "items");
    private static final Key BLOCKS_ATLAS = SpriteObjectContents.DEFAULT_ATLAS;
    private static final Key STEVE_SKIN = Key.key("minecraft", "entity/player/wide/steve");

    private ItemSpriteFactory() {
    }

    static Component create(ItemStack item) {
        return create(item, ItemText.defaultOptions());
    }

    static Component create(ItemStack item, ItemTextOptions options) {
        Material material = item.getType();
        if (material == Material.PLAYER_HEAD || material == Material.PLAYER_WALL_HEAD) {
            return playerHead(item);
        }

        SpriteTarget target = resolve(item, options);
        if (target instanceof HeadTextureRegistry.HeadRef head) {
            return Component.object(ObjectContents.playerHead()
                    .id(head.id())
                    .profileProperty(PlayerHeadObjectContents.property("textures", head.base64Texture()))
                    .hat(true)
                    .build());
        }

        ItemSpriteOverrides.SpriteRef sprite = (ItemSpriteOverrides.SpriteRef) target;
        return Component.object(ObjectContents.sprite(sprite.atlas(), sprite.sprite()));
    }

    /**
     * Player heads use Adventure's player-head object (not an atlas sprite).
     * With a profile this shows that skin; without one it falls back to Steve.
     */
    private static Component playerHead(ItemStack item) {
        ResolvableProfile profile = getProfile(item);
        if (profile != null) {
            // ResolvableProfile is a SkinSource — preserves uuid/name/properties
            return Component.object(ObjectContents.playerHead(profile));
        }
        return Component.object(ObjectContents.playerHead().texture(STEVE_SKIN).build());
    }

    private static @Nullable ResolvableProfile getProfile(ItemStack item) {
        try {
            return item.getData(DataComponentTypes.PROFILE);
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static @Nullable Key getItemModel(ItemStack item) {
        try {
            return item.getData(DataComponentTypes.ITEM_MODEL);
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static SpriteTarget resolve(ItemStack item, ItemTextOptions options) {
        Key itemModel = getItemModel(item);
        if (itemModel != null) {
            return fromModelKey(itemModel, options);
        }

        return fromItemKey(itemKey(item), options);
    }

    private static Key itemKey(ItemStack item) {
        try {
            ItemType itemType = item.getType().asItemType();
            if (itemType != null) {
                return itemType.key();
            }
        } catch (Throwable ignored) {
        }
        return item.getType().getKey();
    }

    private static SpriteTarget fromItemKey(Key itemKey, ItemTextOptions options) {
        String name = itemKey.value();

        if (options.usePlayerHeadsFor3DBlocks()) {
            HeadTextureRegistry.HeadRef head = HeadTextureRegistry.find(name);
            if (head != null) {
                return head;
            }
        }

        ItemSpriteOverrides.SpriteRef override = ItemSpriteOverrides.sprite(name);
        if (override != null) {
            return override;
        }

        if (ItemAtlasSprites.contains(name)) {
            return itemSprite(itemKey.namespace(), name);
        }

        String blockSprite = BlockSpriteLookup.resolve(name);
        return blockSprite(itemKey.namespace(), blockSprite);
    }

    private static SpriteTarget fromModelKey(Key itemModel, ItemTextOptions options) {
        String value = itemModel.value();
        if (value.startsWith("item/")) {
            return new ItemSpriteOverrides.SpriteRef(ITEMS_ATLAS, itemModel);
        }
        if (value.startsWith("block/")) {
            return new ItemSpriteOverrides.SpriteRef(BLOCKS_ATLAS, itemModel);
        }

        if (options.usePlayerHeadsFor3DBlocks()) {
            HeadTextureRegistry.HeadRef head = HeadTextureRegistry.find(value);
            if (head != null) {
                return head;
            }
        }

        ItemSpriteOverrides.SpriteRef override = ItemSpriteOverrides.sprite(value);
        if (override != null) {
            return override;
        }

        if (ItemAtlasSprites.contains(value)) {
            return itemSprite(itemModel.namespace(), value);
        }

        String blockSprite = BlockSpriteLookup.resolve(value);
        return blockSprite(itemModel.namespace(), blockSprite);
    }

    private static ItemSpriteOverrides.SpriteRef itemSprite(String namespace, String name) {
        return new ItemSpriteOverrides.SpriteRef(ITEMS_ATLAS, Key.key(namespace, "item/" + name));
    }

    private static ItemSpriteOverrides.SpriteRef blockSprite(String namespace, String name) {
        return new ItemSpriteOverrides.SpriteRef(BLOCKS_ATLAS, Key.key(namespace, "block/" + name));
    }
}
