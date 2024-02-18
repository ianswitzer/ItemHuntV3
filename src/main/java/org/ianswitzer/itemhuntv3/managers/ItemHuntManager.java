package org.ianswitzer.itemhuntv3.managers;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.ianswitzer.itemhuntv3.ItemHuntV3;
import org.ianswitzer.itemhuntv3.interfaces.GenericTask;
import org.ianswitzer.itemhuntv3.tasks.StatTask;

import java.util.*;

public class ItemHuntManager {
    private boolean active;
    private int round;
    private final int secondsPerRound;
    private int currentTime;
    private int skipCount;
    private final HashMap<UUID, Integer> playerRound;
    private final HashMap<UUID, Integer> playerSkips;
    private final HashMap<UUID, GenericTask> playerTask;
    private final HashMap<UUID, Boolean> playerStatus;
    private BukkitTask checkForCompletion;
    private BukkitTask timer;
    private final Plugin plugin;

    public ItemHuntManager(int timePerRound) {
        plugin = Bukkit.getPluginManager().getPlugin("ItemHuntV3");
        active = false;
        round = 1;
        this.secondsPerRound = timePerRound;
        currentTime = 0;
        playerRound = new HashMap<>();
        playerTask = new HashMap<>();
        playerStatus = new HashMap<>();
        playerSkips = new HashMap<>();
        skipCount = 0;
    }

    public void stop() {
        active = false;
        if (checkForCompletion != null && !checkForCompletion.isCancelled()) checkForCompletion.cancel();
        if (timer != null && !timer.isCancelled()) timer.cancel();
        ItemHuntV3.statusManager.stop();
        playerRound.clear();
        playerTask.clear();
        playerStatus.clear();
        playerSkips.clear();
        round = 1;
        currentTime = 0;
        ItemHuntV3.taskManager.resetCauseOfDeathCompletion();
        ItemHuntV3.logManager.writeLog("Item Hunt ending.");
        ItemHuntV3.logManager.close();
    }

    public void start() {
        ItemHuntV3.logManager.open();
        ItemHuntV3.logManager.writeLog("Item Hunt starting.");
        ItemHuntV3.taskManager.resetCauseOfDeathCompletion();
        configureWorlds();
        wipePlayers();
        round = 1;
        currentTime = 0;
        active = true;

        Bukkit.broadcastMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Item Hunt is starting!");

        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID uuid = player.getUniqueId();
            playerStatus.put(uuid, true);
            playerRound.put(uuid, round);
            playerSkips.put(uuid, skipCount);
            update(player);
        }

        timer = createTimer().runTaskTimer(plugin, 20, 20);
        checkForCompletion = createCompletionChecker().runTaskTimer(plugin, 20, 10);
        ItemHuntV3.statusManager.start();
    }

    public boolean isActive() {
        return active;
    }

    public void reroll(Player player) {
        UUID uuid = player.getUniqueId();
        if (!isPlayerActive(uuid)) return;

        GenericTask task = ItemHuntV3.taskManager.getRandomTask(playerRound.get(uuid));
        ItemHuntV3.logManager.writeLog(player.getDisplayName() + " has rerolled. Old task: " + (playerTask.get(uuid) != null ? playerTask.get(uuid).getTaskMessage() : "") + ". New task: " + task.getTaskMessage());
        playerTask.put(uuid, task);

        player.sendMessage(ChatColor.ITALIC + "" + ChatColor.AQUA + "Your task: " + task.getTaskMessage());
    }

    public void update(Player player) {
        UUID uuid = player.getUniqueId();
        if (!isPlayerActive(uuid)) return;

        GenericTask task = ItemHuntV3.taskManager.getRandomTask(playerRound.get(uuid));
        playerTask.put(uuid, task);
        ItemHuntV3.logManager.writeLog(player.getDisplayName() + " has a new task: " + task.getTaskMessage());

        player.sendMessage(ChatColor.ITALIC + "" + ChatColor.AQUA + "Your task: " + task.getTaskMessage());
    }

    public void shuffle() {
        ItemHuntV3.logManager.writeLog("Shuffling all tasks.");
        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID uuid = player.getUniqueId();
            if (!isPlayerActive(uuid)) continue;

            update(player);
        }
    }

    public boolean skip(Player player) {
        UUID uuid = player.getUniqueId();
        if (!isPlayerActive(uuid)) return false;

        int skipsRemaining = playerSkips.get(uuid);
        if (skipsRemaining <= 0 && !player.isOp()) return false;

        int nextRound = playerRound.get(uuid) + 1;
        playerRound.put(uuid, nextRound);

        Bukkit.broadcastMessage(ChatColor.AQUA + player.getDisplayName() + " has skipped Round " + (nextRound - 1) + ": " + playerTask.get(uuid).getTaskMessage());
        ItemHuntV3.logManager.writeLog(player.getDisplayName() + " has skipped Round " + (nextRound - 1) + ": " + playerTask.get(uuid).getTaskMessage());
        update(player);

        playerSkips.put(uuid, skipsRemaining - 1);
        return true;
    }

    public void join(Player player) {
        UUID uuid = player.getUniqueId();
        if (isPlayerActive(uuid)) return;

        GenericTask task = ItemHuntV3.taskManager.getRandomTask(round);
        playerRound.put(uuid, round);
        playerTask.put(uuid, task);
        playerStatus.put(uuid, true);
        playerSkips.put(uuid, 0);
        player.sendMessage(ChatColor.ITALIC + "" + ChatColor.AQUA + "Your task: " + task.getTaskMessage());
    }

    public void extend(int time) {
        if (time < 0) return;

        currentTime -= time;
        Bukkit.broadcastMessage(ChatColor.AQUA + "" + ChatColor.ITALIC + "Timer extended by " + time + " seconds.");
    }

    public void setSkipCount(int count) {
        skipCount = count;
    }

    public int getSkipsRemaining(UUID uuid) {
        return playerSkips.get(uuid);
    }

    public void incrementTimer() {
        currentTime += 1;
    }

    public boolean isRoundOver() {
        return currentTime >= secondsPerRound;
    }

    public GenericTask getPlayerTask(UUID uuid) {
        return playerTask.get(uuid);
    }

    public boolean isPlayerActive(UUID uuid) {
        return playerStatus.getOrDefault(uuid, false);
    }

    public int getPlayerRound(UUID uuid) {
        return playerRound.get(uuid);
    }

    public int getCurrentRound() {
        return round;
    }

    public int getCurrentTime() {
        return currentTime;
    }

    public int getSecondsPerRound() {
        return secondsPerRound;
    }

    public int getActivePlayerCount() {
        int total = 0;
        for (UUID uuid : playerStatus.keySet()) {
            if (playerStatus.get(uuid)) total += 1;
        }

        return total;
    }

    public void playerFinishRound(Player player) {
        UUID uuid = player.getUniqueId();
        int currentRound = playerRound.get(uuid);
        int newRound = currentRound + 1;
        GenericTask task = playerTask.get(uuid);
        Bukkit.broadcastMessage(ChatColor.AQUA + player.getDisplayName() + " has completed Round " + currentRound + ": " + task.getTaskMessage());
        ItemHuntV3.logManager.writeLog(player.getDisplayName() + " has completed Round " + currentRound + ": " + task.getTaskMessage());

        if (currentRound == 10) {
            stop();
            Bukkit.broadcastMessage(ChatColor.GREEN + player.getDisplayName() + " HAS WON ITEM HUNT!");
        } else {
            playerRound.put(uuid, newRound);
            update(player);
        }
    }

    public void endCurrentRound() {
        currentTime = 0;
        int nextRound = round + 1;

        ArrayList<Player> activePlayers = new ArrayList<>();
        ArrayList<Player> wouldBeActivePlayers = new ArrayList<>();
        ArrayList<Player> wouldBeInactivePlayers = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID uuid = player.getUniqueId();

            if (!isPlayerActive(uuid)) continue;
            activePlayers.add(player);
            
            if (playerRound.get(uuid) >= nextRound)
                wouldBeActivePlayers.add(player);
            else 
                wouldBeInactivePlayers.add(player);
        }
        
        if (activePlayers.isEmpty()) {
            Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + "No players remaining! Stopping Item Hunt.");
            stop();
        } else if (wouldBeActivePlayers.isEmpty()) {
            Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.ITALIC + "No one has completed Round " + round + " - extending the timer.");
        } else {
            for (Player player : wouldBeInactivePlayers) {
                UUID uuid = player.getUniqueId();
                playerStatus.put(uuid, false);
                Bukkit.broadcastMessage(ChatColor.RED + player.getDisplayName() + " failed to complete their task for Round " + round + ": " + playerTask.get(uuid).getTaskMessage());
            }
            if (wouldBeActivePlayers.size() == 1) {
                Bukkit.broadcastMessage(ChatColor.GOLD + "" + ChatColor.BOLD + wouldBeActivePlayers.get(0).getDisplayName() + " has won Item Hunt!");
                stop();
            }
            round += 1;
        }

        for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
            UUID uuid = player.getUniqueId();
            if (playerStatus.containsKey(uuid) && !player.isOnline())
                playerStatus.put(uuid, false);
        }
    }

    private BukkitRunnable createTimer() {
        return new BukkitRunnable() {
            @Override
            public void run() {
                ItemHuntV3.itemHuntManager.incrementTimer();

                if (ItemHuntV3.itemHuntManager.isRoundOver())
                    ItemHuntV3.itemHuntManager.endCurrentRound();
            }
        };
    }

    private BukkitRunnable createCompletionChecker() {
        return new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    UUID uuid = player.getUniqueId();
                    if (!ItemHuntV3.itemHuntManager.isPlayerActive(uuid)) continue;

                    GenericTask task = ItemHuntV3.itemHuntManager.getPlayerTask(uuid);
                    if (task.hasCompleted(player)) {
                        ItemHuntV3.itemHuntManager.playerFinishRound(player);
                    }
                }
            }
        };
    }

    private void configureWorlds() {
        for(World world : Bukkit.getWorlds()) {
            world.setDifficulty(Difficulty.HARD);
            world.setGameRule(GameRule.KEEP_INVENTORY, true);
            world.setTime(0);
            world.setWeatherDuration(1200);
            world.setGameRule(GameRule.SPAWN_RADIUS, 0);
        }
    }

    private void wipePlayers() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            player.getInventory().clear();
            player.setGameMode(GameMode.SURVIVAL);
            player.getActivePotionEffects().clear();
            player.setHealth(20.0);
            player.setExp(0);
            player.setTotalExperience(0);
            player.setLevel(0);
            player.setSaturation(20);
            player.setFoodLevel(20);

            for (StatTask task : ItemHuntV3.taskManager.getAllStatistics()) {
                if (task.getEntityType() == null)
                    player.setStatistic(task.getStatistic(), 0);
                else
                    player.setStatistic(task.getStatistic(), task.getEntityType(), 0);
            }

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "advancement revoke " + player.getDisplayName() + " everything");
            player.teleport(Objects.requireNonNull(Bukkit.getWorld("world")).getSpawnLocation());
        }
    }
}
