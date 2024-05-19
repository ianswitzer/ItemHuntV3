package org.ianswitzer.itemhuntv3.tasks;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.ianswitzer.itemhuntv3.interfaces.CompletionTracker;
import org.ianswitzer.itemhuntv3.interfaces.GenericTask;

import java.util.UUID;

public class ItemTask extends CompletionTracker {
    private final Material material;
    private final String materialName;

    public ItemTask(Material material) {
        super();
        this.material = material;
        this.materialName = "";
    }

    public ItemTask(String materialName) {
        super();
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
        UUID uuid = player.getUniqueId();
        if (completion.getOrDefault(uuid, false))
            return true;

        if (material == null) {
            for (ItemStack item : player.getInventory().getContents()) {
                if (item == null) continue;

                Material itemMaterial = item.getType();
                if (itemMaterial.toString().toUpperCase().contains(materialName.toUpperCase())) {
                    completion.put(uuid, true);
                    return true;
                }
            }
            return false;
        } else return player.getInventory().contains(material);
    }
}
