# ItemAPI

Lightweight, shadeable Java APIs for Paper Minecraft servers (1.21.4+ / Paper 26.x Data Components) providing **Item Text Component Formatting**, **YAML Item Parsing**, and **MiniPlaceholders Expansion**.

---

## 📦 Modules Overview

ItemAPI is built as a multi-module Maven project. You can shade individual modules or all of them into your plugin.

| Module | Description |
| :--- | :--- |
| [**`itemtext`**](file:///h:/IdeaProjects/ItemAPI/itemtext) | Converts `ItemStack` objects into Adventure `Component`s with item/block atlas sprites, custom player head skins, MiniMessage patterns, amount styles, rarity colors, and hover effects. |
| [**`yamlitem`**](file:///h:/IdeaProjects/ItemAPI/yamlitem) | Parses `ItemStack` objects from YAML configuration sections or Maps using Paper's modern `DataComponentTypes` with root-level shortcuts and extensible component handlers. |
| [**`itemplaceholder`**](file:///h:/IdeaProjects/ItemAPI/itemplaceholder) | MiniPlaceholders v3 expansion provider exposing `<item_hand:...>` and `<item_offhand:...>` MiniMessage tags. |
| [**`ExamplePlugin`**](file:///h:/IdeaProjects/ItemAPI/ExamplePlugin) | Example Paper plugin demonstrating how to shade `itemtext` and `yamlitem`, parse `items.yml`, and display item sprites. |

---

## 🎨 Module Details & Examples

### 1. `itemtext`

Renders `ItemStack`s as interactive Adventure `Component`s using Minecraft's atlas sprites (`minecraft:items` and `minecraft:blocks`) and `playerHead` components.

#### Available MiniMessage Tags in Patterns

When defining a pattern in `ItemTextOptions`, the following tags are resolved:

- `<item_sprite>`: Renders the item's sprite or player head texture.
- `<item_displayname>`: Item custom name or translatable item key with optional rarity coloring.
- `<subscript_number>`: Formats item count as subscript numbers (e.g., `₂`, `₆₄`).
- `<superscript_number>`: Formats item count as superscript numbers (e.g., `²`, `⁶⁴`).
- `<normal_number>`: Formats item count as standard digits (e.g., `2`, `64`).
- `<item_amount>`: Formats item count according to the configured `amountDisplay()` setting.

#### Java Example

```java
import me.usainsrht.itemapi.itemtext.AmountDisplay;
import me.usainsrht.itemapi.itemtext.ItemText;
import me.usainsrht.itemapi.itemtext.ItemTextOptions;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

// 1. Format using global default options
Component component = ItemText.format(itemStack);

// 2. Format with customized options
ItemTextOptions options = ItemTextOptions.builder()
        .pattern("<item_sprite><superscript_number> <item_displayname>")
        .displayRarityColor(true)
        .showAmountWhenOne(false)
        .shadowEnabled(true)
        .hoverEnabled(true)
        .build();

Component customComponent = ItemText.format(itemStack, options);

// 3. Configure global default options for your plugin
ItemText.setDefaultOptions(builder -> builder
        .displayBrackets(false)
        .amountDisplay(AmountDisplay.SUBSCRIPT)
        .removeItalic(true)
);
```

---

### 2. `yamlitem`

Parses YAML configurations into Paper `ItemStack` objects powered by `DataComponentTypes`.

#### Supported Root Shortcuts

You can use intuitive root keys directly in YAML:

- `material` / `type` / `item`: Material name or registry key (e.g. `diamond_sword`, `minecraft:golden_apple`)
- `amount` / `count`: Item quantity (default `1`)
- `name` / `custom_name`: Custom display name (MiniMessage string)
- `item_name`: Item name override (MiniMessage string)
- `lore`: List of lore lines (MiniMessage strings)
- `enchantments`: Map of enchantment keys to levels (e.g. `sharpness: 5`)
- `stored_enchantments`: Map of stored enchantments for enchanted books
- `unbreakable`: Boolean flag (`true`/`false`)
- `glint` / `enchantment_glint_override`: Custom glint override (`true`/`false`)
- `hide_tooltip`: Boolean flag (`true`/`false`) to hide entire item tooltip
- `damage` / `max_damage`: Item durability values
- `max_stack_size`: Stack size override
- `custom_model_data`: Integer custom model data
- `rarity`: Rarity level (`common`, `uncommon`, `rare`, `epic`)
- `item_model`: Model key (e.g. `minecraft:item/cool_sword`)
- `repair_cost`: Anvil repair cost integer
- `components`: Nested section for any Paper `DataComponentType` (prefix key with `!` to unset)

#### YAML Example (`items.yml`)

```yaml
cool_sword:
  material: diamond_sword
  name: "<red><bold>Cool Sword"
  lore:
    - "<gray>An example YamlItem"
    - "<dark_gray>Parsed by ItemAPI"
  enchantments:
    sharpness: 5
    unbreaking: 3
  unbreakable: true

glowing_apple:
  material: golden_apple
  amount: 8
  name: "<gold>Glowing Apple"
  lore:
    - "<yellow>Tasty and shiny"
  glint: true

custom_head:
  material: player_head
  name: "<aqua>Example Head"
  components:
    profile: UsainSrht
```

#### Java Example

```java
import me.usainsrht.itemapi.yamlitem.YamlItem;
import me.usainsrht.itemapi.yamlitem.YamlParseException;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

ConfigurationSection section = config.getConfigurationSection("cool_sword");
try {
    ItemStack itemStack = YamlItem.parse(section);
    player.getInventory().addItem(itemStack);
} catch (YamlParseException ex) {
    getLogger().warning("Invalid item configuration: " + ex.getMessage());
}
```

---

### 3. `itemplaceholder`

MiniPlaceholders v3 expansion provider exposing `<item_hand:...>` and `<item_offhand:...>` tags.

#### Supported Tag Options (Colon-Separated)

- `brackets` / `no_brackets`: Wrap formatted output in `[...]`
- `custom_name` / `no_custom_name` (`translate`): Control display name fallback
- `italic` / `no_italic`: Control italic text removal
- `superscript` (`super`) / `subscript` (`sub`) / `normal_amount` (`normal`): Amount style
- `show_one` (`force_amount`): Show amount even when stack size is 1
- `shadow` / `no_shadow`: Enable/disable text shadow
- `shadow_color:<color>`: Shadow color (named, `#RRGGBB`, `#RRGGBBAA`, `default`, `none`)
- `sprite_color:<color>`: Sprite color tint (named color, hex `#RRGGBB`, or `none`)
- `hover` / `no_hover`: Enable/disable item hover details

#### MiniMessage Usage Examples

```text
You are holding: <item_hand>
Offhand item: <item_offhand:brackets:shadow:superscript>
Styled hand item: <item_hand:sprite_color:gold:shadow_color:#000000>
```

---

## 🛠️ How to Use in Your Projects

### 1. Maven (`pom.xml`)

Add the desired modules to your `pom.xml` and relocate the package using `maven-shade-plugin`:

```xml
<dependencies>
    <dependency>
        <groupId>me.usainsrht.itemapi</groupId>
        <artifactId>itemtext</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>
    <dependency>
        <groupId>me.usainsrht.itemapi</groupId>
        <artifactId>yamlitem</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-shade-plugin</artifactId>
            <version>3.6.0</version>
            <executions>
                <execution>
                    <phase>package</phase>
                    <goals>
                        <goal>shade</goal>
                    </goals>
                    <configuration>
                        <relocations>
                            <relocation>
                                <pattern>me.usainsrht.itemapi</pattern>
                                <shadedPattern>your.package.libs.itemapi</shadedPattern>
                            </relocation>
                        </relocations>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

---

### 2. Gradle (`build.gradle.kts`)

Using Gradle with the Shadow plugin (`com.gradleup.shadow`):

```kotlin
plugins {
    java
    id("com.gradleup.shadow") version "8.3.5"
}

dependencies {
    implementation("me.usainsrht.itemapi:itemtext:1.0.0-SNAPSHOT")
    implementation("me.usainsrht.itemapi:yamlitem:1.0.0-SNAPSHOT")
}

tasks.shadowJar {
    relocate("me.usainsrht.itemapi", "your.package.libs.itemapi")
}
```

---

## 🔨 How to Build

### Prerequisites

- **Java Development Kit (JDK)**: Version 25 or higher.
- **Apache Maven**: Version 3.9.0 or higher.

### Build Command

Run the following command from the repository root:

```bash
mvn clean package
```

The compiled artifacts will be produced in the `target/` folder of each respective module:
- `itemtext/target/itemtext-1.0.0-SNAPSHOT.jar`
- `yamlitem/target/yamlitem-1.0.0-SNAPSHOT.jar`
- `itemplaceholder/target/itemplaceholder-1.0.0-SNAPSHOT.jar`
- `ExamplePlugin/target/ItemAPIExamplePlugin.jar`

---

## 🤝 How to Contribute

Contributions are welcome! Follow these steps to contribute to ItemAPI:

1. **Fork the Repository**: Click the "Fork" button on GitHub.
2. **Clone Your Fork**:
   ```bash
   git clone https://github.com/your-username/ItemAPI.git
   cd ItemAPI
   ```
3. **Create a Feature Branch**:
   ```bash
   git checkout -b feature/my-new-feature
   ```
4. **Make Your Changes**:
   - Write clean, readable Java code targeted for JDK 25 and Paper API.
   - Follow standard Java code formatting and naming conventions.
   - Keep existing public API contracts intact.
5. **Test Your Changes**:
   Build the repository to verify there are no compilation or shade errors:
   ```bash
   mvn clean package
   ```
6. **Commit & Push**:
   ```bash
   git commit -m "Add new component handler for XYZ"
   git push origin feature/my-new-feature
   ```
7. **Open a Pull Request**: Submit a PR to the main repository explaining your changes.

---

## 📄 License

This project is open-source. Refer to the repository LICENSE file for full licensing terms.
