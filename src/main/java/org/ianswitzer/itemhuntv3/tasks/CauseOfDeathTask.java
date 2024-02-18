package org.ianswitzer.itemhuntv3.tasks;

import org.bukkit.Bukkit;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.ianswitzer.itemhuntv3.interfaces.GenericTask;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class CauseOfDeathTask implements GenericTask, Listener {
    private final HashMap<UUID, Boolean> completed;
    private final EntityDamageEvent.DamageCause damageCause;

    public CauseOfDeathTask(EntityDamageEvent.DamageCause damageCause) {
        completed = new HashMap<>();
        this.damageCause = damageCause;

        Bukkit.getPluginManager().registerEvents(this, Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("ItemHuntV3")));
    }

    public void resetCompletion() {
        completed.clear();
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        EntityDamageEvent lastDamageCauseEvent = player.getLastDamageCause();
        if (lastDamageCauseEvent == null) return;

        EntityDamageEvent.DamageCause lastDamageCause = lastDamageCauseEvent.getCause();

        if (lastDamageCause.equals(damageCause)) {
            completed.put(player.getUniqueId(), true);
        }
    }

    @Override
    public String getTaskMessage() {
        return "DEATH BY " + damageCause.name();
    }

    @Override
    public boolean hasCompleted(Player player) {
        return completed.getOrDefault(player.getUniqueId(), false);
    }
}
