<div align="center">

# HanafubukiBlueMapMarkers

**Игроки сами ставят метки на веб-карту [BlueMap](https://bluemap.bluecolored.de/) — прямо из игры.**

[![Build](https://github.com/ByteMe6/hanafubuki-bluemap-markers/actions/workflows/release.yml/badge.svg)](https://github.com/ByteMe6/hanafubuki-bluemap-markers/actions/workflows/release.yml)
[![Release](https://img.shields.io/github/v/release/ByteMe6/hanafubuki-bluemap-markers)](https://github.com/ByteMe6/hanafubuki-bluemap-markers/releases/latest)
[![License: GPL v3](https://img.shields.io/github/license/ByteMe6/hanafubuki-bluemap-markers)](LICENSE)
![Paper](https://img.shields.io/badge/Paper-1.21.x-blue)
![Java](https://img.shields.io/badge/Java-21-orange)

[English](README.md) · Русский

</div>

---

Сделан для vanilla+ сервера **Hanafubuki**, но работает на любом Paper-сервере с BlueMap.
Игрок встаёт куда нужно, пишет `/marker add Моя база` — и точка сразу появляется на веб-карте, без перерендера.

## Возможности

- 📍 **Метка на месте игрока** — одна команда, координаты вводить не нужно
- 🗺️ **Отдельный слой на карте** — «Метки игроков» можно скрыть в интерфейсе BlueMap
- 🌍 **Все миры** — обычный мир, незер, энд и любые другие, которые рендерит BlueMap
- 📱 **Bedrock-игроки** — работает через Geyser / Floodgate как обычная команда
- 🔢 **Лимит на игрока** — настраивается, чтобы карта не превратилась в конфетти
- 🛡️ **Безопасные названия** — буквы (вкл. кириллицу), цифры, пробел, `-` и `_`; HTML на карту не протащить
- 💾 **Сохранение** — переживает рестарт и `/bluemap reload`
- 🔌 **BlueMap не обязателен** — без него метки сохраняются и появятся на карте после его установки

## Установка

1. Скачай последний `HanafubukiBlueMapMarkers-x.y.z.jar` из [Releases](https://github.com/ByteMe6/hanafubuki-bluemap-markers/releases/latest).
2. Положи в `plugins/`.
3. Перезапусти сервер.

Всё — настраивать ничего не нужно.

## Команды

| Команда | Описание | Право |
|---|---|---|
| `/marker add <название>` | Поставить метку на своём месте | `hanafubuki.marker.use` |
| `/marker remove <название>` | Удалить свою метку | `hanafubuki.marker.use` |
| `/marker list` | Список своих меток с координатами | `hanafubuki.marker.use` |
| `/marker adminremove <игрок> <название>` | Удалить чужую метку | `hanafubuki.marker.admin` |

Алиас: `/метка`. Работает автодополнение подкоманд и названий меток.

## Права

| Право | По умолчанию | Описание |
|---|---|---|
| `hanafubuki.marker.use` | все | Ставить, удалять и смотреть свои метки |
| `hanafubuki.marker.admin` | op | Удалять метки любых игроков |

## Конфиг

`plugins/HanafubukiBlueMapMarkers/config.yml`:

```yaml
markers:
  # Сколько меток может быть у одного игрока
  max-per-player: 3
```

Метки хранятся в `plugins/HanafubukiBlueMapMarkers/markers.yml`.

## Совместимость

| Платформа | Статус |
|---|---|
| Paper 1.21.x | ✅ Поддерживается |
| Purpur, Pufferfish и другие форки Paper | ✅ Должно работать |
| Spigot / CraftBukkit | ❌ Не поддерживается (используется Adventure API из Paper) |
| Folia | ❌ Не поддерживается |
| BlueMap 5.x (API 2.x) | ✅ Поддерживается, необязателен |
| Geyser / Floodgate | ✅ Поддерживается |

## Сборка

Проект собирает GitHub Actions — локально собирать не нужно.
Если всё же хочется: Java 21 + Gradle.

```bash
gradle build
# → build/libs/HanafubukiBlueMapMarkers-<версия>.jar
```

## Релизы

Версия плагина задаётся в одном месте — [`gradle.properties`](gradle.properties):

```properties
version=1.0.0
```

Поднимаешь её и пушишь в `main` → GitHub Actions собирает jar и публикует релиз `v<версия>` с автоматическими release notes.
Пуши без смены версии и pull request'ы только собираются и проверяются.

## Лицензия

[GNU General Public License v3.0](LICENSE) © ByteMe6
