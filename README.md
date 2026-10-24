<div align="center">

# HanafubukiBlueMapMarkers

**Let players place their own markers on the [BlueMap](https://bluemap.bluecolored.de/) web map — straight from the game.**

[![Build](https://github.com/ByteMe6/hanafubuki-bluemap-markers/actions/workflows/release.yml/badge.svg)](https://github.com/ByteMe6/hanafubuki-bluemap-markers/actions/workflows/release.yml)
[![Release](https://img.shields.io/github/v/release/ByteMe6/hanafubuki-bluemap-markers)](https://github.com/ByteMe6/hanafubuki-bluemap-markers/releases/latest)
[![License: GPL v3](https://img.shields.io/github/license/ByteMe6/hanafubuki-bluemap-markers)](LICENSE)
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

## Installation

1. Download the latest `HanafubukiBlueMapMarkers-x.y.z.jar` from [Releases](https://github.com/ByteMe6/hanafubuki-bluemap-markers/releases/latest).
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

Alias: `/метка`. Tab completion is available for subcommands and marker names.

## Permissions

| Permission | Default | Description |
|---|---|---|
| `hanafubuki.marker.use` | everyone | Create, remove and list own markers |
| `hanafubuki.marker.admin` | op | Remove any player's markers |

## Configuration

`plugins/HanafubukiBlueMapMarkers/config.yml`:

```yaml
markers:
  # How many markers one player can have
  max-per-player: 3
```

Markers are stored in `plugins/HanafubukiBlueMapMarkers/markers.yml`.

## Compatibility

| Platform | Status |
|---|---|
| Paper 1.21.x | ✅ Supported |
| Purpur, Pufferfish and other Paper forks | ✅ Should work |
| Spigot / CraftBukkit | ❌ Not supported (uses Paper's Adventure API) |
| Folia | ❌ Not supported |
| BlueMap 5.x (API 2.x) | ✅ Supported, optional |
| Geyser / Floodgate | ✅ Supported |

> In-game messages are currently in Russian.

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
version=1.0.0
```

Bump it and push to `main` → GitHub Actions builds the jar and publishes release `v<version>` with auto-generated notes.
Pushes without a version change and pull requests are only built and checked.

## License

[GNU General Public License v3.0](LICENSE) © ByteMe6
