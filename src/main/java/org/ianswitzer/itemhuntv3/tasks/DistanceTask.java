package org.ianswitzer.itemhuntv3.tasks;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.ianswitzer.itemhuntv3.interfaces.CompletionTracker;

import java.util.Objects;

public class DistanceTask extends CompletionTracker {
    private final int distance;

    public DistanceTask(int distance) {
        super();
        this.distance = distance;
    }

    @Override
    public String getTaskMessage() {
        return "Distance from (0,0,0) > " + distance;
    }

    @Override
    public boolean hasCompleted(Player player) {
        return player.getLocation().distance(new Location(player.getWorld(), 0, 0, 0)) > distance;
    }

    @Override
    public String getTaskMessage(Player player) {
        return getTaskMessage() + " (" + getTaskProgress(player) + ")";
    }

    private String getTaskProgress(Player player) {
        return Math.round(player.getLocation().distance(new Location(player.getWorld(), 0, 0, 0))) + "/" + distance;
    }
}
