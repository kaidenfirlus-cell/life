package com.snoxier.lifesteal;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;

public class HeartManager {
    private final LifeStealPlugin plugin;

    public HeartManager(LifeStealPlugin plugin){ this.plugin = plugin; }

    public int getDefaultMaxHearts(){
        return plugin.cfg().getInt("defaults.maxHearts", 20);
    }

    public int getGlobalMaxHearts(){
        return plugin.cfg().getInt("global.maxHearts", getDefaultMaxHearts());
    }

    public void setGlobalMaxHearts(int max){
        plugin.cfg().set("global.maxHearts", Math.max(1, max));
        plugin.saveConfig();
    }

    public int getPlayerMaxHearts(Player p){
        return plugin.cfg().getInt("players."+p.getUniqueId()+".maxHearts", getGlobalMaxHearts());
    }

    public void setPlayerMaxHearts(Player p, int max){
        plugin.cfg().set("players."+p.getUniqueId()+".maxHearts", Math.max(1, max));
        plugin.saveConfig();
        clampToCap(p);
    }

    public void applyStoredHearts(Player p){
        clampToCap(p);
    }

    public int getCurrentHearts(Player p){
        AttributeInstance inst = p.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if(inst == null) return 10;
        return (int)Math.round(inst.getBaseValue() / 2.0);
    }

    public void setCurrentHearts(Player p, int hearts){
        int cap = getPlayerMaxHearts(p);
        hearts = Math.max(1, Math.min(hearts, cap));
        AttributeInstance inst = p.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if(inst != null){
            inst.setBaseValue(hearts * 2.0);
            if(p.getHealth() > inst.getBaseValue()) p.setHealth(inst.getBaseValue());
        }
    }

    public void addHearts(Player p, int delta){
        setCurrentHearts(p, getCurrentHearts(p) + delta);
    }

    public void clampToCap(Player p){
        setCurrentHearts(p, Math.min(getCurrentHearts(p), getPlayerMaxHearts(p)));
    }
}
