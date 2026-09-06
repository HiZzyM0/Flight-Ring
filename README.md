# 💍 Flight Ring

**Trinket slots for your inventory and a craftable Ring of Flight that lets you
fly like in creative mode.**

A mod for **Minecraft 1.21.1 (NeoForge)**, built on top of the
[Curios API](https://modrinth.com/mod/curios).

## What it does

### Trinket slots

With Curios API installed, players get extra equipment slots next to their
armor: **two ring slots** and a **back slot**. Mods that integrate with Curios
pick these up automatically — for example
[Sophisticated Backpacks](https://modrinth.com/mod/sophisticated-backpacks)
lets you wear a backpack in the back slot even with a full set of armor.

### Ring of Flight

A new item crafted from diamonds and netherite. While the ring is **anywhere
in your inventory** — hotbar, main inventory, offhand, or equipped in a ring
slot — you can fly exactly like in creative mode (double-tap jump to toggle).
Remove the ring and the flight is cleanly revoked; creative mode and other
flight sources are left untouched.

**Crafting** (shaped, 3×3):

```
💎 🔥 💎        D N D
💎    💎   =   D   D     D = diamond, N = netherite ingot
💎 💎 💎        D D D
```

Under the hood the mod grants flight through two mechanisms at once — the
official NeoForge `CREATIVE_FLIGHT` attribute and the classic "angel ring"
abilities toggle — so it stays compatible with large modpacks.

### Enderite compatibility

The [Enderite](https://modrinth.com/mod/enderite) mod's armor-set handler
force-disables flight for anyone not wearing the full enderite set, which
breaks every other flight mod. When Enderite is present, Flight Ring
unregisters that handler and re-implements all of its set bonuses faithfully
(full-set flight, enderman anger immunity, void-damage rescue teleport,
magic/fire/wither immunity, 25% damage ignore chance, no fall damage) —
minus the hostile behavior. Both mods then work side by side.

## Requirements

| | |
|---|---|
| Minecraft | 1.21.1 |
| Mod loader | [NeoForge](https://neoforged.net/) 21.1+ |
| Dependency | [Curios API](https://modrinth.com/mod/curios) 9.x (optional but recommended — needed for the trinket slots) |
| Side | client & server (servers need the mod installed) |

Without Curios the ring still works from the regular inventory; you just won't
get the extra slots.

## Installation

Download the latest `flightring-x.y.z.jar` from
[Releases](../../releases) and drop it into your `mods` folder together with
Curios API. On multiplayer servers, the server needs both mods too.

## Building from source

The project uses Gradle (8.x) with the
[ModDevGradle](https://github.com/neoforged/ModDevGradle) plugin:

```
gradle build
```

The built jar ends up in `build/libs/`.

## License

[MIT](LICENSE) — do whatever you like, attribution appreciated.
