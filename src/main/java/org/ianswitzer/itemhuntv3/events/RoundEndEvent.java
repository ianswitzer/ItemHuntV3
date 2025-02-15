package org.ianswitzer.itemhuntv3.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RoundEndEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private int round;

    public RoundEndEvent(int round) {
        this.round = round;
    }

    public int getRound() {
        return round;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
