package org.ianswitzer.itemhuntv3;

import org.bukkit.plugin.java.JavaPlugin;
import org.ianswitzer.itemhuntv3.commands.ManageItemHunt;
import org.ianswitzer.itemhuntv3.managers.ItemHuntManager;
import org.ianswitzer.itemhuntv3.managers.LogManager;
import org.ianswitzer.itemhuntv3.managers.StatusManager;
import org.ianswitzer.itemhuntv3.managers.TaskManager;
import org.ianswitzer.itemhuntv3.tabcompleters.ManageItemHuntTabCompleter;

import java.util.Objects;

public final class ItemHuntV3 extends JavaPlugin {
    public static StatusManager statusManager;
    public static ItemHuntManager itemHuntManager;
    public static TaskManager taskManager;
    public static LogManager logManager;

    @Override
    public void onEnable() {
        Objects.requireNonNull(getCommand("itemhunt")).setExecutor(new ManageItemHunt());
        Objects.requireNonNull(getCommand("itemhunt")).setTabCompleter(new ManageItemHuntTabCompleter());

        taskManager = new TaskManager();
        itemHuntManager = new ItemHuntManager(300);
        statusManager = new StatusManager();
        logManager = new LogManager();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        itemHuntManager.stop();
    }
}
