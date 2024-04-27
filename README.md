[![kotlin_version](https://img.shields.io/badge/kotlin-1.9.0-blueviolet?style=flat-square)](https://github.com/Astra-Interactive/AstraLibs)
[![minecraft_version](https://img.shields.io/badge/minecraft-1.20-green?style=flat-square)](https://github.com/Astra-Interactive/AstraLibs)
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

Clone this repository, modify `destination` in [build.gradle.kts](plugin/build.gradle.kts) then run the following
command:

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

### AdminPrivate

This feature allows admins to create chunk-based private system.

Data stored in adminchunks.yml and can be easily modified. Changes are applied by plugin reload.

```yaml
# Sample config
isEnabled: true
chunks:
  "-863288426379_world":
    x: 117
    z: -201
    worldName: "world"
    chunkKey: -863288426379
    flags:
      "BREAK": false
      "PLACE": false
      "INTERACT": false
      "EXPLODE": false
      "EMPTY_BUCKET": false
      "SPREAD": false
```

### Menu

This feature allows admins to create simple menu guis

Data stored in menu/XXX.yml and can be easily modified. Changes are applied by plugin reload.

#### Placeholders:

- `{PLAYER}` - player, opened an inventory

```yaml
# XXS, XS, S, M, L, XL
size: XXS
# Can be executed by /menu XXX
# Example: /menu main
command: main
# Title of the menu
title: Main Menu
# Items stored in page
items:
  diamond:
    permission: com.example.permission
    index: 3
    name: Item
    lore:
      - Lore1
      - Lore2
    amount: 3
    material: DIAMOND
    custom_model_data: 10
    # Console command reward
    reward:
      !<console_command>
      commands:
        - say hello
        - give {PLAYER} dirt 64
    # Player command reward
    reward:
      !<player_command>
      commands:
        - say hello
    # Money price         
    price:
      !<money>
      amount: 10
```

### Money Drop

Drop money from mobs. It's configured to be exploit-free, so it will not be dropped from same location

```yaml
money_drop:
  "zombie":
    from: "ZOMBIE"
    chance: 100.0
    min: 10.0
    max: 100.0
  "diamond_ore":
    from: "DIAMOND_ORE"
    chance: 100.0
    min: 10.0
    max: 100.0
```

| Command                            | Permission           | Description                       |
|:-----------------------------------|:---------------------|:----------------------------------|
| `/adminprivate map`                | `aspekt.admin_claim` | `Show map of near claimed chunks` |
| `/adminprivate claim`              | `aspekt.admin_claim` | `Claim current chunk`             |
| `/adminprivate unclaim`            | `aspekt.admin_claim` | `Unclaim current chunk`           |
| `/adminprivate flag <flag> <bool>` | `aspekt.admin_claim` | `Set flag for chunk`              |

### Inventory sort

AspeKt allows players to sort their inventories by pressing Shift+MiddleMouseButton

## Commands

| Command                                 | Permission               | Description                                          |
|:----------------------------------------|:-------------------------|:-----------------------------------------------------|
| `/maxonline <int>`                      | `aspekt.maxonline`       | `Set server max online to new value`                 |
| `/atemframe <visible> <fixed> <radius>` | `aspekt.atemframe`       | `Makes itemFrames in <radius> <visible> and <fixed>` |
| `/aesreload`                            | `aspekt.reload`          | `Reloads plugin`                                     |
| `/sit`                                  | `-`                      | `Sit a player`                                       |
| `/tellchat <player> [message]`          | `aspekt.tellchat`        | `Sends player a message like /tellraw command`       |
| `/swearfilter <on\| off> [PLAYER]`      | `aspekt.set_swear.admin` | `Set swear filter for player`                        |
| `/swearfilter <on\| off>`               | `-`                      | `Set swear filter for yourself`                      |
