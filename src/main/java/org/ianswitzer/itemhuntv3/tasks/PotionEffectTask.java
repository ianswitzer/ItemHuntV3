package org.ianswitzer.itemhuntv3.tasks;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.ianswitzer.itemhuntv3.interfaces.GenericTask;

public class PotionEffectTask implements GenericTask {
    private final PotionEffectType effect;

    public PotionEffectTask(PotionEffectType effect) {
        this.effect = effect;
    }

    @Override
    public String getTaskMessage() {
        String fullName = effect.toString().toUpperCase();
        return "EFFECT: " + fullName.substring(fullName.indexOf(":") + 1, fullName.length() - 1);
    }

    @Override
    public String getTaskMessage(Player player) {
        return getTaskMessage();
    }

    @Override
    public boolean hasCompleted(Player player) {
        return player.hasPotionEffect(effect);
    }
}
