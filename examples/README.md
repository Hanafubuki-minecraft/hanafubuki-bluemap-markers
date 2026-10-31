# HanafubukiBlueMapMarkers: примеры конфигурации

Метки игроков на веб-карте BlueMap: `/marker add`, `/marker list`, `/marker remove`.

## Файлы

| Файл | Что это |
|---|---|
| [`config.full.yml`](config.full.yml) | Полный `config.yml` по умолчанию со всеми комментариями |
| [`more-markers.yml`](more-markers.yml) | Больше меток на игрока, длиннее названия, без меток в Энде |
| [`hidden-layer.yml`](hidden-layer.yml) | Слой меток скрыт на карте по умолчанию и со своим названием |
| [`russian-command.yml`](russian-command.yml) | Основная команда /метка вместо /marker |

## Как пользоваться

Готовые примеры — это **кусочки** конфига. Перенеси нужные строки в `plugins/HanafubukiBlueMapMarkers/config.yml` (не заменяй весь файл), затем выполни `/marker reload` (имя команды — после рестарта).

## Все настройки

Таблица собрана из комментариев самого `config.yml`, поэтому совпадает с текущей версией плагина.

| Ключ | По умолчанию | Что делает |
|---|---|---|
| `language` | `en` | Language of in-game messages: en, ru — or any file you add to plugins/HanafubukiBlueMapMarkers/lang/ Can also be changed in-game: /marker language <code> |
| `markers.max-per-player` | `3` | How many markers one player can have. Players with the permission hanafubuki.marker.unlimited ignore this limit. |
| `markers.max-name-length` | `32` | Maximum length of a marker name (1–64) |
| `markers.disabled-worlds` | `[]` | Worlds where players can't place markers, e.g. [world_the_end] |
| `map.layer-label` | `""` | Name of the layer in the BlueMap web UI. Leave empty to use the current language. |
| `map.default-hidden` | `false` | Hide the layer by default (visitors can still turn it on in the web UI) |
| `map.max-distance` | `100000` | Markers are hidden when the map camera is further away than this (in blocks) |
| **`commands`** | | Commands: name, aliases, enabled. Restart the server to apply. |
| `commands.marker` | `{ enabled: true, name: marker, aliases: [markers, метка] }` | Команда: `enabled` — включена ли, `name` — имя, `aliases` — алиасы. Меняется после рестарта |
| **`style`** | | Palette of all messages (MiniMessage tags in lang files: <accent> <text> <muted> <dim> <success> <error> <gold>, <symbol>) Hanafubuki style: https://github.com/Hanafubuki-minecraft/hanafubuki-brand/blob/main/STYLE.md |
| `style.symbol` | `"❀"` | Символ в префиксе сообщений (тег `<symbol>`) |
| `style.accent` | `"#F7A1C0"` | Акцент — сакура (тег `<accent>`) |
| `style.text` | `"#F4EEF6"` | Основной текст (`<text>`) |
| `style.muted` | `"#C9BED3"` | Приглушённый текст (`<muted>`) |
| `style.dim` | `"#9488A6"` | Тихий, служебный текст (`<dim>`) |
| `style.success` | `"#A8E6B5"` | Успех (`<success>`) |
| `style.error` | `"#FF8A9B"` | Ошибка (`<error>`) |
| `style.gold` | `"#FFCF6B"` | Золото, редкие акценты (`<gold>`) |
