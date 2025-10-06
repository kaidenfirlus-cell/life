package com.snoxier.lifesteal.commands;

import com.snoxier.lifesteal.HeartManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LSMaxCommand implements CommandExecutor {
    private final HeartManager hearts;
    public LSMaxCommand(HeartManager hearts){ this.hearts = hearts; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.hasPermission("lifesteal.admin")){
            sender.sendMessage(ChatColor.RED + "No permission.");
            return true;
        }
        if(args.length < 1){
            sender.sendMessage(ChatColor.YELLOW+"Usage: /"+label+" <maxHearts> [player]");
            return true;
        }
        int max;
        try{ max = Integer.parseInt(args[0]); } catch (NumberFormatException e){
            sender.sendMessage(ChatColor.RED+"Not a number: "+args[0]);
            return true;
        }
        if(args.length >= 2){
            Player target = Bukkit.getPlayerExact(args[1]);
            if(target == null){
                sender.sendMessage(ChatColor.RED+"Player not found: "+args[1]);
                return true;
            }
            hearts.setPlayerMaxHearts(target, max);
            sender.sendMessage(ChatColor.GREEN+"Set max hearts for "+target.getName()+" to "+max);
            target.sendMessage(ChatColor.YELLOW+"Your heart cap was set to "+max+" by an admin.");
        } else {
            hearts.setGlobalMaxHearts(max);
            sender.sendMessage(ChatColor.GREEN+"Set GLOBAL max hearts to "+max);
        }
        return true;
    }
}
