package com.snoxier.lifesteal;

import com.snoxier.lifesteal.commands.LSMaxCommand;
import com.snoxier.lifesteal.commands.WithdrawCommand;
import com.snoxier.lifesteal.commands.UsePackCommand;
import com.snoxier.lifesteal.items.Items;
import com.snoxier.lifesteal.listeners.DeathListener;
import com.snoxier.lifesteal.listeners.InteractListener;
import com.snoxier.lifesteal.listeners.MobSoulListener;
import com.snoxier.lifesteal.listeners.ResourcePackPrompt;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class LifeStealPlugin extends JavaPlugin {
    private static LifeStealPlugin instance;
    private HeartManager heartManager;

    public static NamespacedKey KEY_SOUL;
    public static NamespacedKey KEY_SHARD;
    public static NamespacedKey KEY_HEART;
    public static NamespacedKey KEY_DRAGON_SOUL;

    public static final int CMD_SOUL = 1010;
    public static final int CMD_SHARD = 1011;
    public static final int CMD_HEART = 1012;
    public static final int CMD_DRAGON_SOUL = 1020;

    public static LifeStealPlugin get(){ return instance; }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        KEY_SOUL = new NamespacedKey(this, "soul");
        KEY_SHARD = new NamespacedKey(this, "heart_shard");
        KEY_HEART = new NamespacedKey(this, "heart");
        KEY_DRAGON_SOUL = new NamespacedKey(this, "dragon_soul");

        heartManager = new HeartManager(this);
        Items.registerRecipes();

        Bukkit.getPluginManager().registerEvents(new MobSoulListener(), this);
        Bukkit.getPluginManager().registerEvents(new DeathListener(heartManager), this);
        Bukkit.getPluginManager().registerEvents(new InteractListener(heartManager), this);

        if(getConfig().getBoolean("resourcePack.offerOnJoin", true)){
            Bukkit.getPluginManager().registerEvents(new ResourcePackPrompt(this), this);
        }

        getCommand("lsmax").setExecutor(new LSMaxCommand(heartManager));
        getCommand("withdraw").setExecutor(new WithdrawCommand(heartManager));
        getCommand("usepack").setExecutor(new UsePackCommand(this));

        Bukkit.getOnlinePlayers().forEach(p -> heartManager.applyStoredHearts(p));
    }

    @Override
    public void onDisable() {
        saveConfig();
    }

    public FileConfiguration cfg(){ return getConfig(); }
}
