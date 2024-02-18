package org.ianswitzer.itemhuntv3.interfaces;

import org.bukkit.entity.Player;

public interface GenericTask {
    String getTaskMessage();
    boolean hasCompleted(Player player);
}
