package org.ianswitzer.itemhuntv3;

import org.bukkit.plugin.java.JavaPlugin;
import org.ianswitzer.itemhuntv3.commands.*;
import org.ianswitzer.itemhuntv3.managers.ItemHuntManager;
import org.ianswitzer.itemhuntv3.managers.LogManager;
import org.ianswitzer.itemhuntv3.managers.StatusManager;
import org.ianswitzer.itemhuntv3.managers.TaskManager;
import org.ianswitzer.itemhuntv3.tabcompleters.ManageItemHuntTabCompleter;
import org.ianswitzer.itemhuntv3.tabcompleters.SkipTaskTabCompleter;

import java.util.Objects;

// TODO: keep timer up for losers
// TODO: add task to get to 0,0,0
// TODO: add constructor for LocationTask to take a world name

public final class ItemHuntV3 extends JavaPlugin {
    public static StatusManager statusManager;
    public static ItemHuntManager itemHuntManager;
    public static TaskManager taskManager;
    public static LogManager logManager;

    @Override
    public void onEnable() {
        Objects.requireNonNull(getCommand("itemhunt")).setExecutor(new ManageItemHunt());
        Objects.requireNonNull(getCommand("setskipcount")).setExecutor(new SetSkipCount());
        Objects.requireNonNull(getCommand("skip")).setExecutor(new SkipTask());
        Objects.requireNonNull(getCommand("reroll")).setExecutor(new RerollTask());
        Objects.requireNonNull(getCommand("join")).setExecutor(new JoinItemHunt());
        Objects.requireNonNull(getCommand("showwincondition")).setExecutor(new ShowWinCondition());
        Objects.requireNonNull(getCommand("togglesamewincondition")).setExecutor(new ToggleSameWinCondition());
        Objects.requireNonNull(getCommand("itemhunt")).setTabCompleter(new ManageItemHuntTabCompleter());
        Objects.requireNonNull(getCommand("skip")).setTabCompleter(new SkipTaskTabCompleter());

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
