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
