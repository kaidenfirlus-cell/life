package com.snoxier.lifesteal.listeners;

import com.snoxier.lifesteal.HeartManager; 
import com.snoxier.lifesteal.items.Items; 
import org.bukkit.Sound; 
import org.bukkit.entity.Player; 
import org.bukkit.event.EventHandler; 
import org.bukkit.event.Listener; 
import org.bukkit.event.player.PlayerInteractEvent; 
import org.bukkit.inventory.EquipmentSlot; 
import org.bukkit.inventory.ItemStack;

public class InteractListener implements Listener {
    private final HeartManager hearts; 
    public InteractListener(HeartManager hearts){ this.hearts = hearts; }

@EventHandler
public void onUse(PlayerInteractEvent e){
    if(e.getHand() != EquipmentSlot.HAND) return;
    ItemStack it = e.getItem();
    if(Items.isHeart(it)){
        e.setCancelled(true);
        Player p = e.getPlayer();
        int before = hearts.getCurrentHearts(p);
        hearts.addHearts(p, 1);
        int after = hearts.getCurrentHearts(p);
        if(after > before){
            it.setAmount(it.getAmount() - 1);
            p.playSound(p, Sound.ITEM_TOTEM_USE, 1f, 1.2f);
            p.sendMessage("§a+1 heart! Now at §c"+after+"§a/§c"+hearts.getPlayerMaxHearts(p));
        } else {
            p.sendMessage("§eYou're already at your max hearts ("+after+").");
        }
    }
}

}
