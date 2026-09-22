package me.usainsrht.itemapi.itemtext;

/**
 * Common target representation for item visuals: either an atlas sprite or a player head skin.
 */
sealed interface SpriteTarget permits ItemSpriteOverrides.SpriteRef, HeadTextureRegistry.HeadRef {
}
