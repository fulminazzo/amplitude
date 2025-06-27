**Amplitude** is a **Minecraft Java library** created with **retro-compatibility** and **multi-platform** in mind.
It is a **facade** for all the available **Minecraft server plugins** messaging systems:

- `strings` for [Spigot](https://www.spigotmc.org/wiki/spigot);
- [BaseComponents](https://javadoc.io/doc/net.md-5/bungeecord-chat/latest/net/md_5/bungee/api/chat/BaseComponent.html)
  for [BungeeCord](https://www.spigotmc.org/wiki/bungeecord/);
- [Components](https://jd.advntr.dev/api/4.9.0/net/kyori/adventure/text/Component.html)
  for [Velocity](https://papermc.io/software/velocity) (and [Kyori Adventure](https://docs.advntr.dev/) in general).

Thanks to this project, it is possible to write player messages only once and see it work on all
the platforms described above.

| **Table of Contents**               |
|-------------------------------------|
| [How to import](#import)            |
| [Available components](#components) |

## Import

**Amplitude** can be imported using one of the three common methods:

- **Gradle** (preferred):

  ```groovy
  repositories {
      maven { url = 'https://repo.fulminazzo.it/releases' }
  }

  dependencies {
      implementation 'it.fulminazzo:amplitude:latest'
  }
  ```

- **Maven** (alternative):

  ```xml
  <repository>
      <id>fulminazzo</id>
      <url>https://repo.fulminazzo.it/releases</url>
  </repository>
  ```

  ```xml
  <dependency>
      <groupId>it.fulminazzo</groupId>
      <artifact>amplitude</artifact>
      <version>LATEST</version>
  </dependency>
  ```

- **Manual** (discouraged): download the JAR file from the [latest release](../../releases/latest) and import it using
  your IDE.

## Components

**Amplitude** provides several components to mimic the supported ones from Minecraft.
This section will describe each one in its **serialized** and **Java** form.

Note that every component can be **serialized** and **deserialized** using provided functions:

- to **deserialize**, it is possible to use
  [Component#fromRaw(String)](../blob/main/common/src/main/java/it/fulminazzo/amplitude/component/Component.java):

  ```java
  String serialized;
  Component component = Component.fromRaw(serialized);
  ```
  
- to **serialize**, every **Amplitude component** provides a `serialize` method:
  
  ```java
  Component component;
  String serialized = component.serialize();
  ```
  
| **Components**                                  |
|-------------------------------------------------|
| [Component](#component)                         |
| [HexComponent](#hexcomponent)                   |
| [FontComponent](#fontcomponent)                 |
| [HoverComponent](#hovercomponent)               |
| [ClickComponent](#clickcomponent)               |
| [InsertionComponent](#insertioncomponent)       |
| [TranslatableComponent](#translatablecomponent) |

### Component

[Component](../blob/main/common/src/main/java/it/fulminazzo/amplitude/component/Component.java)
is the most basic component of **Amplitude**.
It provides support for basic text, as well as coloring and text style.

- **Basic text**: simply input your message:

  ```java
  Component component = new Component("This is my basic text");
  // or
  String serialized = "This is my basic text";
  Component deserialized = Component.fromRaw(serialized);
  ```

- **Coloring**: allows to change the color of the text using Minecraft default colors.
  [Here](../blob/main/common/src/main/java/it/fulminazzo/amplitude/component/Color.java)
  it is possible to find all the available ones.

  ```java
  Component component = new Component("Colored text");
  component.setColor(
    Color.LIGHT_PURPLE, 
    // If false, only the current component will be updated.
    // Meaning, that any subcomponent (a.k.a. follow up components)
    // will NOT be colored.
    true
  );
  // or
  String serialized = "<light_purple>Colored text";
  Component deserialized = Component.fromRaw(serialized);
  ```

- **Style**: allows to change the style of the text using Minecraft styles (bold, italic, etc...).
  [Here](../blob/main/common/src/main/java/it/fulminazzo/amplitude/component/Style.java)
  it is possible to find all the available ones.

  ```java
  Component component = new Component("Bold text");
  component.setStyle(
    Style.BOLD, 
    // false to "unbold"
    true,
    // If false, only the current component will be updated.
    // Meaning, that any subcomponent (a.k.a. follow up components)
    // will NOT be styled. 
    true
  );
  // or
  Component component = new Component("Bold text");
  component.setBold(
    // false to "unbold"
    true,
    // If false, only the current component will be updated.
    // Meaning, that any subcomponent (a.k.a. follow up components)
    // will NOT be styled. 
    true
  );
  // or
  String serialized = "<bold>Bold text";
  Component deserialized = Component.fromRaw(serialized);
  ```

  Among the styles there is also the special one `RESET`, that will remove any coloring, style or font applied.

### HexComponent

As the name implies,
[HexComponent](../blob/main/common/src/main/java/it/fulminazzo/amplitude/component/HexComponent.java)
provides support for **HEX colors** (only available in **Minecraft 1.16+**).

```java
Component component = new HexComponent("<hex color=#FF00AA>I'm colorful!");
// or
String serialized = "<hex color=#FF00AA>I'm colorful!";
Component deserialized = Component.fromRaw(serialized);
```
