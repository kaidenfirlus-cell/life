package com.snoxier.lifesteal.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class UsePackCommand implements CommandExecutor {
    private final Plugin plugin;
    public UsePackCommand(Plugin plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player p)) return true;
        String url = plugin.getConfig().getString("resourcePack.url", "");
        String arg = (args.length > 0 ? args[0] : url);
        if(arg == null || arg.isBlank()){
            p.sendMessage(Component.text("No resource pack URL configured."));
            return true;
        }
        try {
            p.setResourcePack(arg);
            p.sendMessage(Component.text("Loading resource pack..."));
        } catch (Throwable t){
            p.sendMessage(Component.text("Failed to send resource pack link."));
        }
        return true;
    }
}
