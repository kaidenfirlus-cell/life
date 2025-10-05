package com.snoxier.lifesteal;

import com.snoxier.lifesteal.commands.LSMaxCommand;
import com.snoxier.lifesteal.commands.WithdrawCommand;
import com.snoxier.lifesteal.items.Items;
import com.snoxier.lifesteal.listeners.DeathListener;
import com.snoxier.lifesteal.listeners.InteractListener;
import com.snoxier.lifesteal.listeners.MobSoulListener;
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

    public static LifeStealPlugin get(){ return instance; }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        KEY_SOUL = new NamespacedKey(this, "soul");
        KEY_SHARD = new NamespacedKey(this, "heart_shard");
        KEY_HEART = new NamespacedKey(this, "heart");

        heartManager = new HeartManager(this);
        Items.registerRecipes();

        //listeners
        Bukkit.getPluginManager().registerEvents(new MobSoulListener(), this);
        Bukkit.getPluginManager().registerEvents(new DeathListener(), this);
        Bukkit.getPluginManager().registerEvents(new InteractListener(), this);

        //commands
        getCommand("lsmax").setExecutor(new LSMaxCommand(heartManager));
        getCommand("withdraw").setExecutor(new WithdrawCommand(heartManager));

        //ensure online players are normalized bc important.
        Bukkit.getOnlinePlayers().forEach(p -> heartManager.applyStoredHearts(p));

        getLogger().info("LifeStealSouls enabled. Made by Snoxier, the truley best coder.");
    }

    @Override
    public void onDisable() {
        saveConfig();
    }

    public FileConfiguration cfg(){ return getConfig(); }
}