package com.snoxier.lifesteal;

import com.snoxier.lifesteal.commands.LSMaxCommand;
import com.snoxier.lifesteal.commands.UsePackCommand;
import com.snoxier.lifesteal.commands.WithdrawCommand;
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
    public static Names
