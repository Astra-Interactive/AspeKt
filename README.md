<p align="center">
  <img src="assets/logo.png" alt="AspeKt Logo" width="200"/>
</p>

<h1 align="center">AspeKt</h1>
<p align="center"><strong>A lightning-fast, Kotlin-powered essentials plugin for Paper&Forge servers</strong></p>
<p align="center">⚡ Minimal. Modular. Modern. ⚡</p>

---

## What is AspeKt?

**AspeKt** is a high-performance essentials-style plugin for modern Paper&Forge servers, built in **Kotlin** and
tailored for real-world SMP needs. Developed by the Astra-Interactive team, it replaces bloated legacy plugins with a
sleek, modular toolkit.

Originally built for the EmpireProjekt community, AspeKt is open to all.

---

## Installation

1. Download the latest `.jar` from [Releases](https://github.com/Astra-Interactive/AspeKt/releases).
2. Drop it into your server's `/plugins/` folder.
3. Start the server. Configs will auto-generate.
4. Customize features via config and optional menu files.

---

## Building from Source

```bash
git clone https://github.com/Astra-Interactive/AspeKt.git
cd AspeKt
./gradlew shadowJar
```

---

## Features

---

### TreeCapitator

Fells an entire tree by breaking a single log while sneaking with an axe.

**Mechanics:**

- Trigger: break any log while sneaking and holding an axe.
- Recursively breaks all connected logs and leaves in all 6 directions up to the configured limit.
- Optionally replants a sapling on the dirt block below the broken log.
- Applies normal tool damage per block, accounting for the Unbreaking enchantment.
- Supports all vanilla wood types including stripped logs and Nether stems (Crimson/Warped).

**Config** (`treecapitator.yml`):

| Key                      | Default | Description                                           |
|--------------------------|---------|-------------------------------------------------------|
| `enabled`                | `true`  | Enable or disable the module                          |
| `destroy_limit`          | `16`    | Maximum number of blocks broken in a single chop      |
| `damage_axe`             | `true`  | Whether to apply durability damage to the axe         |
| `break_axe`              | `true`  | Whether the axe can break from durability loss        |
| `replant`                | `true`  | Automatically place a sapling after chopping          |
| `replant_max_iterations` | `16`    | How far down to search for a dirt block to replant on |
| `destroy_leaves`         | `true`  | Remove leaves when the tree is felled                 |

---

### Claims

Chunk-based land protection. Players claim the chunk they stand in and control what is allowed inside it.

**Commands:**

| Command                               | Description                                     |
|---------------------------------------|-------------------------------------------------|
| `/claim`                              | Claim the chunk you are currently standing in   |
| `/unclaim`                            | Unclaim the chunk you are currently standing in |
| `/claim map`                          | Display a 5×5 map of nearby claims in chat      |
| `/claim setflag <flag> <true\|false>` | Toggle a protection flag on your claimed chunk  |
| `/claim addmember <player>`           | Grant a player member access to your claim      |
| `/claim removemember <player>`        | Revoke a player's member access                 |

**Chunk flags** (controlled via `/claim setflag`):

| Flag                      | Controls                      |
|---------------------------|-------------------------------|
| `ALLOW_BREAK`             | Block breaking                |
| `ALLOW_PLACE`             | Block placement               |
| `ALLOW_INTERACT`          | Block and entity interaction  |
| `ALLOW_EXPLODE`           | Explosion block damage        |
| `ALLOW_EMPTY_BUCKET`      | Emptying buckets (water/lava) |
| `ALLOW_SPREAD`            | Fire and lava spreading       |
| `ALLOW_RECEIVE_DAMAGE`    | PvP damage to players inside  |
| `ALLOW_HOSTILE_MOB_SPAWN` | Hostile mob spawning          |
| `ALLOW_ICE_MELT`          | Ice melting                   |

Members of a claim bypass all flags that are turned off for non-members.

---

### Economy (ekon)

Multi-currency economy with Vault integration and PlaceholderAPI support.

**Commands:**

| Command                                  | Permission           | Description                      |
|------------------------------------------|----------------------|----------------------------------|
| `/ekon list`                             | `aspekt.admin.claim` | List all registered currencies   |
| `/ekon top <currency> [page]`            | `aspekt.admin.claim` | Show the top-balance leaderboard |
| `/ekon balance <currency> <player>`      | `aspekt.admin.claim` | Check a player's balance         |
| `/ekon set <currency> <player> <amount>` | `aspekt.setbalance`  | Set a player's balance exactly   |
| `/ekon add <currency> <player> <amount>` | `aspekt.setbalance`  | Add to a player's balance        |

**PlaceholderAPI:** `%ekon_balance_<currency_id>%`

**Config** (`currencies.yml`):

```yaml
currencies:
  MyCurrency:
    id: "my_currency"        # Internal ID used in commands and placeholders
    name: "Display Name"     # Shown in messages
    priority: 2              # 0–4; highest priority = default currency for Vault
should_sync: false           # Sync balances across server instances
```

---

### Menu

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
          - "<gray>Costs $100"
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
        visibility_conditions: # Item is hidden unless conditions pass
          - type: "permission"
            permission: "some.permission"
            is_inverted: false
        clickable_conditions: # Item is not clickable unless conditions pass
          - type: "permission"
            permission: "some.other.permission"
            is_inverted: false
```

**Placeholder:** `{PLAYER}` in commands is replaced with the clicking player's name.

---

### Sit

Players can sit on stairs and slabs by right-clicking with an empty hand. Sneaking prevents accidental sitting.

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
- Player is dismounted automatically on death, teleport, or disconnect.
- Sneaking while right-clicking does not trigger sitting.

---

### ChatGame (Quiz)

Periodically posts a challenge in chat. The first player to answer `/quiz <answer>` wins a money reward.

**Commands:**

| Command          | Description                                |
|------------------|--------------------------------------------|
| `/quiz <answer>` | Submit your answer to the active challenge |

**Config** (`chatgame.yml`):

```yaml
isEnabled: false
timer:
  initialDelaySeconds: 10      # Delay before the first game starts
  delaySeconds: 300            # Interval between games (seconds)
defaultReward:
  type: "money"
  minAmount: 0.0
  maxAmount: 100.0
  currencyId: null             # null = default currency
chat_games:
  - type: "RIDDLE"
    reward:
      type: "MONEY"
      minAmount: 40.0
      maxAmount: 50.0
      currency_id: null    
    question: "What has keys but no locks?"
    answer: "keyboard"
  - type: "SUM_OF_TWO"          # Random addition: "What is 7 + 8?"
  - type: "TIMES_OF_TWO"        # Random multiplication
  - type: "EQUATION_EASY"       # Simple linear equation
  - type: "EQUATION_QUADRATIC"  # Quadratic equation
  - type: "ANAGRAM"
    words: [ "creeper", "enderman" ]
```

**Game types:**

| Type                 | Description                                   |
|----------------------|-----------------------------------------------|
| `RIDDLE`             | Custom question with a fixed answer           |
| `SUM_OF_TWO`         | Random addition problem                       |
| `TIMES_OF_TWO`       | Random multiplication problem                 |
| `EQUATION_EASY`      | Simple linear equation (e.g. `2x + 3 = 7`)    |
| `EQUATION_QUADRATIC` | Quadratic equation                            |
| `ANAGRAM`            | Scrambled word from your configured word list |

---

### NewBee (New Player Protection)

Gives new players a temporary protection shield for their first 50 minutes of playtime.

**Mechanics:**

- While protected, players receive: Regeneration V, Absorption III, Haste IV, Fire Resistance V.
- Incoming damage is reduced by 30%.
- The shield is removed immediately if the protected player deals or receives PvP damage.
- A title and message are shown on join and respawn while the shield is active.
- Protection expires automatically after 50 minutes of total playtime.

No configuration file — the timings and effects are set in code.

---

### RTP (Random Teleport)

Teleports a player to a random safe location in the world.

**Commands:**

| Command | Description                        |
|---------|------------------------------------|
| `/rtp`  | Teleport to a random safe location |

**Mechanics:**

- Requires the server TPS to be healthy (≥ 18 ms/tick) before searching.
- Per-player cooldown prevents spam.
- A configurable concurrent search limit avoids lag spikes.
- Searches for a block with solid ground and open air above.

---

### TPA (Teleport Request)

Player-to-player teleport requests with accept/deny workflow.

**Commands:**

| Command             | Description                               |
|---------------------|-------------------------------------------|
| `/tpa <player>`     | Request to teleport to another player     |
| `/tpahere <player>` | Request another player to teleport to you |
| `/tpaccept`         | Accept the pending teleport request       |
| `/tpadeny`          | Deny the pending teleport request         |
| `/tpacancel`        | Cancel your own outgoing request          |

---

### SetHome

Personal named home locations that persist across sessions.

**Commands:**

| Command           | Description                                |
|-------------------|--------------------------------------------|
| `/sethome <name>` | Save your current location as a named home |
| `/home <name>`    | Teleport to a saved home                   |
| `/delhome <name>` | Delete a saved home                        |

Tab completion lists your saved home names.

---

### Jail

Confine players to designated jail areas for a specified duration.

**Commands:**

| Command                               | Permission           | Description                            |
|---------------------------------------|----------------------|----------------------------------------|
| `/jail list`                          | `aspekt.jail.list`   | List all defined jails                 |
| `/jail create <name>`                 | `aspekt.jail.create` | Create a jail at your current location |
| `/jail delete <name>`                 | `aspekt.jail.delete` | Delete a jail (must be empty)          |
| `/jail inmate <jail> <player> <time>` | `aspekt.jail.inmate` | Send a player to jail for a duration   |
| `/jail free <player>`                 | `aspekt.jail.free`   | Release a jailed player immediately    |

**Time format examples:** `30s`, `10m`, `1h`, `1h30m`

**Mechanics:**

- Jailed players are teleported to the jail location.
- On release (automatic or manual) the player is teleported back to their pre-jail location.
- A jail cannot be deleted while it has active inmates.

---

### AntiSwear

Detects profanity in chat and on signs and replaces it with asterisks.

**Mechanics:**

- Regex-based detection supporting Russian and other character sets.
- Intercepts chat events and sign-placement packets.
- Per-player data is pre-cached on join for fast lookups.

No config file — the word list is compiled into the plugin. Contact the developers to adjust patterns.

---

### Broadcast

Sends periodic announcements to all online players.

**Config** (`announcements.yml`):

```yaml
interval: 60000    # Milliseconds between announcement cycles
announcements:
  welcome_message:
    type: "TEXT"                    # TEXT | ACTION_BAR | BOSS_BAR
    text: "<green>Welcome to the server!"
  tip_bar:
    type: "ACTION_BAR"
    text: "<yellow>Tip: use /rtp to explore!"
  event_boss_bar:
    type: "BOSS_BAR"
    text: "<red>Event starts in 5 minutes!"
    barColor: "RED"                 # PINK | RED | GREEN | YELLOW | BLUE | PURPLE | WHITE
    duration_seconds: 10
```

Text supports MiniMessage formatting (`<red>`, `<bold>`, gradients, etc.).

---

### InventorySort

Sorts the contents of a chest or player inventory with a single gesture.

**Mechanics:**

- Shift-right-click inside any inventory to sort its contents.
- Cancels the default shift-click item-move action so no items are accidentally transferred.

No commands or config required.

---

### Restrictions

Granular toggles for potentially destructive game mechanics.

**Config** (`restrictions.yml`):

```yaml
explosion:
  damage_creeper: true    # Enable creeper explosion block damage
  damage_other: true      # Enable other entity explosion damage
  destroy: true           # Enable explosion block destruction in general
place:
  tnt: true               # Allow placing TNT blocks
  lava: true              # Allow placing lava (bucket or block)
spread:
  lava: true              # Allow lava to flow and spread
  fire: true              # Allow fire to spread and burn blocks
```

Setting any value to `false` disables that mechanic server-wide.

---

### MoneyDrop

Mobs and blocks drop physical money items when killed or broken by a player. Other players can pick them up.

**Mechanics:**

- A money item is spawned at the death/break location when a player is the cause.
- Players walk over or click the item to collect it; the amount is added to their balance.
- Hopper collection of money items is blocked to prevent automated farming.
- Piston movement of money items is tracked to prevent duplication.
- The collected amount is broadcast to nearby players.

Configuration for which mobs/blocks drop money and the amounts is defined in the plugin config.

---

### AutoCrop

Instantly harvest mature crops by right-clicking them with a hoe.

**Config** (`autocrop.yml`):

| Key       | Default | Description                                                  |
|-----------|---------|--------------------------------------------------------------|
| `enabled` | `true`  | Enable or disable the module                                 |
| `min`     | `0`     | Minimum bonus drops per harvest                              |
| `max`     | `0`     | Maximum bonus drops per harvest (random between min and max) |

**Mechanics:**

- Right-click a fully grown crop while holding any hoe.
- The crop resets to age 0 (replanted automatically).
- Drops both the crop item and a seed.
- The hoe takes normal durability damage per use.
- A dupe-prevention lock ensures the same block cannot be harvested twice in the same tick.

---

### MoneyAdvancements

Awards currency to a player when they complete an advancement.

**Config** (`money-advancements.yml`):

```yaml
challenge: 5000    # Reward for completing a Challenge-type advancement
goal: 1000         # Reward for completing a Goal-type advancement
task: 1000         # Reward for completing a Task-type advancement
currency_id: null  # null = use the default currency
```

The completion is broadcast to all online players with the player name and amount received.

---

### InvisibleItemFrames

Toggle item frame visibility and glow with a sneak + right-click cycle.

**Mechanics:**

Each sneak + right-click on an item frame advances it through four states:

1. Visible, not glowing
2. Visible, glowing
3. Hidden, not glowing
4. Hidden, glowing

No commands or config required.

---

### Auth

Authentication and registration system, primarily intended for offline-mode or Forge servers.

**Auth states:**

| State           | Meaning                              |
|-----------------|--------------------------------------|
| `NotRegistered` | No account exists for this player    |
| `NotAuthorized` | Account exists but not yet logged in |
| `Pending`       | Login/register is being processed    |
| `Authorized`    | Player is fully authenticated        |

Integration with other modules uses the `getAuthState(uuid)` API so features can be gated behind authentication.

---

## Support & Contributing

- Open issues or PRs via [GitHub](https://github.com/Astra-Interactive/AspeKt).
- Contributions are welcome — check out the code and share ideas!
