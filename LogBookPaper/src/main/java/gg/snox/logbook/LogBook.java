
package gg.snox.logbook;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class LogBook extends JavaPlugin implements Listener, TabExecutor {

    private File logsFile;
    private FileConfiguration logs;
    private File viewersFile;
    private FileConfiguration viewers;
    private final Map<UUID, UUID> viewingTarget = new HashMap<>();
    private final Map<UUID, String> viewingCategory = new HashMap<>();

    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("log").setExecutor(this);
        getCommand("log").setTabCompleter(this);
        initFiles();
        getLogger().info("LogBook enabled.");
    }

    private void initFiles() {
        logsFile = new File(getDataFolder(), "logs.yml");
        viewersFile = new File(getDataFolder(), "viewers.yml");
        if (!getDataFolder().exists()) getDataFolder().mkdirs();
        try {
            if (!logsFile.exists()) logsFile.createNewFile();
            if (!viewersFile.exists()) viewersFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        logs = YamlConfiguration.loadConfiguration(logsFile);
        viewers = YamlConfiguration.loadConfiguration(viewersFile);
        if (!viewers.isSet("viewers")) viewers.set("viewers", new ArrayList<String>());
        saveViewers();
    }

    private void saveLogs() {
        try { logs.save(logsFile); } catch (IOException e) { e.printStackTrace(); }
    }
    private void saveViewers() {
        try { viewers.save(viewersFile); } catch (IOException e) { e.printStackTrace(); }
    }

    // ---- Command handling ----
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.YELLOW + "Usage: /" + label + " <add|view|ban|mute|kick>");
            return true;
        }
        String sub = args[0].toLowerCase(Locale.ROOT);
        switch (sub) {
            case "add": return cmdAdd(sender, args);
            case "view": return cmdView(sender, args);
            case "ban": return cmdBan(sender, args);
            case "mute": return cmdMute(sender, args);
            case "kick": return cmdKick(sender, args);
            default:
                sender.sendMessage(ChatColor.RED + "Unknown subcommand.");
                return true;
        }
    }

    private boolean cmdAdd(CommandSender sender, String[] args) {
        if (!sender.hasPermission("logbook.admin")) {
            sender.sendMessage(ChatColor.RED + "No permission.");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(ChatColor.YELLOW + "Usage: /log add <player>");
            return true;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        if (target == null || (target.getName()==null && !target.hasPlayedBefore())) {
            sender.sendMessage(ChatColor.RED + "Unknown player.");
            return true;
        }
        List<String> list = viewers.getStringList("viewers");
        if (!list.contains(target.getUniqueId().toString())) {
            list.add(target.getUniqueId().toString());
            viewers.set("viewers", list);
            saveViewers();
        }
        sender.sendMessage(ChatColor.GREEN + "Added " + target.getName() + " to viewers.");
        return true;
    }

    private boolean cmdView(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Players only.");
            return true;
        }
        Player p = (Player) sender;
        if (args.length < 2) {
            p.sendMessage(ChatColor.YELLOW + "Usage: /log view <player>");
            return true;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        if (target == null || (target.getName()==null && !target.hasPlayedBefore())) {
            p.sendMessage(ChatColor.RED + "Unknown player.");
            return true;
        }
        if (!canViewAll(p)) {
            p.sendMessage(ChatColor.RED + "You are not allowed to view logs.");
            return true;
        }
        viewingTarget.put(p.getUniqueId(), target.getUniqueId());
        openRootGUI(p);
        return true;
    }

    private boolean cmdBan(CommandSender sender, String[] args) {
        if (!sender.hasPermission("logbook.punish")) {
            sender.sendMessage(ChatColor.RED + "No permission.");
            return true;
        }
        if (args.length < 4) {
            sender.sendMessage(ChatColor.YELLOW + "Usage: /log ban <player> <time|perm> <reason...>");
            return true;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        long duration = parseDuration(args[2]);
        String reason = String.join(" ", Arrays.copyOfRange(args, 3, args.length));
        String actor = sender.getName();

        Date expires = (duration <= 0) ? null : new Date(System.currentTimeMillis() + duration);
        Bukkit.getBanList(BanList.Type.NAME).addBan(target.getName(), reason, expires, actor);
        if (target.isOnline()) {
            Player tp = target.getPlayer();
            if (tp != null) {
                tp.kickPlayer(ChatColor.RED + "Banned: " + reason + (expires==null ? "" : "\nUntil: " + TS.format(expires.toInstant())));
            }
        }
        addLog(target.getUniqueId(), "BAN", actor, reason, duration);
        sender.sendMessage(ChatColor.GREEN + "Banned " + target.getName() + (duration<=0 ? " permanently." : " for " + formatDuration(duration) + "."));
        return true;
    }

    private boolean cmdMute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("logbook.punish")) {
            sender.sendMessage(ChatColor.RED + "No permission.");
            return true;
        }
        if (args.length < 4) {
            sender.sendMessage(ChatColor.YELLOW + "Usage: /log mute <player> <time|perm> <reason...>");
            return true;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        long duration = parseDuration(args[2]);
        String reason = String.join(" ", Arrays.copyOfRange(args, 3, args.length));
        String actor = sender.getName();

        // Save active mute
        String key = target.getUniqueId().toString() + ".activeMute";
        long until = (duration <= 0) ? Long.MAX_VALUE : System.currentTimeMillis() + duration;
        logs.set(key + ".until", until);
        logs.set(key + ".reason", reason);
        logs.set(key + ".actor", actor);
        saveLogs();

        if (target.isOnline()) {
            Player tp = target.getPlayer();
            if (tp != null) {
                tp.sendMessage(ChatColor.RED + "You have been muted for " + (duration<=0 ? "permanent" : formatDuration(duration)) + ". Reason: " + reason);
            }
        }
        addLog(target.getUniqueId(), "MUTE", actor, reason, duration);
        sender.sendMessage(ChatColor.GREEN + "Muted " + target.getName() + (duration<=0 ? " permanently." : " for " + formatDuration(duration) + "."));
        return true;
    }

    private boolean cmdKick(CommandSender sender, String[] args) {
        if (!sender.hasPermission("logbook.punish")) {
            sender.sendMessage(ChatColor.RED + "No permission.");
            return true;
        }
        if (args.length < 3) {
            sender.sendMessage(ChatColor.YELLOW + "Usage: /log kick <player> <reason...>");
            return true;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        String reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
        String actor = sender.getName();

        if (target.isOnline()) {
            Player tp = target.getPlayer();
            if (tp != null) tp.kickPlayer(ChatColor.RED + "Kicked: " + reason);
        }
        addLog(target.getUniqueId(), "KICK", actor, reason, 0);
        sender.sendMessage(ChatColor.GREEN + "Kicked " + target.getName() + " for: " + reason);
        return true;
    }

    private boolean canViewAll(Player p) {
        if (p.hasPermission("logbook.admin")) return true;
        List<String> list = viewers.getStringList("viewers");
        return p.hasPermission("logbook.view") && list.contains(p.getUniqueId().toString());
    }

    // ---- Logging ----
    private void addLog(UUID target, String type, String actor, String reason, long durationMs) {
        String base = target.toString() + ".logs";
        List<Map<String, Object>> list = (List<Map<String, Object>>) logs.getList(base, new ArrayList<>());
        Map<String, Object> entry = new LinkedHashMap<>();
        entry.put("type", type);
        entry.put("timestamp", System.currentTimeMillis());
        entry.put("actor", actor);
        entry.put("reason", reason);
        entry.put("duration", durationMs);
        list.add(entry);
        logs.set(base, list);
        saveLogs();
    }

    // ---- GUI ----
    private void openRootGUI(Player viewer) {
        Inventory inv = Bukkit.createInventory(null, 9, ChatColor.DARK_AQUA + "LogBook: Categories");
        inv.setItem(2, named(Material.RED_BED, ChatColor.RED + "Bans"));
        inv.setItem(4, named(Material.PINK_BED, ChatColor.LIGHT_PURPLE + "Mutes"));
        inv.setItem(6, named(Material.GREEN_BED, ChatColor.GREEN + "Kicks"));
        viewer.openInventory(inv);
    }

    private ItemStack named(Material mat, String name, String... lore) {
        ItemStack is = new ItemStack(mat);
        ItemMeta im = is.getItemMeta();
        if (im != null) {
            im.setDisplayName(name);
            if (lore != null && lore.length > 0) {
                im.setLore(Arrays.stream(lore).map(s -> ChatColor.GRAY + s).collect(Collectors.toList()));
            }
            is.setItemMeta(im);
        }
        return is;
    }

    private void openListGUI(Player viewer, String type) {
        UUID target = viewingTarget.get(viewer.getUniqueId());
        if (target == null) return;

        List<Map<String, Object>> list = (List<Map<String, Object>>) logs.getList(target.toString() + ".logs", new ArrayList<>());
        List<Map<String, Object>> filtered = list.stream()
                .filter(m -> type.equalsIgnoreCase((String)m.get("type")))
                .sorted(Comparator.comparingLong(m -> (long)m.getOrDefault("timestamp", 0L)).reversed())
                .collect(Collectors.toList());

        int size = Math.min(54, ((filtered.size() + 8) / 9) * 9);
        if (size == 0) size = 9;
        Inventory inv = Bukkit.createInventory(null, size, ChatColor.DARK_AQUA + "LogBook: " + type);
        int i = 0;
        for (Map<String, Object> m : filtered) {
            if (i >= size) break;
            long ts = (long) m.getOrDefault("timestamp", System.currentTimeMillis());
            String when = TS.format(Instant.ofEpochMilli(ts));
            String reason = (String) m.getOrDefault("reason", "N/A");
            String actor = (String) m.getOrDefault("actor", "Console");
            long dur = (long) m.getOrDefault("duration", 0L);
            List<String> lore = new ArrayList<>();
            lore.add("When: " + when);
            lore.add("By: " + actor);
            if (dur > 0) lore.add("Length: " + formatDuration(dur));
            if (type.equals("BAN")) {
                inv.setItem(i++, named(Material.RED_BED, ChatColor.RED + "Ban • " + reason, lore.toArray(new String[0])));
            } else if (type.equals("MUTE")) {
                inv.setItem(i++, named(Material.PINK_BED, ChatColor.LIGHT_PURPLE + "Mute • " + reason, lore.toArray(new String[0])));
            } else {
                inv.setItem(i++, named(Material.GREEN_BED, ChatColor.GREEN + "Kick • " + reason, lore.toArray(new String[0])));
            }
        }
        viewingCategory.put(viewer.getUniqueId(), type);
        viewer.openInventory(inv);
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        HumanEntity he = e.getWhoClicked();
        if (!(he instanceof Player)) return;
        Player p = (Player) he;
        String title = e.getView().getTitle();
        if (!title.startsWith(ChatColor.DARK_AQUA + "LogBook")) return;
        e.setCancelled(true);
        ItemStack item = e.getCurrentItem();
        if (item == null || item.getType() == Material.AIR) return;

        if (title.contains("Categories")) {
            if (item.getType() == Material.RED_BED) {
                openListGUI(p, "BAN");
            } else if (item.getType() == Material.PINK_BED) {
                openListGUI(p, "MUTE");
            } else if (item.getType() == Material.GREEN_BED) {
                openListGUI(p, "KICK");
            }
        }
    }

    // ---- Mute enforcement ----
    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        UUID id = e.getPlayer().getUniqueId();
        String key = id.toString() + ".activeMute";
        if (!logs.isSet(key + ".until")) return;
        long until = logs.getLong(key + ".until", 0L);
        if (until == Long.MAX_VALUE || System.currentTimeMillis() < until) {
            e.setCancelled(true);
            String reason = logs.getString(key + ".reason", "Muted");
            long remain = (until == Long.MAX_VALUE) ? -1 : (until - System.currentTimeMillis());
            String more = (remain < 0) ? " (permanent)" : (" (" + formatDuration(remain) + " remaining)");
            e.getPlayer().sendMessage(ChatColor.RED + "You are muted: " + reason + more);
        } else {
            // Expired; clear mute
            logs.set(key, null);
            saveLogs();
        }
    }

    // ---- Time parsing/formatting ----
    private long parseDuration(String s) {
        if (s.equalsIgnoreCase("perm") || s.equalsIgnoreCase("permanent")) return -1L;
        long total = 0L;
        String num = "";
        for (char c : s.toCharArray()) {
            if (Character.isDigit(c)) {
                num += c;
            } else {
                if (num.isEmpty()) return 0L;
                long n = Long.parseLong(num);
                switch (Character.toLowerCase(c)) {
                    case 's': total += n * 1000L; break;
                    case 'm': total += n * 60_000L; break;
                    case 'h': total += n * 3_600_000L; break;
                    case 'd': total += n * 86_400_000L; break;
                    case 'w': total += n * 7 * 86_400_000L; break;
                    default: return 0L;
                }
                num = "";
            }
        }
        if (!num.isEmpty()) {
            // default to seconds if bare number
            total += Long.parseLong(num) * 1000L;
        }
        return total <= 0 ? 0L : total;
    }

    private String formatDuration(long ms) {
        if (ms <= 0) return "0s";
        long s = ms / 1000L;
        long w = s / (7*24*3600); s %= 7*24*3600;
        long d = s / (24*3600); s %= 24*3600;
        long h = s / 3600; s %= 3600;
        long m = s / 60; s %= 60;
        List<String> parts = new ArrayList<>();
        if (w>0) parts.add(w+"w");
        if (d>0) parts.add(d+"d");
        if (h>0) parts.add(h+"h");
        if (m>0) parts.add(m+"m");
        if (s>0) parts.add(s+"s");
        return String.join(" ", parts);
    }

    // ---- Tab complete ----
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) return Arrays.asList("add","view","ban","mute","kick").stream()
                .filter(s->s.startsWith(args[0].toLowerCase(Locale.ROOT))).collect(Collectors.toList());
        if (args.length == 2) {
            if (Arrays.asList("add","view","ban","mute","kick").contains(args[0].toLowerCase(Locale.ROOT))) {
                return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
            }
        }
        if (args.length == 3 && Arrays.asList("ban","mute").contains(args[0].toLowerCase(Locale.ROOT))) {
            return Arrays.asList("10m","1h","1d","7d","perm");
        }
        return Collections.emptyList();
    }
}
