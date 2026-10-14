package io.papermc.paper.datacomponent.item;

import com.destroystokyo.paper.profile.PlayerProfile;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.text.Filtered;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.util.TriState;
import org.bukkit.JukeboxSong;
import org.bukkit.block.BlockType;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.map.MapCursor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public class TestItemComponentTypesBridge implements ItemComponentTypesBridge {

    public TestItemComponentTypesBridge() {
    }

    @Override
    public MapDecorations.Builder mapDecorations() {
        return createMapDecorationsBuilder();
    }

    @SuppressWarnings("unchecked")
    private static MapDecorations.Builder createMapDecorationsBuilder() {
        Map<String, MapDecorations.DecorationEntry> entries = new HashMap<>();
        InvocationHandler handler = (proxy, method, args) -> {
            String name = method.getName();
            if (name.equals("put")) {
                entries.put((String) args[0], (MapDecorations.DecorationEntry) args[1]);
                return proxy;
            }
            if (name.equals("putAll")) {
                if (args[0] != null) {
                    entries.putAll((Map<String, MapDecorations.DecorationEntry>) args[0]);
                }
                return proxy;
            }
            if (name.equals("build")) {
                return createMapDecorations(entries);
            }
            return null;
        };
        return (MapDecorations.Builder) Proxy.newProxyInstance(
                MapDecorations.Builder.class.getClassLoader(),
                new Class<?>[]{MapDecorations.Builder.class},
                handler
        );
    }

    private static MapDecorations createMapDecorations(Map<String, MapDecorations.DecorationEntry> entries) {
        Map<String, MapDecorations.DecorationEntry> copy = Map.copyOf(entries);
        InvocationHandler handler = (proxy, method, args) -> {
            String name = method.getName();
            if (name.equals("decorations")) {
                return copy;
            }
            if (name.equals("decoration")) {
                return copy.get(args[0]);
            }
            if (name.equals("toString")) {
                return "MapDecorations" + copy;
            }
            if (name.equals("equals")) {
                return proxy == args[0];
            }
            if (name.equals("hashCode")) {
                return copy.hashCode();
            }
            return null;
        };
        return (MapDecorations) Proxy.newProxyInstance(
                MapDecorations.class.getClassLoader(),
                new Class<?>[]{MapDecorations.class},
                handler
        );
    }

    @Override public ChargedProjectiles.Builder chargedProjectiles() { return null; }
    @Override public PotDecorations.Builder potDecorations() { return null; }
    @Override public ItemLore.Builder lore() { return null; }
    @Override public ItemEnchantments.Builder enchantments() { return null; }
    @Override public ItemAttributeModifiers.Builder modifiers() { return null; }
    @Override public FoodProperties.Builder food() { return null; }
    @Override public DyedItemColor.Builder dyedItemColor() { return null; }
    @Override public PotionContents.Builder potionContents() { return null; }
    @Override public BundleContents.Builder bundleContents() { return null; }
    @Override public SuspiciousStewEffects.Builder suspiciousStewEffects() { return null; }
    @Override public MapDecorations.DecorationEntry mapDecorationEntry(MapCursor.Type type, double x, double z, float rotation) { return null; }
    @Override public SeededContainerLoot.Builder seededContainerLoot(Key key) { return null; }
    @Override public WrittenBookContent.Builder writtenBookContent(Filtered<String> title, String author) { return null; }
    @Override public WritableBookContent.Builder writeableBookContent() { return null; }
    @Override public ItemArmorTrim.Builder itemArmorTrim(ArmorTrim trim) { return null; }
    @Override public LodestoneTracker.Builder lodestoneTracker() { return null; }
    @Override public Fireworks.Builder fireworks() { return null; }
    @Override public ResolvableProfile resolvableProfile(PlayerProfile profile) { return null; }
    @Override public ResolvableProfile.Builder resolvableProfile() { return null; }
    @Override public ResolvableProfile.SkinPatchBuilder skinPatch() { return null; }
    @Override public ResolvableProfile.SkinPatch emptySkinPatch() { return null; }
    @Override public BannerPatternLayers.Builder bannerPatternLayers() { return null; }
    @Override public BlockItemDataProperties.Builder blockItemStateProperties() { return null; }
    @Override public ItemContainerContents.Builder itemContainerContents() { return null; }
    @Override public JukeboxPlayable.Builder jukeboxPlayable(JukeboxSong song) { return null; }
    @Override public Tool.Builder tool() { return null; }
    @Override public Tool.Rule toolRule(RegistryKeySet<BlockType> blocks, Float speed, TriState correctForDrops) { return null; }
    @Override public ItemAdventurePredicate.Builder itemAdventurePredicate() { return null; }
    @Override public CustomModelData.Builder customModelData() { return null; }
    @Override public MapId mapId(int id) { return null; }
    @Override public UseRemainder useRemainder(ItemStack stack) { return null; }
    @Override public Consumable.Builder consumable() { return null; }
    @Override public UseCooldown.Builder useCooldown(float seconds) { return null; }
    @Override public DamageResistant damageResistant(RegistryKeySet<DamageType> damageTypes) { return null; }
    @Override public Enchantable enchantable(int value) { return null; }
    @Override public Repairable repairable(RegistryKeySet<ItemType> items) { return null; }
    @Override public Equippable.Builder equippable(EquipmentSlot slot) { return null; }
    @Override public DeathProtection.Builder deathProtection() { return null; }
    @Override public OminousBottleAmplifier ominousBottleAmplifier(int amplifier) { return null; }
    @Override public BlocksAttacks.Builder blocksAttacks() { return null; }
    @Override public TooltipDisplay.Builder tooltipDisplay() { return null; }
    @Override public Weapon.Builder weapon() { return null; }
    @Override public KineticWeapon.Builder kineticWeapon() { return null; }
    @Override public KineticWeapon.Condition kineticWeaponCondition(int delay, float force, float damage) { return null; }
    @Override public UseEffects.Builder useEffects() { return null; }
    @Override public PiercingWeapon.Builder piercingWeapon() { return null; }
    @Override public AttackRange.Builder attackRange() { return null; }
    @Override public SwingAnimation.Builder swingAnimation() { return null; }
    @Override public SulfurCubeContent sulfurCubeContent(ItemStack stack) { return null; }
    @Override public MobVisibility mobVisibility(RegistryKeySet<EntityType> entities, float range) { return null; }
    @Override public VillagerFood villagerFood(int food) { return null; }
    @Override public SignText.Builder signText() { return null; }
}
