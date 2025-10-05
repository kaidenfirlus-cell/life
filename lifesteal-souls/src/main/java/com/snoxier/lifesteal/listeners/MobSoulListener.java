package com.snoxier.lifesteal.listeners;

import com.snoxier.lifesteal.items.Items; 
import org.bukkit.Location; 
import org.bukkit.entity.*; 
import org.bukkit.event.EventHandler; 
import org.bukkit.event.Listener; 
import org.bukkit.event.entity.EntityDeathEvent; 
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

public class MobSoulListener implements Listener { 
    private boolean isBoss(Entity e){
         EntityType t = e.getType();
         return t == EntityType.ENDER_DRAGON || t == EntityType.WITHER || t == EntityType.WARDEN; 
    }

private boolean isHostile(Entity e){
    return e instanceof Monster;
}

private boolean isPassive(Entity e){
    // Not hostile and not boss and is living
    return e instanceof Animals || e instanceof WaterMob || e instanceof Ambient || e instanceof Golem;
}

@EventHandler
public void onDeath(EntityDeathEvent e){
    LivingEntity dead = e.getEntity();
    Player killer = dead.getKiller();
    if(killer == null) return;

    double chance;
    if(isBoss(dead)) chance = 0.15; // 15%
    else if(isHostile(dead)) chance = 0.02; // 2%
    else if(isPassive(dead)) chance = 0.005; // 0.5%
    else return;

    if(ThreadLocalRandom.current().nextDouble() < chance){
        ItemStack soul = Items.soul();
        Location loc = dead.getLocation();
        dead.getWorld().dropItemNaturally(loc, soul);
    }
}
}
