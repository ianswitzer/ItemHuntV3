package org.ianswitzer.itemhuntv3.tasks;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.ianswitzer.itemhuntv3.interfaces.GenericTask;

public class AdvancementTask implements GenericTask {
    private Advancement advancement;

    public AdvancementTask(String advancementName) {
        advancement = Bukkit.getAdvancement(NamespacedKey.minecraft(advancementName));
    }

    public Advancement getAdvancement() {
        return advancement;
    }

    @Override
    public String getTaskMessage() {
        if (advancement == null || advancement.getDisplay() == null) return "";
        return advancement.getDisplay().getTitle();
    }

    @Override
    public boolean hasCompleted(Player player) {
        return player.getAdvancementProgress(advancement).isDone();
    }
}
