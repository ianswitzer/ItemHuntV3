package org.ianswitzer.itemhuntv3.tasks;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.ianswitzer.itemhuntv3.interfaces.GenericTask;

import java.util.Objects;

public class EnchantmentTask implements GenericTask {
    private Enchantment enchantment;
    private Material material;
    private int level;

    public EnchantmentTask(Enchantment enchantment) {
        this.enchantment = enchantment;
        this.level = 0;
    }

    public EnchantmentTask(Enchantment enchantment, Material material) {
        this.enchantment = enchantment;
        this.material = material;
        this.level = 0;
    }

    public EnchantmentTask(Enchantment enchantment, Material material, int level) {
        this.enchantment = enchantment;
        this.material = material;
        this.level = level;
    }

    public EnchantmentTask(Enchantment enchantment, int level) {
        this.enchantment = enchantment;
        this.level = level;
    }

    @Override
    public String getTaskMessage() {
        String fullName = enchantment.toString().toUpperCase();
        String clipped = fullName.substring(fullName.indexOf(":") + 1, fullName.length() - 1);

        if (material == null)
            return clipped;
        else
            return clipped + " " + material.name();
    }

    @Override
    public String getTaskMessage(Player player) {
        return getTaskMessage();
    }

    @Override
    public boolean hasCompleted(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) continue;

            if (material == null) {
                if (item.getType().equals(Material.ENCHANTED_BOOK)) {
                    EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
                    if (meta != null && meta.hasStoredEnchant(enchantment) && meta.getStoredEnchantLevel(enchantment) >= level)
                        return true;

                } else if (item.containsEnchantment(enchantment) && item.getEnchantmentLevel(enchantment) >= level)
                    return true;

            } else if (item.getType().equals(material)) {
                if (item.getType().equals(Material.ENCHANTED_BOOK)) {
                    EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
                    if (meta != null && meta.hasStoredEnchant(enchantment) && meta.getStoredEnchantLevel(enchantment) >= level)
                        return true;
                } else if (item.containsEnchantment(enchantment) && item.getEnchantmentLevel(enchantment) >= level)
                    return true;
            }
        }
        return false;
    }
}
