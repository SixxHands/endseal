# EndSeal

A tiny NeoForge 1.21.1 mod that **silently refuses End portal activation** until you decide to open it.

The End Stronghold and its portal frame still generate in the world normally — players can find it,
build around it, explore it. What they can't do is **complete/ignite the portal**. When they try to
seat the final Eye of Ender, the insertion is refused and they get a short action-bar message so it
reads as **intentional design, not a bug**.

This pairs with your Weather-Reincarnated-style gating: just as you make players work for the eyes,
EndSeal lets you keep the End itself sealed behind the scenes — ship the pack now, open the End later
as a server event when your End content is ready.

## How it works (no hacks)
- Hooks `PlayerInteractEvent.RightClickBlock` on the server, which fires *before* vanilla inserts the eye.
- Acts only when: the seal is on, the player holds an Eye of Ender, and the target is an
  End Portal Frame without an eye.
- Cancels the interaction so the eye never seats / the portal never forms.
- No polling, no breaking portal blocks after the fact, no `@Overwrite`. Depends only on stable vanilla
  API (`EndPortalFrameBlock`, `HAS_EYE`, `EndPortalShape`), so it's robust across updates.

## Two modes (config: `allowPartialFrame`)
- **`true` (default):** players can insert eyes up to — but not including — the **completing** eye.
  The frame can look almost-ready; only the final ignition is refused. Best "sealed door" feel.
- **`false`:** no eye can be inserted at all. The frame stays empty.

## Config (`config/endseal-common.toml`)
- `sealed` (default `true`): master switch. Set `false` to open the End.
- `allowPartialFrame` (default `true`): see above.
- `messages`: the action-bar lines shown on refusal. One is picked at random. Edit freely; keep them
  short (the action bar truncates). Defaults:
  - "The portal is not ready to be opened yet."
  - "Something still holds the gateway shut."
  - "The eye will not seat. The way is not yet open."
  - "The portal sleeps. It is not yet time."

## Opening the End live (command)
`/endseal open` — unseal (portal can be completed)
`/endseal close` — re-seal
`/endseal status` — check current state
Requires op (permission level 2). Lets you "open the End" as a live server event without editing files.
(If you'd rather drive everything from the config file, you can delete `EndSealCommand.java`.)

## Build
1. Drop this folder into your dev environment (or use it as a standalone Gradle project).
2. Set the `neoForge { version = "..." }` in `build.gradle` to match your modpack's NeoForge build.
3. `./gradlew build` → jar appears in `build/libs/`.
4. Drop the jar in your modpack's `mods/` folder alongside BAMF Nether.

## Notes / honesty
- The completing-eye detection (`allowPartialFrame=true`) approximates vanilla's `EndPortalShape`
  check using frame presence + eye state across candidate 3x3 centers. It reliably catches the
  completing eye for sealing purposes. If you ever see an edge case (e.g. overlapping frame layouts),
  switch to `allowPartialFrame=false` for an absolute guarantee.
- Server-side only in effect; safe on `BOTH` sides. No client mixins, no rendering changes.
- This is a behavior mod with no assets; it won't conflict with your resource pack.

## Server-side only
This mod is **server-side only**. Install it in the dedicated server's `mods/` folder; clients do
**not** need it. The `neoforge.mods.toml` sets `side="SERVER"` on the dependencies and
`displayTest="IGNORE_ALL_VERSION"` on the mod, so vanilla-NeoForge clients (or clients with a
different mod list) can connect without having EndSeal.

Why this works with zero client code:
- All logic runs on the server (`PlayerInteractEvent` is handled server-side; we gate on
  `!level.isClientSide()`).
- The refusal message is sent with `ServerPlayer.displayClientMessage(..., true)`, which is a
  standard system/action-bar packet that any vanilla client renders. No client mod required.
- There are no assets, no client mixins, no rendering.

Note: in single-player (which runs an integrated server), the mod still works because the
integrated server loads server-side mods. If you only ever run it on a dedicated server, clients
stay completely vanilla-compatible.
