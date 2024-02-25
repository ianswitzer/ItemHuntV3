package org.ianswitzer.itemhuntv3.interfaces;

import org.bukkit.entity.Player;

public interface GenericTask {
    String getTaskMessage();
    String getTaskMessage(Player player);
    boolean hasCompleted(Player player);
}
