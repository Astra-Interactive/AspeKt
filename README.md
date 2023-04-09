[![kotlin_version](https://img.shields.io/badge/kotlin-1.7.0-blueviolet?style=flat-square)](https://github.com/Astra-Interactive/AstraLibs)
[![minecraft_version](https://img.shields.io/badge/minecraft-1.19-green?style=flat-square)](https://github.com/Astra-Interactive/AstraLibs)
[![platforms](https://img.shields.io/badge/platform-spigot-blue?style=flat-square)](https://github.com/Astra-Interactive/AstraLibs)

# AspeKt

AspeKt is Essentials plugin for [EmpireProjekt](https://EmpireProjekt.ru) server
Essentials plugin for EmpireSMP

It provides basic functionality and commands in one plugin

* **AspeKt requires Paper to run.** Other server software may work, but these are not tested.
* **AspeKt is made only for Latest Paper release(1.19.4).** Other versions may work, but these are not tested.

## Support

I don't provide support for plugins, which are not released by me on spigot or any other website(except github)

## Building

To build AspeKt, you need JDK 18 or higher installed on your system.

Clone this repository, modify `destinationDirectoryPath` in `gradle/libs.versions.toml` then run the following command:

* On Linux or macOS: `./gradlew shadowJar`
* On Windows: `gradlew shadowJar`

## Features

### AutoBroadcast

```yaml
announcements:
  interval: 5
  announcements:
    - '#FFFFFF[#baa51c!#FFFFFF]: #03b5fcДискорд сервера #03fc56discord.gg/Gwukdr8'
```

### AutoCrop

It allows players to gather crops by right clicking it

```yaml
core:
  auto_crop:
    enabled: true
    min: 1
    max: 2
    duping:
      enabled: true
      clear_every: 60000
      location_timeout: 15000
```

### Sit

Allows players to use /sit, or sit on slabs or stairs by right clicking it

```yaml
core:
  sit: true
```

### TreeCapitator

Allows players to break trees when shift+break

```yaml
core:
  tree_capitator:
    enabled: true
    destroy_limit: 16
    damage_axe: true
    break_axe: true
    replant: true
    replant_max_iterations: 10
    destroy_leaves: true
```

### Inventory sort

AspeKt allows players to sort their inventories by pressing Shift+MiddleMouseButton

## Commands

| Command                                 | Permission         | Description                                          |
|:----------------------------------------|:-------------------|:-----------------------------------------------------|
| `/maxonline <int>`                      | `aspekt.maxonline` | `Set server max online to new value`                 |
| `/atemframe <visible> <fixed> <radius>` | `aspekt.atemframe` | `Makes itemFrames in <radius> <visible> and <fixed>` |
| `/aesreload`                            | `aspekt.reload`    | `Reloads plugin`                                     |
| `/sit`                                  | `-`                | `Sit a player`                                       |
| `/tellchat <player> [message]`          | `aspekt.tellchat`  | `Sends player a message like /tellraw command`       |
