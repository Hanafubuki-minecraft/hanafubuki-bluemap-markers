# HanafubukiBlueMapMarkers

Paper-плагин для сервера **Hanafubuki**: игроки сами ставят метки на веб-карту [BlueMap](https://bluemap.bluecolored.de/).

- Метка ставится на текущее место игрока, работает и для Bedrock (Geyser/Floodgate)
- Лимит меток на игрока
- Отдельный слой «Метки игроков» на карте, его можно скрыть
- Метки хранятся в `plugins/HanafubukiBlueMapMarkers/markers.yml` и переживают рестарт и `/bluemap reload`
- Без BlueMap плагин тоже работает: метки сохраняются и появятся на карте после его установки

## Команды

| Команда | Описание | Право |
|---|---|---|
| `/marker add <название>` | Поставить метку на своём месте | `hanafubuki.marker.use` (все) |
| `/marker remove <название>` | Удалить свою метку | `hanafubuki.marker.use` |
| `/marker list` | Список своих меток | `hanafubuki.marker.use` |
| `/marker adminremove <игрок> <название>` | Удалить чужую метку | `hanafubuki.marker.admin` (OP) |

Алиас: `/метка`. Название метки: 1–32 символа (буквы, цифры, пробел, `-`, `_`).

## Конфиг

```yaml
markers:
  max-per-player: 3
```

## Требования

- Paper 1.21.x, Java 21
- BlueMap 5.x (необязательно)

## Сборка

```bash
gradle build
# jar: build/libs/HanafubukiBlueMapMarkers-<версия>.jar
```

## Релизы

Версия плагина задаётся в `gradle.properties`. При пуше в `main` с новой версией GitHub Actions собирает jar и публикует релиз `v<версия>`. Если версия не менялась, проект просто собирается для проверки.

## Лицензия

MIT
