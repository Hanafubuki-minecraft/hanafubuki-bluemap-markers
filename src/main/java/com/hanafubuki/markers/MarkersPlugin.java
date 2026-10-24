package com.hanafubuki.markers;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class MarkersPlugin extends JavaPlugin {

    private PlayerMarkers markers;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        markers = new PlayerMarkers(this, getConfig().getInt("markers.max-per-player", 3));
        markers.enable();

        MarkerCommand cmd = new MarkerCommand(markers);
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
}
