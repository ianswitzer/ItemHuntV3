package org.ianswitzer.itemhuntv3.tasks;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.ianswitzer.itemhuntv3.interfaces.CompletionTracker;
import org.ianswitzer.itemhuntv3.interfaces.GenericTask;

import java.util.UUID;

public class PotionEffectTask extends CompletionTracker {
    private final PotionEffectType effect;

    public PotionEffectTask(PotionEffectType effect) {
        super();
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
        UUID uuid = player.getUniqueId();
        if (completion.getOrDefault(uuid, false))
            return true;

        if (player.hasPotionEffect(effect)) {
            completion.put(player.getUniqueId(), true);
            return true;
        }

        return false;
    }
}
