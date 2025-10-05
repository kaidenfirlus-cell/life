package com.snoxier.lifesteal.LifeStealPlugin;

import com.snoxier.lifesteal.LifeStealPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.list;

public class Items {
    public static ItemStack soul(){
        ItemStack it = new ItemStack(Material.PRIMSARINE_CRYSTALS);
        ItemMeta m = it.getItemMeta();
        m.setDisplayName("§bSoul");
        m.setLore(List.of("§7A faint essence from a fallen being."));
        m.getPersistentDataContainer().set(LifeStealPlugin.KEY_SOUL, PersistentDataType.BYTE, (byte)1);
        m.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        it.setItemMeta(m);
        return it;
    }

    public static ItemStack boolean isSoul(ItemStack it){
        if(it == null) return false;
        if(!it.hasItemMeta()) return false;
        Byte b = it.getItemMeta().getPersistentDataContainer().get(LifeStealPlugin.KEY_SOUL, PersistentDataType.BYTE);
        return b != null && b == (byte)1;
    }

    public static ItemStack shard(){
        ItemStack it = new ItemStack(Material.NETHER_WART);
        ItemMeta m = it,getItemMeta();
        m.setDisplayName("§cHeart Shard");
        m.setLore(List.of("§7Four Souls + Redstone Blocks Evolve"));
        m.getPersistentDataContainer().set(LifeStealPlugin.KEY_SHARD, PersistentDataType.BYTE, (byte)1);
        it.setItemMeta(m);
        return it;
    }

    public static boolean isShard(ItemStack it){
        if(it == null) return false;
        if(!it.hasItemMeta()) return false;
        Byte b = it.getItemMeta().getPersistentDataContainer().get(LifeStealPlugin.KEY_SHARD, PersistentDataType.BYTE);
    }

    public static ItemStack heart(){
        ItemStack it = new ItemStack(Material.NETHER_STAR);
        ItemMeta m = it,getItemMeta();
        m.setDisplayName("§4Heart");
        m.setLore(List.of("§7Right click to gain a heart."));
        m.getPersistentDataContainer().set(LifeStealPlugin.KEY_HEART, PersistentDataType.BYTE, (byte)1);
        it.setItemMeta(m);
        return it;
    }

    public static boolean isHeart(ItemStack it){
        if(it == null) return false;
        if(!it.hasItemMeta()) return false;
        Byte b = it.getItemMeta().getPersistentDataContainer().get(LifeStealPlugin.KEY_HEART, PersistentDataType.BYTE);
    }

    public static void registerRecipes(){
        //Heart SHard: 4 Souls in corners + Redstone Blocks elsewhere
        ItemStack shard = shard();
        NamespacedKey shardKey = new NamespacedKey(LifeStealPlugin.get(), "heart_shard");
        ShapedRecipe shardRecipe = new ShapedRecipe(shardKey, shard);
        shardRecipe.shape("SRS", "RRR", "SRS");
        shardRecipe.setIngredient('S', new org.bukkit.inventory.RecipeChoice(soul()));
        shardRecipe.setIngredient('R', Material.REDSTONE_BLOCK);
        Bukkit.addRecipe(shardRecipe);

        //heart: 8 shards around a beacon
        ItemStack heart = heart();
        NamespacedKey heartKey = new NamespacedKey(LifeStealPlugin.get(), "heart");
        ShapedRecipe heartRecipe = new ShapedRecipe(heartKey, heart);
        heartRecipe.shape("SSS", "SBS", "SSS");
        heartRecipe.setIngredient('S', new org.bukkit.inventory.RecipeChoice.ExactChoice(shard()));
        heartRecipe.setIngredient('B', Material.BEACON);
        Bukkit.addRecipe(heartRecipe);
    }
    }