package com.snoxier.lifesteal.items;

import com.snoxier.lifesteal.LifeStealPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class Items {
    public static ItemStack soul(){
        ItemStack it = new ItemStack(Material.PRISMARINE_CRYSTALS);
        ItemMeta m = it.getItemMeta();
        m.setDisplayName("§bSpirit");
        m.setLore(List.of("§7A faint essence from a fallen being."));
        m.getPersistentDataContainer().set(LifeStealPlugin.KEY_SOUL, PersistentDataType.BYTE, (byte)1);
        m.setCustomModelData(LifeStealPlugin.CMD_SOUL);
        m.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        it.setItemMeta(m);
        return it;
    }

    public static boolean isSoul(ItemStack it){
        if(it == null || !it.hasItemMeta()) return false;
        Byte b = it.getItemMeta().getPersistentDataContainer().get(LifeStealPlugin.KEY_SOUL, PersistentDataType.BYTE);
        Integer cmd = it.getItemMeta().hasCustomModelData() ? it.getItemMeta().getCustomModelData() : null;
        return b != null && b == (byte)1 && cmd != null && cmd == LifeStealPlugin.CMD_SOUL;
    }

    public static ItemStack shard(){
        ItemStack it = new ItemStack(Material.NETHER_WART);
        ItemMeta m = it.getItemMeta();
        m.setDisplayName("§cHeart Shard");
        m.setLore(List.of("§7Four Spirits + Redstone Blocks"));
        m.getPersistentDataContainer().set(LifeStealPlugin.KEY_SHARD, PersistentDataType.BYTE, (byte)1);
        m.setCustomModelData(LifeStealPlugin.CMD_SHARD);
        it.setItemMeta(m);
        return it;
    }

    public static boolean isShard(ItemStack it){
        if(it == null || !it.hasItemMeta()) return false;
        Byte b = it.getItemMeta().getPersistentDataContainer().get(LifeStealPlugin.KEY_SHARD, PersistentDataType.BYTE);
        Integer cmd = it.getItemMeta().hasCustomModelData() ? it.getItemMeta().getCustomModelData() : null;
        return b != null && b == (byte)1 && cmd != null && cmd == LifeStealPlugin.CMD_SHARD;
    }

    public static ItemStack heart(){
        ItemStack it = new ItemStack(Material.NETHER_STAR);
        ItemMeta m = it.getItemMeta();
        m.setDisplayName("§4❤ Heart");
        m.setLore(List.of("§7Right-click to gain +1 heart (up to cap)."));
        m.getPersistentDataContainer().set(LifeStealPlugin.KEY_HEART, PersistentDataType.BYTE, (byte)1);
        m.setCustomModelData(LifeStealPlugin.CMD_HEART);
        it.setItemMeta(m);
        return it;
    }

    public static boolean isHeart(ItemStack it){
        if(it == null || !it.hasItemMeta()) return false;
        Byte b = it.getItemMeta().getPersistentDataContainer().get(LifeStealPlugin.KEY_HEART, PersistentDataType.BYTE);
        Integer cmd = it.getItemMeta().hasCustomModelData() ? it.getItemMeta().getCustomModelData() : null;
        return b != null && b == (byte)1 && cmd != null && cmd == LifeStealPlugin.CMD_HEART;
    }

    public static ItemStack dragonSoul(){
        ItemStack it = new ItemStack(Material.ECHO_SHARD);
        ItemMeta m = it.getItemMeta();
        m.setDisplayName("§bDragon Soul");
        m.setLore(List.of("§7A rare essence from the Ender Dragon."));
        m.getPersistentDataContainer().set(LifeStealPlugin.KEY_DRAGON_SOUL, PersistentDataType.BYTE, (byte)1);
        m.setCustomModelData(LifeStealPlugin.CMD_DRAGON_SOUL);
        m.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        it.setItemMeta(m);
        return it;
    }

    public static boolean isDragonSoul(ItemStack it){
        if(it == null || !it.hasItemMeta()) return false;
        Byte b = it.getItemMeta().getPersistentDataContainer().get(LifeStealPlugin.KEY_DRAGON_SOUL, PersistentDataType.BYTE);
        Integer cmd = it.getItemMeta().hasCustomModelData() ? it.getItemMeta().getCustomModelData() : null;
        return b != null && b == (byte)1 && cmd != null && cmd == LifeStealPlugin.CMD_DRAGON_SOUL;
    }

    public static void registerRecipes(){
        ItemStack shard = shard();
        NamespacedKey shardKey = new NamespacedKey(LifeStealPlugin.get(), "heart_shard");
        ShapedRecipe shardRecipe = new ShapedRecipe(shardKey, shard);
        shardRecipe.shape("SRS", "RRR", "SRS");
        shardRecipe.setIngredient('S', new org.bukkit.inventory.RecipeChoice.ExactChoice(soul()));
        shardRecipe.setIngredient('R', Material.REDSTONE_BLOCK);
        Bukkit.addRecipe(shardRecipe);

        ItemStack heart = heart();
        NamespacedKey heartKey = new NamespacedKey(LifeStealPlugin.get(), "heart");
        ShapedRecipe heartRecipe = new ShapedRecipe(heartKey, heart);
        heartRecipe.shape("SSS", "SBS", "SSS");
        heartRecipe.setIngredient('S', new org.bukkit.inventory.RecipeChoice.ExactChoice(shard()));
        heartRecipe.setIngredient('B', Material.BEACON);
        Bukkit.addRecipe(heartRecipe);
    }
}
