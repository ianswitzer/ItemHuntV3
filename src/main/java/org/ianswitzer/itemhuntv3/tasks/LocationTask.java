package org.ianswitzer.itemhuntv3.tasks;

import org.bukkit.Axis;
import org.bukkit.entity.Player;
import org.ianswitzer.itemhuntv3.interfaces.GenericTask;

public class LocationTask implements GenericTask {
    private boolean greaterThan;
    private Axis axis;
    private double threshold;

    public LocationTask(Axis axis, double threshold, boolean greaterThan) {
        this.axis = axis;
        this.threshold = threshold;
        this.greaterThan = greaterThan;
    }

    @Override
    public String getTaskMessage() {
        return axis.name() + (greaterThan ? " > " : " < ") + threshold;
    }

    @Override
    public boolean hasCompleted(Player player) {
        switch (axis) {
            case X:
                return greaterThan ? player.getLocation().getX() > threshold : player.getLocation().getX() < threshold;
            case Y:
                return greaterThan ? player.getLocation().getY() > threshold : player.getLocation().getY() < threshold;
            case Z:
                return greaterThan ? player.getLocation().getZ() > threshold : player.getLocation().getZ() < threshold;
        }
        return false;
    }
}
