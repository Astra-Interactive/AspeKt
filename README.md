<p align="center">
  <img src="assets/logo.png" alt="AspeKt Logo" width="160"/>
</p>

<h1 align="center">AspeKt</h1>
<p align="center"><strong>A lightweight, Kotlin-powered essentials plugin for Paper, Forge & NeoForge servers</strong></p>

---

## What is AspeKt?

**AspeKt** is a modular essentials plugin for Minecraft servers built entirely in **Kotlin**. It ships a curated set of survival-quality features — land claims, teleportation, chat games, money drops, tree-capitation, player protection, and more — without the bloat of traditional "kitchen-sink" plugins.

Originally built for the **EmpireProjekt** community ([empireprojekt.ru](https://empireprojekt.ru)), AspeKt is free and open to all server owners. Licensed under **MIT**.

**Downloads:** [GitHub Releases](https://github.com/Astra-Interactive/AspeKt/releases) · [Modrinth](https://modrinth.com/project/6NpNwzA1)

---

## Supported Platforms

| Platform       | Minecraft                    | Feature set                         |
|----------------|------------------------------|-------------------------------------|
| **Paper**      | 26.1.2 (`api-version: 1.18`) | The full survival feature set below |
| **Forge**      | 1.20.1 (Forge 47.4.12)       | Auth, Claims, RTP, SetHome, TPA     |
| **NeoForge**   | 1.21.1 (NeoForge 21.1.248)   | Auth, Claims, RTP, SetHome, TPA     |
| **BungeeCord** | 26.1                         | Online-count simulator              |

Every module stores its files in the plugin data folder: `plugins/AspeKt/` on Paper, `config/AspeKt/` on Forge/NeoForge. Config files are generated with defaults on first run and are reloaded live with `/aesreload`.

---

## Soft Dependencies

These plugins are **optional** on Paper. AspeKt integrates with them when present:

| Plugin           | Integration                                                                                                                             |
|------------------|-----------------------------------------------------------------------------------------------------------------------------------------|
| **Vault**        | All money features (MoneyDrop, ChatGame, MoneyAdvancements, PlaytimeReward, menu prices) pay through the server's default Vault economy |
| **EssentialsX**  | Economy fallback when no Vault economy is registered; powers `/rtpbypass`                                                               |
| **packetevents** | Required for the AntiSwear packet-level chat filter — without it no filtering happens                                                   |
| **LuckPerms**    | On Forge/NeoForge, required for permission checks on non-op players (fallback: op level 4). On Paper any permission plugin works        |

> Money features require an economy provider (a Vault-compatible economy or EssentialsX) to actually pay players. A built-in multi-currency economy module (`/ekon`) exists in the codebase but is not enabled in current builds.

---

## Features

### 🏠 Claims

> **Platform:** Paper · Forge · NeoForge

Chunk-based land protection. Players claim the chunk they stand in and control exactly what is allowed inside it.

**Commands** — all subcommands require `aspekt.admin_claim`, which also **bypasses** claim protection:

| Command                            | Description                                                |
|------------------------------------|------------------------------------------------------------|
| `/claim claim`                     | Claim the chunk you are standing in                        |
| `/claim unclaim`                   | Unclaim the chunk you are standing in                      |
| `/claim map`                       | Display a 5 × 5 minimap of nearby claims                   |
| `/claim flag <flag> <true\|false>` | Toggle a protection flag on the current chunk (owner only) |
| `/claim add <player>`              | Grant a player member access to your claim                 |
| `/claim remove <player>`           | Revoke a player's member access                            |

**Chunk flags** (all `false` — i.e. blocked for outsiders — when a chunk is first claimed):

| Flag                      | Controls                                            |
|---------------------------|-----------------------------------------------------|
| `ALLOW_BREAK`             | Block breaking                                      |
| `ALLOW_PLACE`             | Block placement, pistons, portals                   |
| `ALLOW_INTERACT`          | Block/entity interaction, item frames, armor stands |
| `ALLOW_EXPLODE`           | Explosion damage, placing TNT/lava                  |
| `ALLOW_EMPTY_BUCKET`      | Emptying buckets (water/lava)                       |
| `ALLOW_SPREAD`            | Fire and lava spreading                             |
| `ALLOW_RECEIVE_DAMAGE`    | Damage to players inside the claim                  |
| `ALLOW_HOSTILE_MOB_SPAWN` | Hostile mob spawning                                |
| `ALLOW_ICE_MELT`          | Ice melting                                         |

Owners and members bypass all flags. On Forge/NeoForge the protection covers break, place, interact, and explosions. Claim data is stored per owner in `<datafolder>/claims/`.

---

### 🌲 TreeCapitator

> **Platform:** Paper

Fells an entire tree by breaking a single log **while sneaking** with an axe.

**Mechanics:**

- Trigger: break any log (including stripped logs and Crimson/Warped stems) while sneaking and holding an axe.
- Recursively breaks all connected logs — and leaves, if enabled — up to the configured depth limit.
- Applies normal axe durability per block, respecting the Unbreaking enchantment. With `break_axe: false` the axe never breaks.
- Optionally replants a sapling (oak, dark oak, spruce, acacia, birch, jungle) on the dirt below the original log.

**Config** (`config.yml`):

| Key                      | Default | Description                                           |
|--------------------------|---------|-------------------------------------------------------|
| `enabled`                | `true`  | Enable or disable the module                          |
| `destroy_limit`          | `16`    | Maximum recursion depth of a single chop              |
| `damage_axe`             | `true`  | Apply durability damage to the axe                    |
| `break_axe`              | `true`  | Allow the axe to break from durability loss           |
| `replant`                | `true`  | Automatically place a sapling after chopping          |
| `replant_max_iterations` | `16`    | How far down to search for a dirt block to replant on |
| `destroy_leaves`         | `true`  | Remove leaves when the tree is felled                 |

---

### 🎛️ Menu

> **Platform:** Paper

Define fully custom GUI menus in YAML — no coding required. Each file in `plugins/AspeKt/menu/` is one menu; the folder is created empty on first run.

**Commands:**

| Command              | Permission | Description                                          |
|----------------------|------------|------------------------------------------------------|
| `/menu`              | —          | Open the first defined menu                          |
| `/menu <name>`       | —          | Open the menu whose `command` value matches `<name>` |
| `/invclose <player>` | —          | Close another player's open inventory                |

**Menu file format** (`plugins/AspeKt/menu/<anything>.yml`):

```yaml
size: S                             # XXS=9, XS=18, S=27, M=36, L=45, XL=54 slots
command: shop                       # Token used with /menu <name>
title: "&6Server Shop"              # Legacy & and &#RRGGBB color codes
update_interval: 5000               # Optional auto-refresh interval in ms; omit to disable
items:
  diamond_deal:
    index: 13                       # Slot index (0-based)
    name: "&bDiamond"
    material: DIAMOND               # Bukkit Material name
    amount: 1
    custom_model_data: 0
    lore:
      - "&7Costs &a100$"
    permission: "myserver.shop.diamond"   # Optional node required to click
    price:
      type: money                   # money | nothing
      amount: 100.0
    reward:
      type: console_command         # console_command | player_command | nothing
      commands:
        - "give {PLAYER} diamond 1"
    visibility_conditions: # Item hidden unless any condition passes
      - type: permission
        permission: "myserver.shop.vip"
        is_inverted: false
    clickable_conditions: # Item not clickable unless any condition passes
      - type: permission
        permission: "myserver.shop.buy"
```

`{PLAYER}` in reward commands is replaced with the clicking player's name. Items can never be taken out of a menu. `money` prices require an economy provider.

---

### 🪑 Sit

> **Platform:** Paper

Players can sit on stairs and slabs by right-clicking them with an empty hand (while not sneaking). `/sit` seats you right where you stand.

| Command | Permission | Description                   |
|---------|------------|-------------------------------|
| `/sit`  | —          | Sit down at your current spot |

**Mechanics:**

- Refuses to seat you inside blocks, too far from the clicked seat, in mid-air, or while flying.
- Players stand up automatically on death, teleport, disconnect, or dismount.

**Config** (`sit.yml`): `is_enabled: true` — disables both the click-to-sit and `/sit`.

---

### 🧩 ChatGame (Quiz)

> **Platform:** Paper

Periodically posts a challenge in chat. The first player to answer with `/quiz` wins a money reward.

| Command          | Permission | Description                                |
|------------------|------------|--------------------------------------------|
| `/quiz <answer>` | —          | Submit your answer to the active challenge |

**Config** (`chat_game.yml`) — the module **ships disabled**; set `isEnabled: true` to start:

```yaml
isEnabled: false
timer:
  initialDelaySeconds: 10      # Delay before the first game
  delaySeconds: 300            # Interval between games
defaultReward:
  type: "MONEY"
  minAmount: 0.0
  maxAmount: 100.0
  currency_id: null            # null = default economy
chat_games:
  - type: "RIDDLE"
    question: "Висит груша нельзя скушать"
    answer: "Лампа"
    reward: null                 # null = use defaultReward
  - type: "SUM_OF_TWO"
    reward: null
  - type: "TIMES_OF_TWO"
    reward: null
  - type: "EQUATION_EASY"
    reward: null
```

**Game types:**

| Type                 | Description                                          |
|----------------------|------------------------------------------------------|
| `RIDDLE`             | Custom question with a fixed answer                  |
| `SUM_OF_TWO`         | Random addition problem                              |
| `TIMES_OF_TWO`       | Random multiplication problem                        |
| `EQUATION_EASY`      | Simple linear equation (e.g. `x + 3 = 7`)            |
| `EQUATION_QUADRATIC` | Quadratic equation — either root wins (experimental) |
| `ANAGRAM`            | Scrambled word from your `words:` list               |

Answers are case-insensitive; multi-word answers work without quotes. The reward is a random whole amount between `minAmount` and `maxAmount`.

---

### 🛡️ NewBee (New Player Protection)

> **Platform:** Paper

Gives newly-joined players a temporary protection shield for their first **50 minutes** of total playtime.

**Mechanics:**

- While protected, the player receives **Regeneration, Absorption III, Haste IV and Fire Resistance V** for the remaining shield time.
- All incoming damage is reduced to **70%** of normal.
- The buff effects are stripped immediately if the protected player deals or receives PvP damage.
- A title and message are shown on join and respawn while the shield is active.

No configuration file — timings and effects are defined in code.

---

### ⛓️ Jail

> **Platform:** Paper

Confine players to designated jail locations for a set duration.

| Command                               | Permission           | Description                            |
|---------------------------------------|----------------------|----------------------------------------|
| `/jail list`                          | `aspekt.jail.list`   | List all defined jails                 |
| `/jail create <name>`                 | `aspekt.jail.create` | Create a jail at your current location |
| `/jail delete <name>`                 | `aspekt.jail.delete` | Delete a jail (must have no inmates)   |
| `/jail inmate <jail> <player> <time>` | `aspekt.jail.inmate` | Send a player to jail for a duration   |
| `/jail free <player>`                 | `aspekt.jail.free`   | Release a jailed player immediately    |

**Time format:** compound duration strings — `30s`, `10m`, `1h30m`, `3w4d6h10m30s`.

**Mechanics:**

- Inmates are teleported to the jail; on release (automatic or manual) they return to their pre-jail location.
- All commands and interactions are blocked while jailed; relogging or respawning teleports the inmate straight back.
- Release is checked every 10 seconds.

---

### 🤬 AntiSwear

> **Platform:** Paper · requires **packetevents**

Per-player profanity filter that replaces matched text with `****` — each player sees chat filtered according to their **own** setting.

| Command                           | Permission | Description                          |
|-----------------------------------|------------|--------------------------------------|
| `/swearfilter <on\|off>`          | —          | Toggle the swear filter for yourself |
| `/swearfilter <on\|off> <player>` | —          | Toggle the filter for another player |

**Mechanics:**

- Regex-based detection with broad coverage of Russian profanity and common evasion patterns (the word list is compiled in, not configurable).
- Filters at the packet level in both directions: your outgoing messages and every message you receive.
- Enabled by default for every player; the preference persists across sessions.
- Without the **packetevents** plugin installed, no filtering happens.

---

### 📢 AutoBroadcast

> **Platform:** Paper

Sends periodic announcements to all online players, cycling round-robin through the configured list.

**Config** (`announcements.yml`) — ships with an empty list:

```yaml
interval: 300                  # Seconds between announcements
announcements:
  welcome:
    type: TEXT                 # Chat message
    text: "&6Welcome to the server!"
  hint:
    type: ACTION_BAR           # Action bar
    text: "&7Use &a/menu &7to open the shop"
  event:
    type: BOSS_BAR             # Boss bar draining over duration_seconds
    text: "&cEvent starting soon!"
    barColor: RED              # PINK | BLUE | RED | GREEN | YELLOW | PURPLE | WHITE
    duration_seconds: 10
```

Text supports legacy `&` codes and `&#RRGGBB` hex colors.

---

### 📦 InventorySort

> **Platform:** Paper

Sorts any inventory with a single gesture.

- **Shift + right-click** inside an inventory (chest or your own) sorts its contents.
- Each click advances to the next sort mode, cycling through type, name, wool color, glass color, block kind, and tool kind — each ascending and descending.
- The default shift-click item transfer is cancelled, so no items move accidentally.

No commands or config.

---

### 🔥 Restrictions

> **Platform:** Paper

Per-world toggles for destructive game mechanics, independent of claims.

**Config** (`restrictions.yml`) — every rule has the same shape:

```yaml
explosion:
  damage_creeper: # Creeper explosions deal no block damage
    is_enabled: true
    restricted_in_worlds: [ ]   # Empty list = every world
    invert: false              # true = restrict everywhere EXCEPT the listed worlds
  destroy: # Explosion block destruction in general
    is_enabled: true
    restricted_in_worlds: [ ]
    invert: false
place:
  tnt: { is_enabled: true, restricted_in_worlds: [ ], invert: false }
  lava: { is_enabled: true, restricted_in_worlds: [ ], invert: false }
spread:
  lava: { is_enabled: true, restricted_in_worlds: [ ], invert: false }
  fire: { is_enabled: true, restricted_in_worlds: [ ], invert: false }
```

> ⚠️ The defaults restrict **everything everywhere**: TNT placement, lava placement, fire/lava spread, and explosion block damage are all blocked out of the box. Set `is_enabled: false` on the rules you want to allow. Lighting fires with flint and steel stays allowed. Restricted actions are cancelled silently.

---

### 💸 MoneyDrop

> **Platform:** Paper

Mobs and blocks drop physical money items (a golden coin) that players pick up to receive the amount.

**Config** (`money_drop.yml`) — ships empty; the module does nothing until you add entries:

```yaml
money_drop:
  diamond_ore:
    from: "DEEPSLATE_DIAMOND_ORE"   # Material name (block break) or EntityType name (mob kill)
    chance: 25.0                    # Drop chance in percent (0–100)
    min: 10.0                       # Minimum amount
    max: 50.0                       # Maximum amount (exclusive)
    currencyId: null                # null = default economy
```

**Mechanics:**

- Drops only when a player causes the break/kill; walking over the coin credits the amount to your balance.
- Hoppers and droppers cannot collect money items.
- Anti-farm: each block location only pays once per 30 days, and player-placed or piston-moved blocks never pay out.

---

### 🌾 AutoCrop

> **Platform:** Paper

Harvest and instantly replant mature crops by right-clicking them with a hoe.

**Mechanics:**

- Right-click a fully grown crop (wheat, potatoes, carrots, beetroots, sweet berries) while holding a hoe.
- The harvest radius scales with the hoe tier: wooden 2 → netherite 7.
- Crops reset to age 0 (auto-replanted); the crop item and its seeds are dropped; the hoe takes normal durability damage.
- A dupe-prevention lock stops the same block paying out twice in quick succession.

**Config** (`auto_crop.yml`):

| Key       | Default | Description                                      |
|-----------|---------|--------------------------------------------------|
| `enabled` | `true`  | Enable or disable the module                     |
| `min`     | `0`     | Minimum drop roll per harvested crop             |
| `max`     | `0`     | Maximum drop roll (exclusive) per harvested crop |

> ⚠️ Set `max` strictly greater than `min` (e.g. `min: 1`, `max: 3`) — the shipped `0`/`0` default is not a valid roll range.

---

### 🏆 MoneyAdvancements

> **Platform:** Paper

Awards currency when a player completes a Minecraft advancement, scaled by its type.

**Config** (`money_advancements.yml`):

```yaml
challenge: 5000    # Reward for a Challenge-type advancement
goal: 1000         # Reward for a Goal-type advancement
task: 1000         # Reward for a Task-type advancement
currency_id: null  # null = default economy
```

---

### ⏱️ PlaytimeReward

> **Platform:** Paper

Pays a fixed reward every time a player accumulates the configured amount of playtime, then the counter restarts.

**Config** (`playtime_reward.yml`):

```yaml
is_enabled: true
reward_amount: 10.0            # Amount paid per completed period
required_minutes: "0d1h"       # Playtime needed per reward (duration string)
check_interval_seconds: "0d1m" # How often progress is checked (duration string)
```

Durations use compound strings like `30m`, `1h30m`, `3w4d6h10m30s`. Per-player progress is persisted across restarts.

---

### ⛏️ OreGeneration

> **Platform:** Paper

Thins out ore veins and chest loot to make resources scarcer on survival servers. Two independent features, two config files:

**World generation** (`ore-generation.yml`) — in **newly generated chunks**, each configured ore block is replaced by its host stone (stone/deepslate/netherrack) with the given chance:

```yaml
is_enabled: true
# The chance in range [0.0, 1.0] of the ore will be excluded
ores:
  "COAL_ORE": 0.5
  "IRON_ORE": 0.5
  "DIAMOND_ORE": 0.5
  "ANCIENT_DEBRIS": 0.5
  # ... ships with all 19 vanilla ores at 0.5
```

**Loot chests** (`loot-generation.yml`) — each unit of a configured item in naturally generated loot is independently removed with the given chance:

```yaml
enabled: true
# The chance in range [0.0, 1.0] that each unit of the loot item will be excluded
items:
  "COAL": 0.5
  "DIAMOND": 0.5
  "NETHERITE_INGOT": 0.5
  # ... ships with 17 valuables at 0.5
```

Existing terrain is not affected.

---

### 🖼️ InvisibleItemFrames

> **Platform:** Paper

Toggle item frame visibility and glow with **sneak + right-click**. Each click cycles the frame through four states: visible → invisible → invisible + glowing → visible + glowing. No commands or config; see `/atemframe` below for the admin bulk tool.

---

### 🔐 Auth

> **Platform:** Forge · NeoForge

Login/registration gate for offline-mode servers.

| Command                          | Permission          | Description                       |
|----------------------------------|---------------------|-----------------------------------|
| `/register <password> <confirm>` | —                   | Create a new account              |
| `/login <password>` (alias `/l`) | —                   | Log in to your existing account   |
| `/unregister <username>`         | `aspekt.unregister` | Delete a player's account (admin) |

**Mechanics:**

- Unauthenticated players are frozen in place; block breaking, interaction, attacking, item use, and item pickup are blocked until login.
- Reconnecting from the same IP address logs you in automatically.
- Passwords are stored as **SHA-256 hashes** in an embedded H2 database (`<datafolder>/auth/`) — never in plain text.
- Auth messages are configurable in `<datafolder>/auth/translation.yml`.

---

### 🌍 RTP (Random Teleport)

> **Platform:** Forge · NeoForge

Teleports the player to a random safe location.

| Command | Permission | Description                        |
|---------|------------|------------------------------------|
| `/rtp`  | —          | Teleport to a random safe location |

**Mechanics:**

- Asynchronous search: picks random coordinates, force-loads the chunk, scans for a safe non-liquid column, and pre-generates the surrounding chunks before teleporting.
- Rejects destinations near dangerous modded bosses/entities (configurable `hazard_namespaces` mod-ID blacklist).
- 10-second per-player cooldown; a global cap limits concurrent searches.

**Config** (`rtp.yml`):

```yaml
min_x: -100000
max_x: 100000
min_z: -100000
max_z: 100000
min_y: 30
max_y: 100
max_search_jobs: 1        # Max concurrent searches server-wide
max_retry_count: 32       # Attempts before giving up
hazard_namespaces: # Mod IDs whose entities make a spot unsafe
  - "mowziesmobs"
  - "cataclysm"
  - "bosses_of_mass_destruction"
  - "conjurer_illager"
  - "brutalbosses"
  - "create"
```

On Paper, `/rtpbypass <player>` (see Admin Commands) provides Essentials-backed random teleportation instead.

---

### 🏡 SetHome

> **Platform:** Forge · NeoForge

Personal named home locations that persist across sessions.

| Command           | Permission | Description                                                               |
|-------------------|------------|---------------------------------------------------------------------------|
| `/sethome <name>` | —          | Save your current location (overwrites an existing home of the same name) |
| `/home <name>`    | —          | Teleport to a saved home                                                  |
| `/delhome <name>` | —          | Delete a saved home                                                       |

Tab completion lists your saved home names. There is no home-count limit.

---

### ↔️ TPA (Teleport Request)

> **Platform:** Forge · NeoForge

Player-to-player teleport requests with an accept/deny workflow.

| Command             | Permission | Description                               |
|---------------------|------------|-------------------------------------------|
| `/tpa <player>`     | —          | Request to teleport to another player     |
| `/tpahere <player>` | —          | Request another player to teleport to you |
| `/tpaccept`         | —          | Accept all pending requests aimed at you  |
| `/tpadeny`          | —          | Deny all pending requests aimed at you    |
| `/tpacancel`        | —          | Cancel your own outgoing request          |

Requests expire automatically after **30 seconds**.

---

### 🌐 Online Simulator

> **Platform:** BungeeCord

Displays a plausible fake online player count in the server list ping.

- Generates a random count from time-of-day brackets (2–4 at night, up to 6–15 at prime time), refreshed every 30 seconds.
- Sets max players to `displayed + 5` so the server never appears full, and fills the hover sample with placeholder player names.

No commands or configuration.

---

## Admin Commands

| Command                                     | Platform             | Permission         | Description                                                                              |
|---------------------------------------------|----------------------|--------------------|------------------------------------------------------------------------------------------|
| `/aesreload`                                | Paper·Forge·NeoForge | `aspekt.reload`    | Reload all plugin configuration files                                                    |
| `/tellchat <* \| player> <message>`         | Paper                | `aspekt.tellchat`  | Send a raw message to one player or everyone (`*`)                                       |
| `/maxonline <count>`                        | Paper                | `aspekt.maxonline` | Set the server's max player count (until restart)                                        |
| `/atemframe [isVisible] [isFixed] [radius]` | Paper                | `aspekt.atemframe` | Bulk-set visibility/fixation of item frames within a radius (defaults: `false false 20`) |
| `/invclose <player>`                        | Paper                | —                  | Close another player's open inventory                                                    |
| `/rtpbypass <player>`                       | Paper                | `aspekt.rtpbypass` | Randomly teleport a player via EssentialsX (requires EssentialsX)                        |

---

## Localization

All player-facing messages live in `translations.yml` in the plugin data folder (Auth uses its own `auth/translation.yml`). Defaults are in **Russian**; every string can be freely rewritten. Messages support legacy `&` color codes and `&#RRGGBB` hex.

---

## Building from Source

```bash
git clone https://github.com/Astra-Interactive/AspeKt.git
cd AspeKt
./gradlew shadowJar
```

Jars are named `AspeKt-<platform>-<version>.jar` and are output to the `jars/` directory (or `build/<platform>/plugins|mods/` if that folder exists).

---

## Support

- 🐛 Bug reports & feature requests → [GitHub Issues](https://github.com/Astra-Interactive/AspeKt/issues)
- 🌐 Community → [empireprojekt.ru](https://empireprojekt.ru)

---

## 💜 Support Us

If our projects help you, consider supporting their development.

<table>
<tr>
<td align="center" width="130">
<img src="https://cdn.simpleicons.org/bitcoin/F7931A" width="25" alt="BTC"/><br/>
<sub><b>Bitcoin</b></sub>
</td>
<td>

```text
bc1q9a8dr55jgfae0mhevw3vvczegjv0khfp0ngrnv
```

</td>
</tr>
<tr>
<td align="center" width="130">
<img src="https://cdn.simpleicons.org/ethereum/627EEA" width="25" alt="ETH"/><br/>
<sub><b>Ethereum</b></sub>
</td>
<td>

```text
0x0BaAeEA44Ce08c8DC139224ff57563695B30d423
```

</td>
</tr>
<tr>
<td align="center" width="130">
<img src="https://cdn.simpleicons.org/boosty/F15F2C" width="25" alt="Boosty"/><br/>
<sub><b>Boosty</b></sub>
</td>
<td align="center">
<a href="https://boosty.to/empireprojekt/donate">
<img width="70%" src="https://img.shields.io/badge/Donate-Boosty-F15F2C?style=for-the-badge&logo=boosty&logoColor=white" alt="Donate on Boosty"/>
</a>
</td>
</tr>
</table>
