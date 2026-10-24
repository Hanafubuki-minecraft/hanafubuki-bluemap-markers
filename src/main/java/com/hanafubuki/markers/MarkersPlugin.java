package com.hanafubuki.markers;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;

public final class MarkersPlugin extends JavaPlugin {

    private static final List<String> BUNDLED_LANGUAGES = List.of("en", "ru");

    private Messages messages;
    private PlayerMarkers markers;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        // дописать новые ключи в config.yml старых установок
        getConfig().options().copyDefaults(true);
        saveConfig();

        for (String lang : BUNDLED_LANGUAGES) {
            if (!new File(getDataFolder(), "lang/" + lang + ".yml").exists()) {
                saveResource("lang/" + lang + ".yml", false);
            }
        }

        messages = new Messages(this);
        markers = new PlayerMarkers(this, messages);
        reloadAll();
        markers.enable();

        MarkerCommand cmd = new MarkerCommand(this, markers, messages);
        PluginCommand command = getCommand("marker");
        if (command != null) {
            command.setExecutor(cmd);
            command.setTabCompleter(cmd);
        }
    }

    @Override
    public void onDisable() {
        if (markers != null) markers.disable();
    }

    /** Перечитать config.yml и языковой файл, применить к карте. */
    public void reloadAll() {
        reloadConfig();
        messages.load(getConfig().getString("language", "en"));
        markers.applySettings(PlayerMarkers.Settings.from(getConfig()));
    }

    /** Сменить язык: записывает в config.yml и сразу применяет. */
    public boolean setLanguage(String code) {
        if (!availableLanguages().contains(code)) return false;
        getConfig().set("language", code);
        saveConfig();
        reloadAll();
        return true;
    }

    public List<String> availableLanguages() {
        Set<String> result = new TreeSet<>(BUNDLED_LANGUAGES);
        File[] files = new File(getDataFolder(), "lang").listFiles((dir, name) -> name.endsWith(".yml"));
        if (files != null) {
            for (File f : files) result.add(f.getName().substring(0, f.getName().length() - 4).toLowerCase(Locale.ROOT));
        }
        return List.copyOf(result);
    }
}
