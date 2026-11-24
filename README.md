# EndSeal

A server-side NeoForge 1.21.1 mod that stops players from activating the End portal until you choose to open it.

Strongholds and End portal frames still generate normally, so players can find them and build around them. When someone tries to seat a blocked Eye of Ender, the interaction is cancelled before vanilla inserts the eye and the player gets a short action-bar message.

## Requirements

- Minecraft 1.21.1
- NeoForge 21.1.0 or newer (server side only; clients do not need the mod installed)
- Java 21 (for building from source)

## How it works

Drop the jar into your server's `mods/` folder (or your single-player `mods/` folder, since single-player runs an integrated server). Because `neoforge.mods.toml` sets `displayTest="IGNORE_ALL_VERSION"` and all logic runs on the server, vanilla-NeoForge clients can connect without installing EndSeal.

The mod listens to `PlayerInteractEvent.RightClickBlock` when the seal is enabled, the player is holding an Eye of Ender, and the target block is an empty End Portal Frame.

- When `allowPartialFrame` is `true` (default), players can place eyes up to the eleventh eye, and only an eye that would complete a 12-frame ring around a 3x3 center is refused.
- When `allowPartialFrame` is `false`, every eye insertion is refused and the frame stays empty.

## Commands

All commands require operator permission level 2.

| Command | What it does |
| --- | --- |
| `/endseal open` | Sets `sealed` to `false` and saves the config so the End portal can be completed. |
| `/endseal close` | Sets `sealed` to `true` and saves the config so portal completion is blocked again. |
| `/endseal status` | Reports whether the End portal is currently sealed or open. |

## Configuration

Generated at `config/endseal-common.toml` under `[general]`.

| Field | Type | Default | What it controls |
| --- | --- | --- | --- |
| `sealed` | boolean | `true` | Master switch. Set to `false` (or run `/endseal open`) to let portals activate. |
| `allowPartialFrame` | boolean | `true` | Lets players insert eyes until only the completing eye remains. Set to `false` to block all eye insertions. |
| `messages` | list of strings | 4 short lines | Action-bar lines shown when an insertion is refused; one is chosen at random. |

The four default refusal lines are listed below.

- `"The portal is not ready to be opened yet."`
- `"Something still holds the gateway shut."`
- `"The eye will not seat. The way is not yet open."`
- `"The portal sleeps. It is not yet time."`

## Building

```bash
./gradlew test build
```

The built jar lands in `build/libs/endseal-1.0.0.jar`.

## Limitations

- Partial-frame detection checks the 12 standard frame positions and their `HAS_EYE` states around each candidate 3x3 center without checking individual block facing directions. If a custom map has overlapping non-standard portal frames, set `allowPartialFrame = false` to block every eye unconditionally.
- Action-bar messages truncate long lines on the client, so keep custom entries in `messages` short.

## License

MIT. See [LICENSE](LICENSE).
