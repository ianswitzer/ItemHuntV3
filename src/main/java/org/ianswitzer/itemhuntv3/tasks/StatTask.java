package org.ianswitzer.itemhuntv3.tasks;

import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.ianswitzer.itemhuntv3.interfaces.GenericTask;

public class StatTask implements GenericTask {
    private final Statistic statistic;
    private final int threshold;
    private EntityType entityType;

    public StatTask(Statistic statistic, int threshold) {
        this.statistic = statistic;
        this.threshold = threshold;
    }

    public StatTask(Statistic statistic, EntityType entityType, int threshold) {
        this.statistic = statistic;
        this.threshold = threshold;
        this.entityType = entityType;
    }

    public Statistic getStatistic() {
        return statistic;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    @Override
    public String getTaskMessage() {
        if (entityType == null)
            return statistic.name() + " >= " + threshold;
        else
            return statistic.name() + " " + entityType.name() + " >= " + threshold;
    }

    @Override
    public boolean hasCompleted(Player player) {
        if (entityType == null)
            return player.getStatistic(statistic) >= threshold;
        else
            return player.getStatistic(statistic, entityType) >= threshold;
    }
}
