package com.hanafubuki.markers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.*;

public final class MarkerCommand implements TabExecutor {

    private static final String USE = "hanafubuki.marker.use";
    private static final String ADMIN = "hanafubuki.marker.admin";

    private final PlayerMarkers markers;

    public MarkerCommand(PlayerMarkers markers) {
        this.markers = markers;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) return help(sender, label);

        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "add" -> {
                if (!(sender instanceof Player p)) return err(sender, "Только для игроков");
                if (!p.hasPermission(USE)) return err(sender, "Нет прав");
                if (args.length < 2) return err(sender, "/" + label + " add <название>");
                String name = rest(args, 1);
                switch (markers.add(p, name)) {
                    case OK -> ok(sender, "Метка «" + name.trim() + "» добавлена на карту");
                    case BAD_NAME -> err(sender, "Название: 1–32 символа — буквы, цифры, пробел, - и _");
                    case LIMIT -> err(sender, "Лимит меток: " + markers.maxPerPlayer()
                            + ". Удали старую: /" + label + " remove <название>");
                    case EXISTS -> err(sender, "У тебя уже есть метка с таким названием");
                    default -> { }
                }
            }
            case "remove" -> {
                if (!(sender instanceof Player p)) return err(sender, "Только для игроков");
                if (args.length < 2) return err(sender, "/" + label + " remove <название>");
                String name = rest(args, 1);
                if (markers.remove(p.getUniqueId(), name) == PlayerMarkers.Result.OK) {
                    ok(sender, "Метка «" + name.trim() + "» удалена");
                } else {
                    err(sender, "Нет метки с таким названием. Список: /" + label + " list");
                }
            }
            case "list" -> {
                if (!(sender instanceof Player p)) return err(sender, "Только для игроков");
                List<PlayerMarkers.Marker> own = markers.ownedBy(p.getUniqueId());
                if (own.isEmpty()) return ok(sender, "У тебя нет меток. Добавить: /" + label + " add <название>");
                ok(sender, "Твои метки (" + own.size() + "/" + markers.maxPerPlayer() + "):");
                for (PlayerMarkers.Marker m : own) {
                    sender.sendMessage(Component.text(" • " + m.name() + " — " + m.world() + " "
                            + (int) m.x() + " " + (int) m.y() + " " + (int) m.z(), NamedTextColor.GRAY));
                }
            }
            case "adminremove" -> {
                if (!sender.hasPermission(ADMIN)) return err(sender, "Нет прав");
                if (args.length < 3) return err(sender, "/" + label + " adminremove <игрок> <название>");
                Optional<UUID> owner = markers.ownerByName(args[1]);
                if (owner.isEmpty()) return err(sender, "У игрока " + args[1] + " нет меток");
                String name = rest(args, 2);
                if (markers.remove(owner.get(), name) == PlayerMarkers.Result.OK) {
                    ok(sender, "Метка «" + name.trim() + "» игрока " + args[1] + " удалена");
                } else {
                    err(sender, "Нет такой метки у " + args[1]);
                }
            }
            default -> help(sender, label);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            List<String> subs = new ArrayList<>(List.of("add", "remove", "list"));
            if (sender.hasPermission(ADMIN)) subs.add("adminremove");
            return filter(subs, args[0]);
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("remove") && sender instanceof Player p) {
            return filter(markers.ownedBy(p.getUniqueId()).stream().map(PlayerMarkers.Marker::name).toList(), args[1]);
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("adminremove") && sender.hasPermission(ADMIN)) {
            return filter(markers.ownerNames(), args[1]);
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("adminremove") && sender.hasPermission(ADMIN)) {
            return markers.ownerByName(args[1])
                    .map(u -> filter(markers.ownedBy(u).stream().map(PlayerMarkers.Marker::name).toList(), args[2]))
                    .orElse(List.of());
        }
        return List.of();
    }

    private boolean help(CommandSender s, String label) {
        ok(s, "Метки на карте:");
        s.sendMessage(Component.text(" /" + label + " add <название> — метка на твоём месте", NamedTextColor.GRAY));
        s.sendMessage(Component.text(" /" + label + " remove <название> — удалить", NamedTextColor.GRAY));
        s.sendMessage(Component.text(" /" + label + " list — твои метки", NamedTextColor.GRAY));
        return true;
    }

    private static String rest(String[] args, int from) {
        return String.join(" ", Arrays.copyOfRange(args, from, args.length));
    }

    private static List<String> filter(List<String> options, String prefix) {
        String p = prefix.toLowerCase(Locale.ROOT);
        return options.stream().filter(o -> o.toLowerCase(Locale.ROOT).startsWith(p)).toList();
    }

    private static boolean ok(CommandSender s, String msg) {
        s.sendMessage(Component.text(msg, NamedTextColor.GREEN));
        return true;
    }

    private static boolean err(CommandSender s, String msg) {
        s.sendMessage(Component.text(msg, NamedTextColor.RED));
        return true;
    }
}
