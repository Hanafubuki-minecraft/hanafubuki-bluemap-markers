package com.hanafubuki.markers;

import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import de.bluecolored.bluemap.api.markers.POIMarker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
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
    public static final String UNLIMITED = "hanafubuki.marker.unlimited";

    public record Marker(UUID owner, String ownerName, String name,
                         String world, double x, double y, double z) {
        String key() { return owner + ":" + name.toLowerCase(Locale.ROOT); }
    }

    public record Settings(int maxPerPlayer, int maxNameLength, Set<String> disabledWorlds,
                           String layerLabel, boolean defaultHidden, double maxDistance) {
        public static Settings from(FileConfiguration c) {
            return new Settings(
                    Math.max(0, c.getInt("markers.max-per-player", 3)),
                    Math.min(64, Math.max(1, c.getInt("markers.max-name-length", 32))),
                    Set.copyOf(c.getStringList("markers.disabled-worlds")),
                    c.getString("map.layer-label", ""),
                    c.getBoolean("map.default-hidden", false),
                    c.getDouble("map.max-distance", 100000));
        }
    }

    public enum Result { OK, BAD_NAME, LIMIT, EXISTS, NOT_FOUND, WORLD_DISABLED }

    private final JavaPlugin plugin;
    private final Messages messages;
    private final File file;
    private final Map<String, Marker> markers = new ConcurrentHashMap<>();
    private volatile Settings settings;
    // буквы (вкл. кириллицу), цифры, пробел, - и _ ; заодно защищает HTML в подписи на карте
    private volatile Pattern namePattern;
    private Consumer<BlueMapAPI> listener;

    public PlayerMarkers(JavaPlugin plugin, Messages messages) {
        this.plugin = plugin;
        this.messages = messages;
        this.file = new File(plugin.getDataFolder(), "markers.yml");
    }

    public void enable() {
        load();
        if (Bukkit.getPluginManager().getPlugin("BlueMap") != null) {
            listener = this::syncAll;
            BlueMapAPI.onEnable(listener); // срабатывает и после /bluemap reload
        } else {
            plugin.getLogger().warning("BlueMap not found — markers are saved but not shown on a map");
        }
    }

    public void disable() {
        if (listener != null) BlueMapAPI.unregisterListener(listener);
    }

    public void applySettings(Settings s) {
        this.settings = s;
        this.namePattern = Pattern.compile("[\\p{L}\\p{N} _\\-]{1," + s.maxNameLength() + "}");
        refreshMap();
    }

    public Settings settings() {
        return settings;
    }

    public boolean isUnlimited(Player p) {
        return p.hasPermission(UNLIMITED);
    }

    public Result add(Player p, String name) {
        name = name.trim();
        if (!namePattern.matcher(name).matches()) return Result.BAD_NAME;
        if (settings.disabledWorlds().contains(p.getWorld().getName())) return Result.WORLD_DISABLED;
        if (!isUnlimited(p) && ownedBy(p.getUniqueId()).size() >= settings.maxPerPlayer()) return Result.LIMIT;
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

    private void changed() {
        save();
        refreshMap();
    }

    private void refreshMap() {
        if (listener != null) BlueMapAPI.getInstance().ifPresent(this::syncAll);
    }

    private void syncAll(BlueMapAPI api) {
        Settings s = settings;
        String label = s.layerLabel() == null || s.layerLabel().isBlank() ? messages.raw("layer-label") : s.layerLabel();
        String detail = messages.raw("map-detail");
        for (World world : Bukkit.getWorlds()) {
            MarkerSet set = MarkerSet.builder()
                    .label(label)
                    .toggleable(true)
                    .defaultHidden(s.defaultHidden())
                    .build();
            for (Marker m : markers.values()) {
                if (!m.world().equals(world.getName())) continue;
                set.getMarkers().put(m.key(), POIMarker.builder()
                        .label(m.name())
                        .detail(detail.replace("<name>", m.name()).replace("<owner>", m.ownerName()))
                        .position(m.x(), m.y(), m.z())
                        .maxDistance(s.maxDistance())
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
                plugin.getLogger().warning("Broken marker in markers.yml (" + id + "): " + e.getMessage());
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
            plugin.getLogger().severe("Could not save markers.yml: " + e.getMessage());
        }
    }
}
