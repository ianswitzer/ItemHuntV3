package org.ianswitzer.itemhuntv3.tasks;

import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.ianswitzer.itemhuntv3.interfaces.GenericTask;

public class StatTask implements GenericTask {
    private final Statistic statistic;
    private final int threshold;
    private EntityType entityType;
    private String nameOverride;
    private int displayMultiplier = 1;

    public StatTask(Statistic statistic, int threshold) {
        this.statistic = statistic;
        this.threshold = threshold;
    }

    public StatTask(Statistic statistic, int threshold, String nameOverride) {
        this(statistic, threshold);
        this.nameOverride = nameOverride;
    }

    public StatTask(Statistic statistic, int threshold, String nameOverride, int displayMultiplier) {
        this(statistic, threshold, nameOverride);
        this.displayMultiplier = displayMultiplier;
    }

    public StatTask(Statistic statistic, EntityType entityType, int threshold) {
        this(statistic, threshold);
        this.entityType = entityType;
    }

    public StatTask(Statistic statistic, EntityType entityType, int threshold, String nameOverride) {
        this(statistic, entityType, threshold);
        this.nameOverride = nameOverride;
    }

    public StatTask(Statistic statistic, EntityType entityType, int threshold, String nameOverride, int displayMultiplier) {
        this(statistic, entityType, threshold, nameOverride);
        this.displayMultiplier = displayMultiplier;
    }

    public Statistic getStatistic() {
        return statistic;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    private String getTaskProgress(Player player) {
        if (entityType == null)
            return Math.round((float) player.getStatistic(statistic) / displayMultiplier) + "/" + Math.round((float) threshold / displayMultiplier);
        else
            return Math.round((float) player.getStatistic(statistic, entityType) / displayMultiplier) + "/" + Math.round((float) threshold / displayMultiplier);
    }

    @Override
    public String getTaskMessage(Player player) {
        return getTaskMessage() + " (" + getTaskProgress(player) + ")";
    }

    @Override
    public String getTaskMessage() {
        String name = statistic.name();
        if (nameOverride != null) name = nameOverride;

        if (entityType == null)
            return name + " >= " + threshold;
        else
            return name + " " + entityType.name() + " >= " + threshold;
    }

    @Override
    public boolean hasCompleted(Player player) {
        if (entityType == null)
            return player.getStatistic(statistic) >= threshold;
        else
            return player.getStatistic(statistic, entityType) >= threshold;
    }
}
