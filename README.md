<p align="center">
  <img src="assets/logo.png" alt="AspeKt Logo" width="160"/>
</p>

<h1 align="center">AspeKt</h1>
<p align="center"><strong>A lightweight, Kotlin-powered essentials plugin for Paper, Forge & NeoForge servers</strong></p>

---

## What is AspeKt?

**AspeKt** is a high-performance, modular essentials plugin for Minecraft servers built entirely in **Kotlin**. It ships a curated set of survival-quality features — economy, land claims, teleportation, chat games, tree-capitation, player protection, and more — without the bloat of traditional "kitchen-sink" plugins.

Originally built for the **EmpireProjekt** community ([empireprojekt.ru](https://empireprojekt.ru)), AspeKt is free and open to all server owners.

---

## Soft Dependencies

These plugins are **optional**. AspeKt integrates with them when present and falls back gracefully otherwise.

| Plugin             | Integration                                                   |
|--------------------|---------------------------------------------------------------|
| **Vault**          | Economy bridge — exposes AspeKt currencies as a Vault economy |
| **PlaceholderAPI** | `%ekon_balance_<id>%` — player balance placeholders           |
| **LuckPerms**      | Permission resolution                                         |
| **EssentialsX**    | Cross-plugin compatibility                                    |
| **packetevents**   | Packet-level swear filter on signs (optional enhancement)     |

---

## Building from Source

```bash
git clone https://github.com/Astra-Interactive/AspeKt.git
cd AspeKt
./gradlew shadowJar
```

Jars are output to the `jars/` directory (or `build/<platform>/plugins/` if that folder exists).

---

## Features

---

### 🌲 TreeCapitator

> **Platform:** Paper / Bukkit

Fells an entire tree by breaking a single log while sneaking with an axe.

**Mechanics:**

- Trigger: break any log while sneaking and holding an axe.
- Recursively breaks all connected logs (and optionally leaves) up to the configured limit.
- Optionally replants a sapling on the dirt block below the original log.
- Applies normal tool durability per block, respecting the Unbreaking enchantment.
- Supports all vanilla wood types — stripped logs and Nether stems (Crimson / Warped) included.

**Config** (`treecapitator.yml`):

| Key                      | Default | Description                                           |
|--------------------------|---------|-------------------------------------------------------|
| `enabled`                | `true`  | Enable or disable the module                          |
| `destroy_limit`          | `16`    | Maximum blocks broken in a single chop                |
| `damage_axe`             | `true`  | Apply durability damage to the axe                    |
| `break_axe`              | `true`  | Allow the axe to break from durability loss           |
| `replant`                | `true`  | Automatically place a sapling after chopping          |
| `replant_max_iterations` | `16`    | How far down to search for a dirt block to replant on |
| `destroy_leaves`         | `true`  | Remove leaves when the tree is felled                 |

---

### 🏠 Claims

> **Platform:** Paper / Bukkit · NeoForge · Forge

Chunk-based land protection. Players claim the chunk they stand in and control exactly what is allowed inside it.

**Commands:**

| Command                            | Description                                    |
|------------------------------------|------------------------------------------------|
| `/claim`                           | Claim the chunk you are standing in            |
| `/unclaim`                         | Unclaim the chunk you are standing in          |
| `/claim map`                       | Display a 5 × 5 minimap of nearby claims       |
| `/claim flag <flag> <true\|false>` | Toggle a protection flag on your claimed chunk |
| `/claim addmember <player>`        | Grant a player member access to your claim     |
| `/claim removemember <player>`     | Revoke a player's member access                |

**Chunk flags** (controlled via `/claim flag`):

| Flag                      | Controls                      |
|---------------------------|-------------------------------|
| `ALLOW_BREAK`             | Block breaking                |
| `ALLOW_PLACE`             | Block placement               |
| `ALLOW_INTERACT`          | Block and entity interaction  |
| `ALLOW_EXPLODE`           | Explosion block damage        |
| `ALLOW_EMPTY_BUCKET`      | Emptying buckets (water/lava) |
| `ALLOW_SPREAD`            | Fire and lava spreading       |
| `ALLOW_RECEIVE_DAMAGE`    | PvP damage inside the claim   |
| `ALLOW_HOSTILE_MOB_SPAWN` | Hostile mob spawning          |
| `ALLOW_ICE_MELT`          | Ice melting                   |

Members of a claim bypass all flags that are disabled for outsiders.

---

### 💰 Economy (ekon)

> **Platform:** Paper / Bukkit

Multi-currency economy with full Vault integration and PlaceholderAPI support.

**Commands:**

| Command                                  | Permission           | Description                      |
|------------------------------------------|----------------------|----------------------------------|
| `/ekon list`                             | `aspekt.admin_claim` | List all registered currencies   |
| `/ekon top <currency> [page]`            | `aspekt.admin_claim` | Show the top-balance leaderboard |
| `/ekon balance <currency> <player>`      | `aspekt.admin_claim` | Check a player's balance         |
| `/ekon set <currency> <player> <amount>` | `aspekt.economy.set` | Set a player's balance exactly   |
| `/ekon add <currency> <player> <amount>` | `aspekt.economy.set` | Add to a player's balance        |

**PlaceholderAPI:** `%ekon_balance_<currency_id>%`

**Config** (`currencies.yml`):

```yaml
currencies:
  Gold:
    id: "gold"                # Internal ID used in commands and placeholders
    name: "Gold Coins"        # Display name shown in messages
    priority: 0               # 0–4; highest priority = default Vault currency
should_sync: false            # Sync balances across server network
```

---

### 🎛️ Menu

> **Platform:** Paper / Bukkit

Define fully custom GUI menus entirely in YAML — no coding required. Supports prices, permission gates, command rewards, and per-item visibility rules.

**Commands:**

| Command        | Description                  |
|----------------|------------------------------|
| `/menu`        | Open the first defined menu  |
| `/menu <name>` | Open a specific menu by name |
| `/invclose`    | Close your current inventory |

**Config** (`menus.yml`):

```yaml
menus:
  example_menu:
    command: "example_menu"         # Alias used with /menu <name>
    title: "My Menu"
    size: CHEST_3_ROWS              # CHEST_1_ROWS … CHEST_6_ROWS
    update_interval: 1000           # Optional auto-refresh interval in ms
    items:
      my_item:
        index: 13                   # Slot index (0-based)
        material: "DIAMOND"
        name: "<gold>Click me!"     # MiniMessage format
        lore:
          - "<gray>Costs 100 Gold"
        amount: 1
        custom_model_data: 0
        permission: "optional.permission.node"
        price:
          type: "money"             # "money" | "nothing"
          amount: 100.0
        reward:
          type: "console_command"   # "console_command" | "player_command" | "nothing"
          commands:
            - "give {PLAYER} diamond 1"
        visibility_conditions: # Item hidden unless conditions pass
          - type: "permission"
            permission: "some.permission"
            is_inverted: false
        clickable_conditions: # Item not clickable unless conditions pass
          - type: "permission"
            permission: "some.other.permission"
            is_inverted: false
```

`{PLAYER}` in commands is replaced with the clicking player's name.

---

### 🪑 Sit

> **Platform:** Paper / Bukkit

Players can sit on stairs and slabs by right-clicking with an empty hand.

**Commands:**

| Command | Description                |
|---------|----------------------------|
| `/sit`  | Dismount your current seat |

**Config** (`sit.yml`):

| Key          | Default | Description                  |
|--------------|---------|------------------------------|
| `is_enabled` | `true`  | Enable or disable the module |

**Mechanics:**

- Right-click a stair or slab with an empty hand to sit.
- Sneaking while right-clicking does **not** trigger sitting.
- Players are dismounted automatically on death, teleport, or disconnect.

---

### 🧩 ChatGame (Quiz)

> **Platform:** Paper / Bukkit

Periodically posts a challenge in chat. The first player to type the correct answer wins a money reward.

**Commands:**

| Command          | Description                                |
|------------------|--------------------------------------------|
| `/quiz <answer>` | Submit your answer to the active challenge |

**Config** (`chatgame.yml`):

```yaml
isEnabled: true
timer:
  initialDelaySeconds: 10      # Delay before the first game starts
  delaySeconds: 300            # Interval between games
defaultReward:
  type: "money"
  minAmount: 0.0
  maxAmount: 100.0
  currencyId: null             # null = use the default currency
chat_games:
  - type: "RIDDLE"
    question: "What has keys but no locks?"
    answer: "keyboard"
    reward:
      type: "MONEY"
      minAmount: 40.0
      maxAmount: 50.0
      currency_id: null
  - type: "SUM_OF_TWO"            # e.g. "What is 7 + 8?"
  - type: "TIMES_OF_TWO"          # e.g. "What is 6 × 9?"
  - type: "EQUATION_EASY"         # e.g. "Solve: 2x + 3 = 11"
  - type: "EQUATION_QUADRATIC"    # Quadratic equation
  - type: "ANAGRAM"
    words: [ "creeper", "enderman" ]
```

**Game types:**

| Type                 | Description                                |
|----------------------|--------------------------------------------|
| `RIDDLE`             | Custom question with a fixed answer        |
| `SUM_OF_TWO`         | Random addition problem                    |
| `TIMES_OF_TWO`       | Random multiplication problem              |
| `EQUATION_EASY`      | Simple linear equation (e.g. `2x + 3 = 7`) |
| `EQUATION_QUADRATIC` | Quadratic equation                         |
| `ANAGRAM`            | Scrambled word chosen from your word list  |

---

### 🛡️ NewBee (New Player Protection)

> **Platform:** Paper / Bukkit

Gives newly-joined players a temporary protection shield for their first **50 minutes** of total playtime.

**Mechanics:**

- While protected, the player receives: **Regeneration V, Absorption III, Haste IV, Fire Resistance V**.
- Incoming damage is reduced to **70%** of normal.
- The shield is immediately removed if the protected player **deals or receives PvP damage**.
- A title and message are shown on join and respawn while the shield is active.
- Protection expires automatically after 50 minutes of cumulative playtime.

No configuration file — timings and effects are defined in code.

---

### 🌍 RTP (Random Teleport)

> **Platform:** Paper / Bukkit · NeoForge · Forge

Teleports a player to a random safe location in the world.

**Commands:**

| Command      | Permission         | Description                        |
|--------------|--------------------|------------------------------------|
| `/rtp`       | —                  | Teleport to a random safe location |
| `/rtpbypass` | `aspekt.rtpbypass` | Bypass the per-player cooldown     |

**Mechanics:**

- Requires healthy server TPS (≥ 18 ms/tick) before searching to avoid lag.
- Per-player cooldown prevents teleport spam.
- A configurable concurrent-search limit avoids lag spikes during mass teleports.
- Searches for a block with solid ground and open air above.

---

### ↔️ TPA (Teleport Request)

> **Platform:** Paper / Bukkit · NeoForge · Forge

Player-to-player teleport requests with an accept/deny workflow.

**Commands:**

| Command             | Description                               |
|---------------------|-------------------------------------------|
| `/tpa <player>`     | Request to teleport to another player     |
| `/tpahere <player>` | Request another player to teleport to you |
| `/tpaccept`         | Accept the pending teleport request       |
| `/tpadeny`          | Deny the pending teleport request         |
| `/tpacancel`        | Cancel your own outgoing request          |

---

### 🏡 SetHome

> **Platform:** Paper / Bukkit · NeoForge · Forge

Personal named home locations that persist across sessions.

**Commands:**

| Command           | Description                                |
|-------------------|--------------------------------------------|
| `/sethome <name>` | Save your current location as a named home |
| `/home <name>`    | Teleport to a saved home                   |
| `/delhome <name>` | Delete a saved home                        |

Tab completion lists your saved home names.

---

### ⛓️ Jail

> **Platform:** Paper / Bukkit

Confine players to designated jail locations for a set duration.

**Commands:**

| Command                               | Permission           | Description                            |
|---------------------------------------|----------------------|----------------------------------------|
| `/jail list`                          | `aspekt.jail.list`   | List all defined jails                 |
| `/jail create <name>`                 | `aspekt.jail.create` | Create a jail at your current location |
| `/jail delete <name>`                 | `aspekt.jail.delete` | Delete a jail (must have no inmates)   |
| `/jail inmate <jail> <player> <time>` | `aspekt.jail.inmate` | Send a player to jail for a duration   |
| `/jail free <player>`                 | `aspekt.jail.free`   | Release a jailed player immediately    |

**Time format examples:** `30s`, `10m`, `1h`, `1h30m`

**Mechanics:**

- Jailed players are teleported to the jail location on imprisonment.
- On release (automatic or manual) the player is teleported back to their pre-jail location.
- A jail cannot be deleted while it still has active inmates.
- Commands are blocked while a player is in jail.

---

### 🤬 AntiSwear

> **Platform:** Paper / Bukkit

Detects profanity in chat and on signs and replaces matched text with `****`.

**Commands:**

| Command                           | Permission               | Description                                |
|-----------------------------------|--------------------------|--------------------------------------------|
| `/swearfilter <on\|off>`          | —                        | Toggle the swear filter for yourself       |
| `/swearfilter <on\|off> <player>` | `aspekt.set_swear.admin` | Force-toggle the filter for another player |

**Mechanics:**

- Regex-based detection with broad coverage of Russian profanity and common evasion patterns.
- Intercepts both chat events and sign-placement packets (requires **packetevents** for packet interception).
- Per-player preference is cached in-memory on join for fast lookups.

---

### 📢 AutoBroadcast

> **Platform:** Paper / Bukkit

Sends periodic announcements to all online players on a configurable interval.

**Config** (`announcements.yml`):

```yaml
interval: 60000     # Milliseconds between announcement cycles
announcements:
  welcome_message:
    type: "TEXT"
    text: "<green>Welcome to the server!"
  tip_bar:
    type: "ACTION_BAR"
    text: "<yellow>Tip: use /rtp to explore!"
  event_boss_bar:
    type: "BOSS_BAR"
    text: "<red>Event starts in 5 minutes!"
    barColor: "RED"            # PINK | RED | GREEN | YELLOW | BLUE | PURPLE | WHITE
    duration_seconds: 10
```

Text supports full **MiniMessage** formatting (`<red>`, `<bold>`, gradients, etc.).

---

### 📦 InventorySort

> **Platform:** Paper / Bukkit

Sorts chest or player inventory contents with a single gesture.

**Mechanics:**

- **Shift + right-click** inside any inventory to sort it.
- Items are grouped and ordered by: type, name, wool colour, glass colour, block type, or tool type — cycling through sort modes.
- The default shift-click item-transfer action is cancelled, so no items are accidentally moved.

No commands or config required.

---

### 🔥 Restrictions

> **Platform:** Paper / Bukkit

Granular toggles for potentially destructive game mechanics — useful for controlled survival environments.

**Config** (`restrictions.yml`):

```yaml
explosion:
  damage_creeper: true   # Creeper explosion block damage
  damage_other: true     # Other entity explosion damage
  destroy: true          # Explosion block destruction in general
place:
  tnt: true              # Placing TNT blocks
  lava: true             # Placing lava (bucket or block)
spread:
  lava: true             # Lava flow and spreading
  fire: true             # Fire spreading and block burning
```

Setting any value to `false` disables that mechanic server-wide.

---

### 💸 MoneyDrop

> **Platform:** Paper / Bukkit

Mobs and blocks drop physical money items on death or breakage. Other players can pick them up.

**Config** (`moneydrop.yml`):

```yaml
money_drop:
  - from: "ZOMBIE"          # Entity type or block material
    chance: 0.5             # Drop probability (0.0–1.0)
    min: 5                  # Minimum amount dropped
    max: 20                 # Maximum amount dropped
    currency_id: null       # null = default currency
```

**Mechanics:**

- Money item spawns at the death/break location when a player is the cause.
- Players walk over (or click) the item to collect it — amount is added to their balance.
- Hopper collection of money items is **blocked** to prevent automated farming.
- Piston movement is tracked to prevent item duplication exploits.
- The collected amount is broadcast to nearby players.

---

### 🌾 AutoCrop

> **Platform:** Paper / Bukkit

Instantly harvest mature crops by right-clicking them with a hoe.

**Config** (`autocrop.yml`):

| Key       | Default | Description                                                      |
|-----------|---------|------------------------------------------------------------------|
| `enabled` | `true`  | Enable or disable the module                                     |
| `min`     | `0`     | Minimum bonus drops per harvest                                  |
| `max`     | `0`     | Maximum bonus drops per harvest (random between `min` and `max`) |

**Mechanics:**

- Right-click a **fully grown crop** while holding any hoe to harvest it.
- The crop resets to age 0 (auto-replanted).
- Both the crop item and its seed are dropped.
- The hoe takes normal durability damage, respecting enchantments.
- Using a hoe with a larger radius (e.g. enchanted) harvests surrounding blocks too.
- A dupe-prevention lock ensures the same block cannot be harvested twice in the same tick.

---

### 🏆 MoneyAdvancements

> **Platform:** Paper / Bukkit

Awards currency to a player when they complete a Minecraft advancement.

**Config** (`money-advancements.yml`):

```yaml
challenge: 5000    # Reward for completing a Challenge-type advancement
goal: 1000         # Reward for completing a Goal-type advancement
task: 1000         # Reward for completing a Task-type advancement
currency_id: null  # null = use the default currency
```

The reward is broadcast to all online players with the player name and amount received.

---

### 🖼️ InvisibleItemFrames

> **Platform:** Paper / Bukkit

Toggle item frame visibility and glow with sneak + right-click.

**Mechanics:**

Each sneak + right-click on an item frame advances it through four states:

1. **Visible**, not glowing
2. **Visible**, glowing ✨
3. **Hidden**, not glowing
4. **Hidden**, glowing ✨

No commands or config required.

---

### 🔐 Auth

> **Platform:** NeoForge · Forge

Authentication and registration system for offline-mode or Forge/NeoForge servers.

**Commands:**

| Command                          | Permission          | Description                     |
|----------------------------------|---------------------|---------------------------------|
| `/register <password> <confirm>` | —                   | Create a new account            |
| `/login <password>`              | —                   | Log in to your existing account |
| `/unregister <username>`         | `aspekt.unregister` | Delete another player's account |

**Auth states:**

| State           | Meaning                              |
|-----------------|--------------------------------------|
| `NotRegistered` | No account exists for this player    |
| `NotAuthorized` | Account exists but not yet logged in |
| `Pending`       | Login / register is being processed  |
| `Authorized`    | Player is fully authenticated        |

**Mechanics:**

- Unauthenticated players are teleported back if they try to move.
- Block breaking and all cancellable events are blocked until authenticated.
- Passwords are stored as **SHA-256 hashes** — never in plain text.
- Other modules can gate their functionality on auth state via `AuthorizedApi.getAuthState(uuid)`.

---

### 🌐 Online Simulator (BungeeCord)

> **Platform:** BungeeCord proxy

Displays a realistic-looking fake online player count in the server list ping, scaled to the time of day.

**Mechanics:**

- Generates a plausible player count based on configured time-of-day ranges.
- Updates every 30 seconds.
- Also sets the max player slot count to `displayed + 5` so the server never appears "full".

No configuration file — ranges are defined in code.

---

## Admin Commands

These commands are available on Paper / Bukkit and require elevated permissions.

| Command                                     | Permission               | Description                                           |
|---------------------------------------------|--------------------------|-------------------------------------------------------|
| `/aesreload`                                | `aspekt.reload`          | Reload all plugin configuration files                 |
| `/tellchat <* \| player> <message>`         | `aspekt.tellchat`        | Send a message to a specific player or everyone (`*`) |
| `/maxonline <count>`                        | `aspekt.maxonline`       | Dynamically set the server's max player count         |
| `/atemframe [isVisible] [isFixed] [radius]` | `aspekt.atemframe`       | Bulk-configure item frames within a radius            |
| `/swearfilter <on\|off> [player]`           | `aspekt.set_swear.admin` | Force-toggle swear filter for another player          |

---
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

---