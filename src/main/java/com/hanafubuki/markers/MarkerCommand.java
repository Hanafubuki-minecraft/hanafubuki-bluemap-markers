package com.hanafubuki.markers;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.*;

import static com.hanafubuki.markers.Messages.ph;

public final class MarkerCommand implements TabExecutor {

    private static final String USE = "hanafubuki.marker.use";
    private static final String ADMIN = "hanafubuki.marker.admin";

    private final MarkersPlugin plugin;
    private final PlayerMarkers markers;
    private final Messages msg;

    public MarkerCommand(MarkersPlugin plugin, PlayerMarkers markers, Messages msg) {
        this.plugin = plugin;
        this.markers = markers;
        this.msg = msg;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            help(sender, label);
            return true;
        }
        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "add" -> add(sender, label, args);
            case "remove" -> remove(sender, label, args);
            case "list" -> list(sender, label);
            case "adminremove" -> adminRemove(sender, label, args);
            case "language", "lang" -> language(sender, args);
            case "reload" -> reload(sender);
            default -> help(sender, label);
        }
        return true;
    }

    private void add(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player p)) { msg.send(sender, "players-only"); return; }
        if (!p.hasPermission(USE)) { msg.send(sender, "no-permission"); return; }
        if (args.length < 2) { msg.send(sender, "usage-add", ph("label", label)); return; }
        String name = rest(args, 1).trim();
        switch (markers.add(p, name)) {
            case OK -> msg.send(sender, "add-ok", ph("name", name));
            case BAD_NAME -> msg.send(sender, "bad-name", ph("max", markers.settings().maxNameLength()));
            case WORLD_DISABLED -> msg.send(sender, "world-disabled");
            case LIMIT -> msg.send(sender, "limit",
                    ph("limit", markers.settings().maxPerPlayer()), ph("label", label));
            case EXISTS -> msg.send(sender, "exists");
            default -> { }
        }
    }

    private void remove(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player p)) { msg.send(sender, "players-only"); return; }
        if (!p.hasPermission(USE)) { msg.send(sender, "no-permission"); return; }
        if (args.length < 2) { msg.send(sender, "usage-remove", ph("label", label)); return; }
        String name = rest(args, 1).trim();
        if (markers.remove(p.getUniqueId(), name) == PlayerMarkers.Result.OK) {
            msg.send(sender, "remove-ok", ph("name", name));
        } else {
            msg.send(sender, "remove-not-found", ph("label", label));
        }
    }

    private void list(CommandSender sender, String label) {
        if (!(sender instanceof Player p)) { msg.send(sender, "players-only"); return; }
        if (!p.hasPermission(USE)) { msg.send(sender, "no-permission"); return; }
        List<PlayerMarkers.Marker> own = markers.ownedBy(p.getUniqueId());
        if (own.isEmpty()) { msg.send(sender, "list-empty", ph("label", label)); return; }
        String limit = markers.isUnlimited(p) ? "∞" : String.valueOf(markers.settings().maxPerPlayer());
        msg.send(sender, "list-header", ph("count", own.size()), ph("limit", limit));
        for (PlayerMarkers.Marker m : own) {
            msg.line(sender, "list-entry", ph("name", m.name()), ph("world", m.world()),
                    ph("x", (int) Math.floor(m.x())), ph("y", (int) m.y()), ph("z", (int) Math.floor(m.z())));
        }
    }

    private void adminRemove(CommandSender sender, String label, String[] args) {
        if (!sender.hasPermission(ADMIN)) { msg.send(sender, "no-permission"); return; }
        if (args.length < 3) { msg.send(sender, "usage-adminremove", ph("label", label)); return; }
        Optional<UUID> owner = markers.ownerByName(args[1]);
        if (owner.isEmpty()) { msg.send(sender, "admin-no-markers", ph("player", args[1])); return; }
        String name = rest(args, 2).trim();
        if (markers.remove(owner.get(), name) == PlayerMarkers.Result.OK) {
            msg.send(sender, "admin-remove-ok", ph("name", name), ph("player", args[1]));
        } else {
            msg.send(sender, "admin-remove-not-found", ph("player", args[1]));
        }
    }

    private void language(CommandSender sender, String[] args) {
        if (!sender.hasPermission(ADMIN)) { msg.send(sender, "no-permission"); return; }
        String available = String.join(", ", plugin.availableLanguages());
        if (args.length < 2) {
            msg.send(sender, "language-current", ph("language", msg.code()), ph("available", available));
            return;
        }
        String code = args[1].toLowerCase(Locale.ROOT);
        if (plugin.setLanguage(code)) {
            msg.send(sender, "language-set", ph("language", code)); // уже на новом языке
        } else {
            msg.send(sender, "language-unknown", ph("language", code), ph("available", available));
        }
    }

    private void reload(CommandSender sender) {
        if (!sender.hasPermission(ADMIN)) { msg.send(sender, "no-permission"); return; }
        plugin.reloadAll();
        msg.send(sender, "reloaded");
    }

    private void help(CommandSender s, String label) {
        msg.send(s, "help-header");
        msg.line(s, "help-add", ph("label", label));
        msg.line(s, "help-remove", ph("label", label));
        msg.line(s, "help-list", ph("label", label));
        if (s.hasPermission(ADMIN)) {
            msg.line(s, "help-adminremove", ph("label", label));
            msg.line(s, "help-language", ph("label", label));
            msg.line(s, "help-reload", ph("label", label));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        boolean admin = sender.hasPermission(ADMIN);
        if (args.length == 1) {
            List<String> subs = new ArrayList<>(List.of("add", "remove", "list"));
            if (admin) subs.addAll(List.of("adminremove", "language", "reload"));
            return filter(subs, args[0]);
        }
        String sub = args[0].toLowerCase(Locale.ROOT);
        if (args.length == 2 && sub.equals("remove") && sender instanceof Player p) {
            return filter(markers.ownedBy(p.getUniqueId()).stream().map(PlayerMarkers.Marker::name).toList(), args[1]);
        }
        if (args.length == 2 && (sub.equals("language") || sub.equals("lang")) && admin) {
            return filter(plugin.availableLanguages(), args[1]);
        }
        if (args.length == 2 && sub.equals("adminremove") && admin) {
            return filter(markers.ownerNames(), args[1]);
        }
        if (args.length == 3 && sub.equals("adminremove") && admin) {
            return markers.ownerByName(args[1])
                    .map(u -> filter(markers.ownedBy(u).stream().map(PlayerMarkers.Marker::name).toList(), args[2]))
                    .orElse(List.of());
        }
        return List.of();
    }

    private static String rest(String[] args, int from) {
        return String.join(" ", Arrays.copyOfRange(args, from, args.length));
    }

    private static List<String> filter(List<String> options, String prefix) {
        String p = prefix.toLowerCase(Locale.ROOT);
        return options.stream().filter(o -> o.toLowerCase(Locale.ROOT).startsWith(p)).toList();
    }
}
