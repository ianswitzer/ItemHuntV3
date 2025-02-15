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
//      - show current round and time left in round
//      - show task for each player
// TODO: add constructor for LocationTask to take a world name
// TODO: fireworks/jingle for the winner
// TODO: add RNG for count of mobs killed
// TODO: add lightning round - 1 min per round, 5 tier1 items then 5 tier 2 etc.
// TODO: move everything to config.yml
//      - all tasks, skip count, time per round, round spacing (how many times to pull from each tier)

// TODO: play block.tripwire.click_on for 10 second countdown
// TODO: play sound every time round ends
// TODO: play embarrassing sound when skipping - minecraft:entity.villager.ambient /.no
// TODO: play sequence of horns after win
// TODO: play sound for individual people when they complete a round
// TODO: play sound when more time gets added - minecraft:entity.zombie_villager.converted
// TODO: play sound when someone gets eliminated -

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
