package org.ianswitzer.itemhuntv3.tasks;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.ianswitzer.itemhuntv3.interfaces.GenericTask;

public class ItemTask implements GenericTask {
    private final Material material;

    public ItemTask(Material material) {
        this.material = material;
    }

    @Override
    public String getTaskMessage() {
        return material.toString().toUpperCase();
    }

    @Override
    public boolean hasCompleted(Player player) {
        return player.getInventory().contains(material);
    }
}
