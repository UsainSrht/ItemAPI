package me.usainsrht.itemapi.itemtext;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.object.ObjectContents;
import net.kyori.adventure.text.object.SpriteObjectContents;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;

final class ItemSpriteFactory {

    private static final Key ITEMS_ATLAS = Key.key("minecraft", "items");
    private static final Key BLOCKS_ATLAS = SpriteObjectContents.DEFAULT_ATLAS;
    private static final Key STEVE_SKIN = Key.key("minecraft", "entity/player/wide/steve");

    private ItemSpriteFactory() {
    }

    static Component create(ItemStack item) {
        Material material = item.getType();
        if (material == Material.PLAYER_HEAD || material == Material.PLAYER_WALL_HEAD) {
            return playerHead(item);
        }

        ItemSpriteOverrides.SpriteRef sprite = resolve(item);
        return Component.object(ObjectContents.sprite(sprite.atlas(), sprite.sprite()));
    }

    /**
     * Player heads use Adventure's player-head object (not an atlas sprite).
     * With a profile this shows that skin; without one it falls back to Steve.
     */
    private static Component playerHead(ItemStack item) {
        ResolvableProfile profile = item.getData(DataComponentTypes.PROFILE);
        if (profile != null) {
            // ResolvableProfile is a SkinSource — preserves uuid/name/properties
            return Component.object(ObjectContents.playerHead(profile));
        }
        return Component.object(ObjectContents.playerHead().texture(STEVE_SKIN).build());
    }

    private static ItemSpriteOverrides.SpriteRef resolve(ItemStack item) {
        Key itemModel = item.getData(DataComponentTypes.ITEM_MODEL);
        if (itemModel != null) {
            return fromModelKey(itemModel);
        }

        return fromItemKey(itemKey(item));
    }

    private static Key itemKey(ItemStack item) {
        ItemType itemType = item.getType().asItemType();
        if (itemType != null) {
            return itemType.key();
        }
        return item.getType().getKey();
    }

    private static ItemSpriteOverrides.SpriteRef fromItemKey(Key itemKey) {
        String name = itemKey.value();

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

    private static ItemSpriteOverrides.SpriteRef fromModelKey(Key itemModel) {
        String value = itemModel.value();
        if (value.startsWith("item/")) {
            return new ItemSpriteOverrides.SpriteRef(ITEMS_ATLAS, itemModel);
        }
        if (value.startsWith("block/")) {
            return new ItemSpriteOverrides.SpriteRef(BLOCKS_ATLAS, itemModel);
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
