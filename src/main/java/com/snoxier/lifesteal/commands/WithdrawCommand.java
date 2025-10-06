package com.snoxier.lifesteal.commands;

import com.snoxier.lifesteal.HeartManager;
import com.snoxier.lifesteal.items.Items;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class WithdrawCommand implements CommandExecutor {
    private final HeartManager hearts;
    public WithdrawCommand(HeartManager hearts){ this.hearts = hearts; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player p)){
            sender.sendMessage("Players only.");
            return true;
        }
        if(args.length != 1){
            p.sendMessage(ChatColor.YELLOW+"Usage: /"+label+" <amount>");
            return true;
        }
        int amount;
        try{ amount = Integer.parseInt(args[0]); } catch (NumberFormatException e){
            p.sendMessage(ChatColor.RED+"Not a number: "+args[0]);
            return true;
        }
        if(amount <= 0){
            p.sendMessage(ChatColor.RED+"Amount must be > 0");
            return true;
        }
        int current = hearts.getCurrentHearts(p);
        int removable = Math.max(0, current - 1);
        if(amount > removable){
            p.sendMessage(ChatColor.RED+"You can only withdraw up to "+removable+" right now.");
            return true;
        }
        ItemStack heart = Items.heart();
        for(int i=0;i<amount;i++) p.getInventory().addItem(heart.clone());
        hearts.addHearts(p, -amount);
        p.sendMessage(ChatColor.GREEN+"Withdrew "+amount+" heart"+(amount==1?"":"s")+". You now have "+hearts.getCurrentHearts(p)+".");
        return true;
    }
}
