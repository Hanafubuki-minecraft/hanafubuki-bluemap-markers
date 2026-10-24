<div align="center">

# HanafubukiBlueMapMarkers

**Let players place their own markers on the [BlueMap](https://bluemap.bluecolored.de/) web map — straight from the game.**

[![Build](https://github.com/Hanafubuki-minecraft/hanafubuki-bluemap-markers/actions/workflows/release.yml/badge.svg)](https://github.com/Hanafubuki-minecraft/hanafubuki-bluemap-markers/actions/workflows/release.yml)
[![Release](https://img.shields.io/github/v/release/Hanafubuki-minecraft/hanafubuki-bluemap-markers)](https://github.com/Hanafubuki-minecraft/hanafubuki-bluemap-markers/releases/latest)
[![License: GPL v3](https://img.shields.io/github/license/Hanafubuki-minecraft/hanafubuki-bluemap-markers)](LICENSE)
![Paper](https://img.shields.io/badge/Paper-1.21.x-blue)
![Java](https://img.shields.io/badge/Java-21-orange)

English · [Русский](README.ru.md)

</div>

---

Made for the **Hanafubuki** vanilla+ server, but works on any Paper server with BlueMap.
A player stands where they want, types `/marker add My Base` — and the point appears on the web map instantly, no re-render needed.

## Features

- 📍 **Markers at the player's position** — one command, no coordinates to type
- 🗺️ **Separate map layer** — "Метки игроков" can be toggled on/off in the BlueMap UI
- 🌍 **All worlds** — overworld, nether, end and any custom worlds BlueMap renders
- 📱 **Bedrock players supported** — works through Geyser / Floodgate like any other command
- 🔢 **Per-player limit** — configurable, so the map doesn't turn into confetti
- 🛡️ **Safe names** — letters (incl. Cyrillic), digits, spaces, `-` and `_`; no HTML injection into the map
- 💾 **Persistent** — survives restarts and `/bluemap reload`
- 🔌 **BlueMap is optional** — without it markers are still saved and show up once BlueMap is installed
- 🌐 **Multilingual** — English by default, Russian built in, switch with one command, add your own language file

## Installation

1. Download the latest `HanafubukiBlueMapMarkers-x.y.z.jar` from [Releases](https://github.com/Hanafubuki-minecraft/hanafubuki-bluemap-markers/releases/latest).
2. Put it into `plugins/`.
3. Restart the server.

That's it — no setup required.

## Commands

| Command | Description | Permission |
|---|---|---|
| `/marker add <name>` | Place a marker where you stand | `hanafubuki.marker.use` |
| `/marker remove <name>` | Remove your marker | `hanafubuki.marker.use` |
| `/marker list` | List your markers with coordinates | `hanafubuki.marker.use` |
| `/marker adminremove <player> <name>` | Remove someone else's marker | `hanafubuki.marker.admin` |
| `/marker language [code]` | Show or change the language (saved to `config.yml`) | `hanafubuki.marker.admin` |
| `/marker reload` | Reload config and language files | `hanafubuki.marker.admin` |

Aliases: `/markers`, `/метка`. Tab completion works for subcommands, marker names and languages.

## Permissions

| Permission | Default | Description |
|---|---|---|
| `hanafubuki.marker.use` | everyone | Create, remove and list own markers |
| `hanafubuki.marker.unlimited` | op | Ignore the per-player marker limit |
| `hanafubuki.marker.admin` | op | Remove any marker, change language, reload (includes `use`) |

## Configuration

`plugins/HanafubukiBlueMapMarkers/config.yml`:

```yaml
# Language of in-game messages: en, ru — or any file in lang/
language: en

markers:
  max-per-player: 3        # per-player limit (bypass: hanafubuki.marker.unlimited)
  max-name-length: 32      # 1–64
  disabled-worlds: []      # e.g. [world_the_end]

map:
  layer-label: ""          # BlueMap layer name; empty = from language file
  default-hidden: false    # hide the layer until a visitor turns it on
  max-distance: 100000     # hide markers when zoomed out further than this
```

Apply changes with `/marker reload`. Markers are stored in `markers.yml`; new config keys are added automatically on update.

## Languages

Built in: **English** (`en`, default) and **Russian** (`ru`).

```
/marker language ru
```

The command writes `language: ru` to `config.yml` and applies it immediately — no restart.

Messages live in `plugins/HanafubukiBlueMapMarkers/lang/<code>.yml` and use [MiniMessage](https://docs.advntr.dev/minimessage/format.html) formatting, so you can freely recolor or reword them.
**Adding a language:** copy `en.yml` to e.g. `de.yml`, translate it, run `/marker language de`. Missing keys fall back to English. Pull requests with new languages are welcome!

## Compatibility

| Platform | Status |
|---|---|
| Paper 1.21.x | ✅ Supported |
| Purpur, Pufferfish and other Paper forks | ✅ Should work |
| Spigot / CraftBukkit | ❌ Not supported (uses Paper's Adventure API) |
| Folia | ❌ Not supported |
| BlueMap 5.x (API 2.x) | ✅ Supported, optional |
| Geyser / Floodgate | ✅ Supported |

## Building

The project is built by GitHub Actions — you don't need to build it locally.
If you want to anyway: Java 21 + Gradle.

```bash
gradle build
# → build/libs/HanafubukiBlueMapMarkers-<version>.jar
```

## Releasing

The plugin version lives in a single place — [`gradle.properties`](gradle.properties):

```properties
version=1.1.0
```

Bump it and push to `main` → GitHub Actions builds the jar and publishes release `v<version>` with auto-generated notes.
Pushes without a version change and pull requests are only built and checked.

## License

[GNU General Public License v3.0](LICENSE) © ByteMe6
