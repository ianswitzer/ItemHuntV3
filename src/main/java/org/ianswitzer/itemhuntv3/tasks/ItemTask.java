package org.ianswitzer.itemhuntv3.tasks;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.ianswitzer.itemhuntv3.interfaces.GenericTask;

public class ItemTask implements GenericTask {
    private final Material material;
    private final String materialName;

    public ItemTask(Material material) {
        this.material = material;
        this.materialName = "";
    }

    public ItemTask(String materialName) {
        this.material = null;
        this.materialName = materialName;
    }

    @Override
    public String getTaskMessage() {
        return material == null ? materialName : material.toString().toUpperCase();
    }

    @Override
    public String getTaskMessage(Player player) {
        return getTaskMessage();
    }

    @Override
    public boolean hasCompleted(Player player) {
        if (material == null) {
            for (ItemStack item : player.getInventory().getContents()) {
                if (item == null) continue;

                Material itemMaterial = item.getType();
                if (itemMaterial.toString().toUpperCase().contains(materialName.toUpperCase()))
                    return true;
            }
            return false;
        } else return player.getInventory().contains(material);
    }
}
