**Amplitude** is a **Minecraft Java library** created with **retro-compatibility** and **multi-platform** in mind.
It is a **facade** for all the available **Minecraft server plugins** messaging systems:

- `strings` for [Spigot](https://www.spigotmc.org/wiki/spigot);
- [BaseComponents](https://javadoc.io/doc/net.md-5/bungeecord-chat/latest/net/md_5/bungee/api/chat/BaseComponent.html)
  for [BungeeCord](https://www.spigotmc.org/wiki/bungeecord/);
- [Components](https://jd.advntr.dev/api/4.9.0/net/kyori/adventure/text/Component.html)
  for [Velocity](https://papermc.io/software/velocity) (and [Kyori Adventure](https://docs.advntr.dev/) in general).

Thanks to this project, it is possible to write player messages only once and see it work on all
the platforms described above. Check out the [wiki](./wiki) for more.

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
