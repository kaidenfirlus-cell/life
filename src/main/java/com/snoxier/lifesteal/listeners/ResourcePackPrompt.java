package com.snoxier.lifesteal.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

public class ResourcePackPrompt implements Listener {

    private final Plugin plugin;
    private final String url;
    private final String yesLabel;
    private final String noLabel;
    private final Component prompt;

    public ResourcePackPrompt(Plugin plugin) {
        this.plugin = plugin;
        var cfg = plugin.getConfig().getConfigurationSection("resourcePack");
        this.url = cfg.getString("url", "");
        this.prompt = Component.text(cfg.getString("prompt", "Would you like to use our texture pack?"));
        this.yesLabel = cfg.getString("buttons.yes", "Use Pack");
        this.noLabel = cfg.getString("buttons.no", "No Thanks");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (url == null || url.isBlank()) return;

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Component msg = prompt.append(Component.text("  "))
                .append(Component.text("[ " + yesLabel + " ]", NamedTextColor.GREEN)
                    .clickEvent(ClickEvent.runCommand("/usepack " + url)))
                .append(Component.text("   "))
                .append(Component.text("[ " + noLabel + " ]", NamedTextColor.RED));
            p.sendMessage(msg);
        }, 40L);
    }
}
