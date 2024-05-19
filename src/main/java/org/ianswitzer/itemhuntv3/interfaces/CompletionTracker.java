package org.ianswitzer.itemhuntv3.interfaces;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.ianswitzer.itemhuntv3.events.ResetItemHuntEvent;

import java.util.HashMap;
import java.util.UUID;

public class CompletionTracker implements GenericTask, Listener {
    protected HashMap<UUID, Boolean> completion;

    protected CompletionTracker() {
        completion = new HashMap<>();
    }

    @EventHandler
    public void onItemHuntEnd(ResetItemHuntEvent event) {
        completion.clear();
    }

    @Override
    public String getTaskMessage() {
        return null;
    }

    @Override
    public String getTaskMessage(Player player) {
        return null;
    }

    @Override
    public boolean hasCompleted(Player player) {
        return false;
    }
}
