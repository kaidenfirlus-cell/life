package com.snoxier.lifesteal.listeners;

import com.snoxier.lifesteal.items.Items;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class MobSoulListener implements Listener {
    private boolean isBoss(Entity e){
        EntityType t = e.getType();
        return t == EntityType.WITHER || t == EntityType.WARDEN; // Ender Dragon removed
    }
    private boolean isHostile(Entity e){ return e instanceof Monster; }
    private boolean isPassive(Entity e){ return e instanceof Animals || e instanceof WaterMob || e instanceof Ambient || e instanceof Golem; }

    @EventHandler
    public void onDeath(EntityDeathEvent e){
        LivingEntity dead = e.getEntity();
        Player killer = dead.getKiller();
        if(killer == null) return;

        if(dead.getType() == EntityType.ENDER_DRAGON){
            if(java.util.concurrent.ThreadLocalRandom.current().nextDouble() < 0.02){
                e.getEntity().getWorld().dropItemNaturally(dead.getLocation(), Items.dragonSoul());
            }
            return;
        }

        double chance;
        if(isBoss(dead)) chance = 0.15;
        else if(isHostile(dead)) chance = 0.02;
        else if(isPassive(dead)) chance = 0.005;
        else return;

        if(java.util.concurrent.ThreadLocalRandom.current().nextDouble() < chance){
            e.getEntity().getWorld().dropItemNaturally(dead.getLocation(), Items.soul());
        }
    }
}
