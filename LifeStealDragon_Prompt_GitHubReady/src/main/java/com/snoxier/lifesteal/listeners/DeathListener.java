package com.snoxier.lifesteal.listeners;

import com.snoxier.lifesteal.HeartManager;
import com.snoxier.lifesteal.items.Items;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathListener implements Listener {
    private final HeartManager hearts;
    public DeathListener(HeartManager hearts){ this.hearts = hearts; }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e){
        Player p = e.getEntity();
        int current = hearts.getCurrentHearts(p);
        if(current <= 1){
            Bukkit.getBanList(BanList.Type.NAME).addBan(p.getName(), "Eliminated: ran out of hearts.", null, "LifeStealDragon");
            Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("LifeStealDragon"), () -> {
                p.kickPlayer("You are permanently banned: 0 hearts remaining.");
            }, 1L);
            return;
        }
        hearts.addHearts(p, -1);
        e.getEntity().getWorld().dropItemNaturally(p.getLocation(), Items.heart());
    }
}
