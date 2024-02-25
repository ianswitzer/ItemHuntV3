package org.ianswitzer.itemhuntv3.tasks;

import org.bukkit.Axis;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.ianswitzer.itemhuntv3.interfaces.GenericTask;

import java.util.Objects;

public class LocationTask implements GenericTask {
    private boolean greaterThan;
    private Axis axis;
    private double threshold;
    private final Location location;

    public LocationTask(Axis axis, double threshold, boolean greaterThan) {
        this.axis = axis;
        this.threshold = threshold;
        this.greaterThan = greaterThan;
        this.location = null;
    }

    public LocationTask(Location location) {
        this.location = location;
    }

    @Override
    public String getTaskMessage() {
        if (location == null)
            return axis.name() + (greaterThan ? " > " : " < ") + threshold;
        else
            return Objects.requireNonNull(location.getWorld()).getName() + ": " + Math.round(location.getX()) + "," + Math.round(location.getY()) + "," + Math.round(location.getZ());
    }

    @Override
    public String getTaskMessage(Player player) {
        return getTaskMessage() + " (" + getTaskProgress(player) + ")";
    }

    @Override
    public boolean hasCompleted(Player player) {
        if (location == null)
            return switch (axis) {
                case X -> greaterThan ? player.getLocation().getX() > threshold : player.getLocation().getX() < threshold;
                case Y -> greaterThan ? player.getLocation().getY() > threshold : player.getLocation().getY() < threshold;
                case Z -> greaterThan ? player.getLocation().getZ() > threshold : player.getLocation().getZ() < threshold;
            };
        else {
            Location playerLocation = player.getLocation();
            if (!Objects.requireNonNull(location.getWorld()).getName().equalsIgnoreCase(Objects.requireNonNull(playerLocation.getWorld()).getName())) return false;

            if (Math.round(playerLocation.getX()) != Math.round(location.getX())) return false;
            if (Math.round(playerLocation.getY()) != Math.round(location.getY())) return false;
            return Math.round(playerLocation.getZ()) == Math.round(location.getZ());
        }
    }

    private String getTaskProgress(Player player) {
        if (location == null)
            return switch (axis) {
                case X -> "X = " + player.getLocation().getX();
                case Y -> "Y = " + player.getLocation().getY();
                case Z -> "Z = " + player.getLocation().getZ();
            };
        else {
            Location playerLocation = player.getLocation();
            return Math.round(playerLocation.getX()) + "," + Math.round(playerLocation.getY()) + "," + Math.round(playerLocation.getZ());
        }
    }
}
