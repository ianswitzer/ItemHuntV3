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
import org.ianswitzer.itemhuntv3.interfaces.CompletionTracker;
import org.ianswitzer.itemhuntv3.interfaces.GenericTask;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class CauseOfDeathTask extends CompletionTracker implements Listener {
    private final EntityDamageEvent.DamageCause damageCause;

    public CauseOfDeathTask(EntityDamageEvent.DamageCause damageCause) {
        super();
        this.damageCause = damageCause;

        Bukkit.getPluginManager().registerEvents(this, Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("ItemHuntV3")));
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        EntityDamageEvent lastDamageCauseEvent = player.getLastDamageCause();
        if (lastDamageCauseEvent == null) return;

        EntityDamageEvent.DamageCause lastDamageCause = lastDamageCauseEvent.getCause();

        if (lastDamageCause.equals(damageCause)) {
            completion.put(player.getUniqueId(), true);
        }
    }

    @Override
    public String getTaskMessage() {
        return "DEATH BY " + damageCause.name();
    }

    @Override
    public String getTaskMessage(Player player) {
        return getTaskMessage();
    }

    @Override
    public boolean hasCompleted(Player player) {
        return completion.getOrDefault(player.getUniqueId(), false);
    }
}
