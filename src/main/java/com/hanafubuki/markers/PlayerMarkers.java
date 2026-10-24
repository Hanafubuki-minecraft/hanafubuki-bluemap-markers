package com.hanafubuki.markers;

import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import de.bluecolored.bluemap.api.markers.POIMarker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public final class PlayerMarkers {

    public static final String SET_ID = "player-markers";
    // буквы (вкл. кириллицу), цифры, пробел, - и _ ; заодно защищает HTML в подписи на карте
    private static final Pattern NAME = Pattern.compile("[\\p{L}\\p{N} _\\-]{1,32}");

    public record Marker(UUID owner, String ownerName, String name,
                         String world, double x, double y, double z) {
        String key() { return owner + ":" + name.toLowerCase(Locale.ROOT); }
    }

    public enum Result { OK, BAD_NAME, LIMIT, EXISTS, NOT_FOUND }

    private final JavaPlugin plugin;
    private final File file;
    private final int maxPerPlayer;
    private final Map<String, Marker> markers = new ConcurrentHashMap<>();
    private Consumer<BlueMapAPI> listener;

    public PlayerMarkers(JavaPlugin plugin, int maxPerPlayer) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "markers.yml");
        this.maxPerPlayer = maxPerPlayer;
    }

    public void enable() {
        load();
        if (Bukkit.getPluginManager().getPlugin("BlueMap") != null) {
            listener = this::syncAll;
            BlueMapAPI.onEnable(listener); // срабатывает и после /bluemap reload
        } else {
            plugin.getLogger().warning("BlueMap не найден — метки сохраняются, но не видны на карте");
        }
    }

    public void disable() {
        if (listener != null) BlueMapAPI.unregisterListener(listener);
    }

    public Result add(Player p, String name) {
        name = name.trim();
        if (!NAME.matcher(name).matches()) return Result.BAD_NAME;
        if (ownedBy(p.getUniqueId()).size() >= maxPerPlayer) return Result.LIMIT;
        Location l = p.getLocation();
        Marker m = new Marker(p.getUniqueId(), p.getName(), name, l.getWorld().getName(),
                l.getBlockX() + 0.5, l.getBlockY(), l.getBlockZ() + 0.5);
        if (markers.containsKey(m.key())) return Result.EXISTS;
        markers.put(m.key(), m);
        changed();
        return Result.OK;
    }

    public Result remove(UUID owner, String name) {
        if (markers.remove(owner + ":" + name.trim().toLowerCase(Locale.ROOT)) == null) return Result.NOT_FOUND;
        changed();
        return Result.OK;
    }

    public List<Marker> ownedBy(UUID owner) {
        return markers.values().stream().filter(m -> m.owner().equals(owner)).toList();
    }

    public Optional<UUID> ownerByName(String playerName) {
        return markers.values().stream()
                .filter(m -> m.ownerName().equalsIgnoreCase(playerName))
                .map(Marker::owner).findFirst();
    }

    public List<String> ownerNames() {
        return markers.values().stream().map(Marker::ownerName).distinct().toList();
    }

    public int maxPerPlayer() { return maxPerPlayer; }

    private void changed() {
        save();
        if (listener != null) BlueMapAPI.getInstance().ifPresent(this::syncAll);
    }

    private void syncAll(BlueMapAPI api) {
        for (World world : Bukkit.getWorlds()) {
            MarkerSet set = MarkerSet.builder()
                    .label("Метки игроков")
                    .toggleable(true)
                    .defaultHidden(false)
                    .build();
            for (Marker m : markers.values()) {
                if (!m.world().equals(world.getName())) continue;
                set.getMarkers().put(m.key(), POIMarker.builder()
                        .label(m.name())
                        .detail("<b>" + m.name() + "</b><br>" + m.ownerName())
                        .position(m.x(), m.y(), m.z())
                        .maxDistance(100000)
                        .build());
            }
            api.getWorld(world).ifPresent(bw ->
                    bw.getMaps().forEach(map -> map.getMarkerSets().put(SET_ID, set)));
        }
    }

    private void load() {
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection root = yml.getConfigurationSection("markers");
        if (root == null) return;
        for (String id : root.getKeys(false)) {
            ConfigurationSection s = root.getConfigurationSection(id);
            if (s == null) continue;
            try {
                Marker m = new Marker(UUID.fromString(s.getString("owner")), s.getString("owner-name"),
                        s.getString("name"), s.getString("world"),
                        s.getDouble("x"), s.getDouble("y"), s.getDouble("z"));
                markers.put(m.key(), m);
            } catch (Exception e) {
                plugin.getLogger().warning("Битая метка в markers.yml (" + id + "): " + e.getMessage());
            }
        }
    }

    private void save() {
        YamlConfiguration yml = new YamlConfiguration();
        int i = 0;
        for (Marker m : markers.values()) {
            String p = "markers." + i++ + ".";
            yml.set(p + "owner", m.owner().toString());
            yml.set(p + "owner-name", m.ownerName());
            yml.set(p + "name", m.name());
            yml.set(p + "world", m.world());
            yml.set(p + "x", m.x());
            yml.set(p + "y", m.y());
            yml.set(p + "z", m.z());
        }
        try {
            yml.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Не удалось сохранить markers.yml: " + e.getMessage());
        }
    }
}
