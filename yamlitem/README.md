# YamlItem

Shadeable API that parses Paper `ItemStack`s from YAML (`ConfigurationSection` or `Map`) using modern [`DataComponentTypes`](https://jd.papermc.io/paper/io/papermc/paper/datacomponent/DataComponentTypes.html).

Target: **Paper 1.21.4+ / Paper 26.x** data components.

---

## Quick start

```java
import me.usainsrht.itemapi.yamlitem.YamlItem;
import me.usainsrht.itemapi.yamlitem.YamlParseException;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

ConfigurationSection section = config.getConfigurationSection("cool_sword");
try {
    ItemStack item = YamlItem.parse(section);
    // or: YamlItem.parse(map);
} catch (YamlParseException ex) {
    getLogger().warning(ex.getMessage());
}
```

Maven:

```xml
<dependency>
    <groupId>me.usainsrht.itemapi</groupId>
    <artifactId>yamlitem</artifactId>
    <version>1.0.3</version>
</dependency>
```

---

## How parsing works

1. Resolve `material` / `type` / `item` and `amount` / `count`.
2. Apply **root shortcuts** (`name`, `lore`, `enchantments`, …).
3. Apply entries under `components:` (any registered `DataComponentType` id).
4. Root shortcuts **win** over the same key under `components`.

Component keys accept `minecraft:custom_name`, `custom_name`, or `custom-name`.  
Prefix a key with `!` to unset a component (`!unbreakable: true`).

Text fields use **MiniMessage**.

---

## Root shortcuts

| Key | Component / meaning |
| --- | --- |
| `material` / `type` / `item` | Base material / item key |
| `amount` / `count` | Stack size (default `1`) |
| `name` / `custom_name` | `custom_name` |
| `item_name` | `item_name` |
| `lore` | `lore` |
| `enchantments` | `enchantments` |
| `stored_enchantments` | `stored_enchantments` |
| `unbreakable` | `unbreakable` |
| `glint` / `enchantment_glint_override` | `enchantment_glint_override` |
| `hide_tooltip` | `tooltip_display.hide_tooltip` |
| `damage` / `max_damage` | durability |
| `max_stack_size` | stack limit |
| `custom_model_data` | model data |
| `rarity` | `common` / `uncommon` / `rare` / `epic` |
| `item_model` | model key |
| `repair_cost` | anvil cost |

Any other root key that matches a component id is also treated as a shortcut.

```yaml
cool_sword:
  material: diamond_sword
  name: "<red><bold>Cool Sword"
  lore:
    - "<gray>Root shortcuts example"
  enchantments:
    sharpness: 5
    unbreaking: 3
  unbreakable: true
  glint: true
```

---

## Shortcuts & tricks

Patterns the parser actually supports (string / bare-value forms, aliases, root component keys). Prefer these over verbose maps when you do not need extra fields.

### Vanilla potion types (tipped arrows, potions, splash, lingering)

`potion_contents` accepts a potion registry id string. Tipped arrows keep vanilla’s 1/8 duration scale, so survival craftable arrows match without custom effects:

```yaml
# Survival Weakness tipped arrow (~0:11)
weakness_arrow:
  material: tipped_arrow
  potion_contents: weakness

# Extended Weakness arrow (~0:30)
long_weakness_arrow:
  material: tipped_arrow
  potion_contents: long_weakness

healing_potion:
  material: potion
  potion_contents: strong_healing

splash_poison:
  material: splash_potion
  potion_contents: poison
```

Map form when you need color / custom effects too:

```yaml
custom_arrow:
  material: tipped_arrow
  potion_contents:
    potion: weakness          # alias: type
    color: "#4E9331"          # alias: custom_color
    effects:                  # alias: custom_effects
      - type: weakness        # alias: effect
        duration: 220         # ticks (absolute; not auto-scaled)
        amplifier: 0
```

### Bare values (skip the map)

| Component | Bare form | Equivalent |
| --- | --- | --- |
| `potion_contents` | `weakness` | `{ potion: weakness }` |
| `profile` | `Steve` or a UUID string | `{ name: ... }` / `{ uuid: ... }` |
| `jukebox_playable` | `cat` | `{ song: cat }` |
| `use_cooldown` | `1.5` | `{ seconds: 1.5 }` |
| `use_remainder` | `bowl` | nested item `{ material: bowl }` |
| `custom_model_data` | `42` | `{ floats: [42] }` |
| `enchantable` | `15` | `{ value: 15 }` |
| `swing_animation` | `stab` | `{ type: stab }` |
| `recipes` | `minecraft:diamond_sword` | single-element list |
| `repairable` | `iron_ingot` | `[iron_ingot]` |
| `damage_resistant` | `minecraft:on_fire` | `{ types: [minecraft:on_fire] }` |
| `ominous_bottle_amplifier` | `3` | — |

```yaml
disc:
  material: music_disc_13
  jukebox_playable: 13

head:
  material: player_head
  profile: UsainSrht

snack:
  material: cooked_beef
  food:
    nutrition: 8
    saturation: 12.8
  use_cooldown: 2.5
  use_remainder: bowl

book_pages:
  material: writable_book
  writable_book_content:      # bare page list (no pages: wrapper)
    - "Page one"
    - "Page two"

totem:
  material: totem_of_undying
  death_protection:           # bare consume-effect list
    - type: clear_all
    - type: apply_effects
      effects:
        - type: regeneration
          duration: 900
          amplifier: 1
```

### Nested items as material names

`bundle_contents`, `container`, `charged_projectiles`, `use_remainder`, and `sulfur_cube_content` accept a material string or a full item map:

```yaml
loaded_crossbow:
  material: crossbow
  charged_projectiles:
    - tipped_arrow
    - material: tipped_arrow
      potion_contents: weakness

loot_bundle:
  material: bundle
  bundle_contents:
    - diamond
    - material: golden_apple
      amount: 2
      name: "<gold>Snack"
```

### Root-level component keys

Any registered component id works at the root (same as under `components:`). Root wins if both are set. Kebab-case and `minecraft:` prefixes are fine:

```yaml
shiny_pick:
  material: diamond_pickaxe
  name: "<aqua>Shiny"
  lore: "<gray>Single-line lore also works"   # non-list → one lore line
  enchantments:
    efficiency: 5
  enchantment-glint-override: true
  minecraft:max_stack_size: 1
  hide_tooltip: true
```

### Handy aliases

| Prefer | Also accepted |
| --- | --- |
| `name` | `custom_name` |
| `glint` | `enchantment_glint_override` |
| `amount` | `count` |
| `material` | `type`, `item` |
| `potion` / `type` | in `potion_contents` |
| `effects` | `custom_effects` (potions), `explosions` (fireworks) |
| `color` | `custom_color` (potions) |
| `flight` | `flight_duration` |
| `fade` | `fade_colors` |
| `group` | `cooldown_group` |
| `song` | `jukebox_song` |
| `attribute` / `type` | in attribute modifiers |
| `enchantment` / `id` | in enchantment list entries |
| `slot` / `slot_group` | in attribute modifiers |
| `particles` | `has_consume_particles` |

### Colors that work

`ValueUtil` colors (`dyed_color`, `map_color`, firework colors, potion `custom_color`) accept `#RRGGBB`, bare hex, `r,g,b`, or an RGB int — **not** named colors like `red`.

`DyeColor` fields (`dye`, `base_color`, collars, banner pattern `color`, …) do accept names: `blue`, `white`, …

```yaml
leather:
  material: leather_chestplate
  dyed_color: "#C45C26"

shield:
  material: shield
  base_color: red              # DyeColor enum — OK
```

### Lodestone without a nested `location`

```yaml
compass:
  material: compass
  lodestone_tracker:
    tracked: true
    world: world
    x: 100
    y: 64
    z: -200
```

---

## Component examples

Keys below may be used at the root (when they match a component id) or under `components:`.

### Flags (non-valued)

```yaml
flags_demo:
  material: elytra
  components:
    unbreakable: true
    glider: true
    intangible_projectile: true
```

### Scalars

```yaml
scalars_demo:
  material: diamond_pickaxe
  components:
    max_stack_size: 16
    max_damage: 500
    damage: 12
    repair_cost: 5
    minimum_attack_charge: 0.5
    potion_duration_scale: 1.25
    enchantment_glint_override: true
```

### Text

```yaml
text_demo:
  material: paper
  components:
    custom_name: "<gold>Custom Name"
    item_name: "<yellow>Item Name"
    lore:
      - "<gray>Line one"
      - "<dark_gray>Line two"
```

### Keys / sounds / models

```yaml
keys_demo:
  material: note_block
  components:
    item_model: minecraft:diamond
    tooltip_style: minecraft:default
    break_sound: minecraft:block.glass.break
    note_block_sound: minecraft:entity.pig.ambient
```

### Enums & registry values

```yaml
enums_demo:
  material: filled_map
  components:
    rarity: epic
    map_post_processing: lock
    damage_type: minecraft:player_attack
    instrument: ponder_goat_horn
    provides_trim_material: iron
    painting_variant: kebab
```

### Colors & dyes

```yaml
colors_demo:
  material: leather_chestplate
  components:
    dyed_color: "#FF55AA"          # hex / rgb int / "r,g,b" (not named colors)
    map_color: "#FF0000"
    dye: blue                      # DyeColor name — OK
    base_color: white              # shields / banners (DyeColor)
    cat_collar: cyan
    wolf_collar: orange
    sheep_color: pink
    shulker_color: purple
    tropical_fish_base_color: red
    tropical_fish_pattern_color: yellow
```

`dyed_color` / `map_color` also accept maps:

```yaml
  dyed_color:
    rgb: "#112233"
  map_color:
    color: "#00FFFF"
```

### Enchantments

```yaml
enchantments_demo:
  material: diamond_sword
  components:
    enchantments:
      sharpness: 5
      unbreaking: 3
    enchantable: 15                # or { value: 15 }

enchanted_book_demo:
  material: enchanted_book
  components:
    stored_enchantments:
      - enchantment: mending
        level: 1
      - id: efficiency
        level: 4
```

### Custom model data

```yaml
cmd_number:
  material: stick
  custom_model_data: 42

cmd_object:
  material: stick
  components:
    custom_model_data:
      floats: [1.0, 2.5]
      flags: [true, false]
      strings: ["layer_a", "layer_b"]
      colors: ["#FF0000", "#0000FF"]
```

### Food, consumable, use

```yaml
food_demo:
  material: cooked_beef
  components:
    food:
      nutrition: 8
      saturation: 12.8
      can_always_eat: false
    consumable:
      consume_seconds: 1.6
      animation: eat
      sound: minecraft:entity.generic.eat
      has_consume_particles: true
      effects:
        - type: apply_effects
          probability: 1.0
          effects:
            - type: regeneration
              duration: 100
              amplifier: 1
        - type: remove_effects
          effects: [poison, wither]
        - type: clear_all_status_effects
        - type: teleport_randomly
          diameter: 16
        - type: play_sound
          sound: minecraft:entity.player.levelup
    use_cooldown:
      seconds: 2.5
      cooldown_group: myplugin:special_food
    use_effects:
      can_sprint: true
      interact_vibrations: true
      speed_multiplier: 0.2
    use_remainder: bowl            # material name, or nested item map
```

`use_cooldown` also accepts a bare number: `use_cooldown: 1.5`.

### Weapon & tool

```yaml
weapon_demo:
  material: iron_sword
  components:
    weapon:
      item_damage_per_attack: 1
      disable_blocking_for_seconds: 0.5
    attack_range:
      min_reach: 0
      max_reach: 4
      min_creative_reach: 0
      max_creative_reach: 6
      hitbox_margin: 0.1
      mob_factor: 1.0
    piercing_weapon:
      deals_knockback: true
      dismounts: false
      sound: minecraft:item.trident.throw
      hit_sound: minecraft:item.trident.hit
    kinetic_weapon:
      contact_cooldown_ticks: 10
      delay_ticks: 5
      forward_movement: 0.5
      damage_multiplier: 1.2
      dismount_conditions:
        max_duration_ticks: 20
        min_speed: 0.1
        min_relative_speed: 0.05
      knockback_conditions:
        max_duration_ticks: 20
        min_speed: 0.1
        min_relative_speed: 0.05
      damage_conditions:
        max_duration_ticks: 40
        min_speed: 0.2
        min_relative_speed: 0.1
    swing_animation:
      type: stab
      duration: 6
    blocks_attacks:
      block_delay_seconds: 0.25
      disable_cooldown_scale: 1.0
      block_sound: minecraft:item.shield.block
      disable_sound: minecraft:item.shield.break
      bypassed_by: [minecraft:bypass_shield]
      item_damage:
        threshold: 0
        base: 1
        factor: 1
      damage_reductions:
        - types: [minecraft:player_attack]
          horizontal_blocking_angle: 90
          base: 0
          factor: 1

tool_demo:
  material: netherite_pickaxe
  components:
    tool:
      default_mining_speed: 1.0
      damage_per_block: 1
      can_destroy_blocks_in_creative: true
      rules:
        - blocks: "#minecraft:mineable/pickaxe"
          speed: 12.0
          correct_for_drops: true
```

### Equippable, repairable, damage resistant

```yaml
equippable_demo:
  material: iron_chestplate
  components:
    equippable:
      slot: chest
      equip_sound: minecraft:item.armor.equip_iron
      asset_id: minecraft:iron
      camera_overlay: minecraft:misc/pumpkinblur
      allowed_entities: [player]
      dispensable: true
      swappable: true
      damage_on_hurt: true
      equip_on_interact: false
      can_be_sheared: false
      shear_sound: minecraft:item.shears.shear
    repairable: [iron_ingot, iron_block]
    damage_resistant:
      types: [minecraft:on_fire, minecraft:in_fire]
```

`repairable` / `damage_resistant` also accept a bare list or tag key string.

### Attribute modifiers

```yaml
attributes_demo:
  material: diamond_sword
  components:
    attribute_modifiers:
      - attribute: attack_damage
        id: myplugin:bonus_damage
        amount: 2.0
        operation: ADD_NUMBER
        slot: hand
      - type: max_health
        key: myplugin:bonus_health
        value: 4.0
        operation: ADD_NUMBER
        slot_group: any
```

### Potions & stew

```yaml
potion_demo:
  material: potion
  components:
    potion_contents:
      potion: strong_healing
      custom_color: "#FF55FF"
      custom_name: "elixir"
      custom_effects:
        - type: speed
          duration: 200
          amplifier: 1
          ambient: false
          particles: true
          icon: true
    potion_duration_scale: 1.5

ominous_demo:
  material: ominous_bottle
  components:
    ominous_bottle_amplifier: 3

stew_demo:
  material: suspicious_stew
  components:
    suspicious_stew_effects:
      - effect: night_vision
        duration: 100
      - type: jump_boost
        duration: 80
```

### Profile (player heads)

```yaml
head_by_name:
  material: player_head
  components:
    profile: UsainSrht

head_by_uuid:
  material: player_head
  components:
    profile: "069a79f4-44e9-4726-a5be-fca90e38aaf5"

head_full:
  material: player_head
  components:
    profile:
      name: Steve
      uuid: "069a79f4-44e9-4726-a5be-fca90e38aaf5"
      properties:
        - name: textures
          value: "base64-texture-value"
          signature: "optional-signature"
```

### Fireworks

```yaml
firework_rocket_demo:
  material: firework_rocket
  components:
    fireworks:
      flight_duration: 2
      explosions:
        - type: BALL_LARGE
          colors: ["#FF0000", "#FFFF00"]
          fade_colors: ["#0000FF"]
          flicker: true
          trail: true

firework_star_demo:
  material: firework_star
  components:
    firework_explosion:
      type: CREEPER
      color: "#32CD32"
      fade: ["#000000"]
      flicker: false
      trail: true
```

### Armor trim & jukebox

```yaml
trim_demo:
  material: netherite_chestplate
  components:
    trim:
      material: gold
      pattern: silence

disc_demo:
  material: music_disc_cat
  components:
    jukebox_playable: cat
    # or: jukebox_playable: { song: cat }
```

### Tooltip display

```yaml
tooltip_demo:
  material: diamond
  hide_tooltip: false
  components:
    tooltip_display:
      hide_tooltip: false
      hidden_components:
        - enchantments
        - attribute_modifiers
```

### Lodestone compass

```yaml
lodestone_demo:
  material: compass
  components:
    lodestone_tracker:
      tracked: true
      location:
        world: world
        x: 100
        y: 64
        z: -200
```

### Nested items (bundle / container / projectiles)

```yaml
bundle_demo:
  material: bundle
  components:
    bundle_contents:
      - diamond
      - material: apple
        amount: 8
        name: "<red>Fancy Apple"

shulker_demo:
  material: shulker_box
  components:
    container:
      - material: gold_ingot
        amount: 16
      - emerald

crossbow_demo:
  material: crossbow
  components:
    charged_projectiles:
      - arrow
      - spectral_arrow

sulfur_demo:
  material: sulfur_cube   # use a valid material on your Paper version
  components:
    sulfur_cube_content:
      material: gunpowder
      amount: 4
```

### Container loot & maps

```yaml
loot_demo:
  material: chest
  components:
    container_loot:
      loot_table: minecraft:chests/simple_dungeon
      seed: 12345

map_demo:
  material: filled_map
  components:
    map_id: 1
    map_decorations:
      home:
        type: target_x
        x: 64.0
        z: -32.0
        rotation: 90
```

### Books

```yaml
writable_book_demo:
  material: writable_book
  components:
    writable_book_content:
      pages:
        - "Page one raw text"
        - "Page two raw text"

written_book_demo:
  material: written_book
  components:
    written_book_content:
      title: "Adventure Log"
      author: "Usain"
      generation: 0
      resolved: true
      pages:
        - "<green>Hello <bold>world</bold>"
        - "<gray>Second page"
```

### Banner, pot, adventure predicates

```yaml
banner_demo:
  material: white_banner
  components:
    banner_patterns:
      - pattern: creeper
        color: black
      - type: border
        color: red
    provides_banner_patterns: "#minecraft:pattern_item/flower"

pot_demo:
  material: decorated_pot
  components:
    pot_decorations:
      front: brick
      back: arb_pottery_sherd
      left: arms_up_pottery_sherd
      right: prize_pottery_sherd

adventure_demo:
  material: diamond_pickaxe
  components:
    can_break:
      - stone
      - blocks: [dirt, gravel]
    can_place_on:
      predicates:
        - blocks: "#minecraft:logs"
```

### Death protection

```yaml
totem_demo:
  material: totem_of_undying
  components:
    death_protection:
      effects:
        - type: apply_effects
          effects:
            - type: regeneration
              duration: 900
              amplifier: 1
        - type: clear_all_status_effects
```

### Recipes & block data

```yaml
knowledge_book_demo:
  material: knowledge_book
  components:
    recipes:
      - minecraft:diamond_sword
      - minecraft:shield

block_state_demo:
  material: chest
  components:
    block_data: {}   # currently builds empty block-item state properties
```

### Entity variants (spawn eggs / mob items)

Enum-style variants:

```yaml
variants_enum:
  material: fox_spawn_egg
  components:
    fox_variant: RED
    salmon_size: LARGE
    parrot_variant: BLUE
    tropical_fish_pattern: KOB
    mooshroom_variant: RED
    rabbit_variant: BLACK
    horse_variant: CHESTNUT
    llama_variant: CREAMY
    axolotl_variant: LUCY
```

Registry-key variants:

```yaml
variants_registry:
  material: wolf_spawn_egg
  components:
    villager_variant: plains
    wolf_variant: pale
    wolf_sound_variant: classic
    cat_variant: tabby
    cat_sound_variant: classic
    frog_variant: temperate
    pig_variant: temperate
    pig_sound_variant: classic
    cow_variant: temperate
    cow_sound_variant: classic
    chicken_variant: temperate
    chicken_sound_variant: classic
    zombie_nautilus_variant: temperate
```

Exact registry ids depend on your Minecraft / Paper version.

---

## Unsetting components

```yaml
unset_demo:
  material: diamond_sword
  components:
    "!enchantments": true
    "!attribute_modifiers": true
```

Setting a valued component to `false` or `null` also unsets it.

---

## Extending handlers

```java
YamlItemParser parser = YamlItem.parser();
parser.handlers().register(DataComponentTypes.CUSTOM_NAME, (stack, type, value, path, p) -> {
    // custom apply logic
});
```

`ComponentHandlerRegistry` indexes Paper `DataComponentTypes` and `Registry.DATA_COMPONENT_TYPE`.

---

## Errors

Invalid configs throw `YamlParseException` with a dotted path, e.g. `cool_sword.components.food.nutrition`.
