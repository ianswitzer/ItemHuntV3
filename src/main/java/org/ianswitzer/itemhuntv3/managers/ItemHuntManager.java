package org.ianswitzer.itemhuntv3.managers;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.ianswitzer.itemhuntv3.ItemHuntV3;
import org.ianswitzer.itemhuntv3.events.ResetItemHuntEvent;
import org.ianswitzer.itemhuntv3.interfaces.GenericTask;
import org.ianswitzer.itemhuntv3.tasks.StatTask;

import java.util.*;

public class ItemHuntManager {
    private boolean active;
    private int round;
    private final int secondsPerRound;
    private int currentTime;
    private int skipCount;
    private final HashMap<UUID, Integer> playerRounds;
    private final HashMap<UUID, Integer> playerSkips;
    private final HashMap<UUID, GenericTask> playerTasks;
    private final HashMap<UUID, GenericTask> playerWinConditions;
    private final HashMap<UUID, Boolean> playerStatuses;
    private BukkitTask checkForCompletion;
    private BukkitTask timer;
    private final Plugin plugin;
    private int winConditionVisibleRound;
    private boolean sameWinCondition;

    public ItemHuntManager(int timePerRound) {
        plugin = Bukkit.getPluginManager().getPlugin("ItemHuntV3");
        active = false;
        round = 1;
        this.secondsPerRound = timePerRound;
        currentTime = 0;
        playerRounds = new HashMap<>();
        playerTasks = new HashMap<>();
        playerStatuses = new HashMap<>();
        playerSkips = new HashMap<>();
        playerWinConditions = new HashMap<>();
        skipCount = 1;
        winConditionVisibleRound = 0;
        sameWinCondition = true;
    }

    public void stop() {
        active = false;
        if (checkForCompletion != null && !checkForCompletion.isCancelled()) checkForCompletion.cancel();
        if (timer != null && !timer.isCancelled()) timer.cancel();
        ItemHuntV3.statusManager.stop();
        playerRounds.clear();
        playerTasks.clear();
        playerStatuses.clear();
        playerSkips.clear();
        playerWinConditions.clear();
        round = 1;
        currentTime = 0;
        Bukkit.getPluginManager().callEvent(new ResetItemHuntEvent());
        ItemHuntV3.logManager.writeLog("Item Hunt ending.");
        ItemHuntV3.logManager.close();
    }

    public void start() {
        ItemHuntV3.logManager.open();
        ItemHuntV3.logManager.writeLog("Item Hunt starting.");
        Bukkit.getPluginManager().callEvent(new ResetItemHuntEvent());
        configureWorlds();
        wipePlayers();
        round = 1;
        currentTime = 0;
        active = true;
        Bukkit.broadcastMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Item Hunt is starting!");

        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID uuid = player.getUniqueId();
            playerStatuses.put(uuid, true);
            playerRounds.put(uuid, round);
            playerSkips.put(uuid, skipCount);
            update(player, false);
        }

        timer = createTimer().runTaskTimer(plugin, 20, 20);
        checkForCompletion = createCompletionChecker().runTaskTimer(plugin, 20, 10);
        ItemHuntV3.statusManager.start();
    }

    public void skip(Player player) {
        UUID uuid = player.getUniqueId();

        int skipsRemaining = playerSkips.get(uuid);
        playerSkips.put(uuid, Math.max(skipsRemaining - 1, 0));

        int nextRound = playerRounds.get(uuid) + 1;
        playerRounds.put(uuid, nextRound);

        Bukkit.broadcastMessage(ChatColor.AQUA + player.getDisplayName() + " has skipped Round " + (nextRound - 1) + ": " + playerTasks.get(uuid).getTaskMessage());
        ItemHuntV3.logManager.writeLog(player.getDisplayName() + " has skipped Round " + (nextRound - 1) + ": " + playerTasks.get(uuid).getTaskMessage());
        boolean checkCompletion = nextRound > 10;
        update(player, checkCompletion);
    }

    public void playerFinishRound(Player player) {
        UUID uuid = player.getUniqueId();
        int currentRound = playerRounds.get(uuid);
        int newRound = currentRound + 1;

        GenericTask task = playerTasks.get(uuid);
        Bukkit.broadcastMessage(ChatColor.AQUA + player.getDisplayName() + " has completed Round " + currentRound + ": " + task.getTaskMessage());
        ItemHuntV3.logManager.writeLog(player.getDisplayName() + " has completed Round " + currentRound + ": " + task.getTaskMessage());

        playerRounds.put(uuid, newRound);
        boolean checkCompletion = newRound > 10;
        update(player, checkCompletion);
    }

    public void endCurrentRound() {
        currentTime = 0;
        int nextRound = round + 1;

        deactivateOfflinePlayers();

        int activePlayers = 0;
        ArrayList<Player> wouldBeActivePlayers = new ArrayList<>();
        ArrayList<Player> wouldBeInactivePlayers = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID uuid = player.getUniqueId();

            if (!isPlayerActive(uuid)) continue;
            activePlayers += 1;

            int currentRound = playerRounds.get(uuid);
            if (currentRound >= nextRound || (currentRound == round && playerSkips.get(uuid) > 0))
                wouldBeActivePlayers.add(player);
            else 
                wouldBeInactivePlayers.add(player);
        }
        
        if (activePlayers == 0) {
            Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + "No players remaining! Stopping Item Hunt.");
            stop();
            return;
        }

        if (wouldBeActivePlayers.isEmpty()) {
            Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.ITALIC + "No one has completed Round " + round + " - extending the timer.");

        } else {
            for (Player player : wouldBeInactivePlayers) {
                UUID uuid = player.getUniqueId();
                playerStatuses.put(uuid, false);
                Bukkit.broadcastMessage(ChatColor.RED + player.getDisplayName() + " failed to complete their task for Round " + round + ": " + playerTasks.get(uuid).getTaskMessage());
                player.setGameMode(GameMode.SPECTATOR);
            }

            if (wouldBeActivePlayers.size() == 1) {
                playerWonGame(wouldBeActivePlayers.get(0));

            } else {
                for (Player player : wouldBeActivePlayers) {
                    if (playerRounds.get(player.getUniqueId()) == round)
                        skip(player);
                }
            }

            round += 1;
        }
    }

    public void join(Player player) {
        UUID uuid = player.getUniqueId();
        if (isPlayerActive(uuid)) return;

        player.setGameMode(GameMode.SURVIVAL);
        GenericTask task = ItemHuntV3.taskManager.getRandomTask(round);
        playerRounds.put(uuid, round);
        playerTasks.put(uuid, task);
        playerStatuses.put(uuid, true);
        playerSkips.put(uuid, 0);
        player.sendMessage(ChatColor.ITALIC + "" + ChatColor.AQUA + "Your task: " + task.getTaskMessage());
    }

    public void playerWonGame(Player player) {
        Bukkit.broadcastMessage(ChatColor.GOLD + "" + ChatColor.BOLD + player.getDisplayName() + " has won Item Hunt!");
        stop();
    }

    public void reroll(Player player) {
        UUID uuid = player.getUniqueId();
        if (!isPlayerActive(uuid)) return;

        int currentRound = playerRounds.get(uuid);
        GenericTask task = ItemHuntV3.taskManager.getRandomTask(currentRound);
        while (currentRound > 10 && task.hasCompleted(player))
            task = ItemHuntV3.taskManager.getRandomTask(currentRound);

        ItemHuntV3.logManager.writeLog(player.getDisplayName() + " has rerolled. Old task: " + (playerTasks.get(uuid) != null ? playerTasks.get(uuid).getTaskMessage() : "") + ". New task: " + task.getTaskMessage());
        playerTasks.put(uuid, task);

        player.sendMessage(ChatColor.ITALIC + "" + ChatColor.AQUA + "Your task: " + task.getTaskMessage());
    }

    public void update(Player player, boolean checkCompletion) {
        UUID uuid = player.getUniqueId();
        if (!isPlayerActive(uuid)) return;

        GenericTask task = ItemHuntV3.taskManager.getRandomTask(playerRounds.get(uuid));
        while (checkCompletion && task.hasCompleted(player))
            task = ItemHuntV3.taskManager.getRandomTask(playerRounds.get(uuid));

        playerTasks.put(uuid, task);
        ItemHuntV3.logManager.writeLog(player.getDisplayName() + " has a new task: " + task.getTaskMessage());

        player.sendMessage(ChatColor.ITALIC + "" + ChatColor.AQUA + "Your task: " + task.getTaskMessage());
    }

    public void extend(int time) {
        if (time <= 0) return;

        currentTime -= time;
        Bukkit.broadcastMessage(ChatColor.AQUA + "" + ChatColor.ITALIC + "Timer extended by " + time + " seconds.");
    }

    public boolean isActive() {
        return active;
    }

    public void setSkipCount(int count) {
        skipCount = count;
    }

    public int getSkipsRemaining(UUID uuid) {
        if (!playerStatuses.getOrDefault(uuid, false)) return 0;
        return playerSkips.get(uuid);
    }

    public void incrementTimer() {
        currentTime += 1;
    }

    public boolean isRoundOver() {
        return currentTime >= secondsPerRound;
    }

    public GenericTask getPlayerTask(UUID uuid) {
        return playerTasks.get(uuid);
    }

    public GenericTask getPlayerWinCondition(UUID uuid) {
        if (sameWinCondition)
            return ItemHuntV3.taskManager.getWinCondition(true);

        if (!playerWinConditions.containsKey(uuid))
            playerWinConditions.put(uuid, ItemHuntV3.taskManager.getWinCondition(false));

        return playerWinConditions.get(uuid);
    }

    public boolean isPlayerActive(UUID uuid) {
        return playerStatuses.getOrDefault(uuid, false);
    }

    public int getPlayerRound(UUID uuid) {
        return playerRounds.get(uuid);
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
        for (UUID uuid : playerStatuses.keySet()) {
            if (playerStatuses.get(uuid)) total += 1;
        }

        return total;
    }

    private void deactivateOfflinePlayers() {
        for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
            UUID uuid = player.getUniqueId();
            if (playerStatuses.containsKey(uuid) && !player.isOnline())
                playerStatuses.put(uuid, false);
        }
    }

    public boolean showWinCondition() {
        return winConditionVisibleRound >= 0 && round >= winConditionVisibleRound;
    }

    public void setWinConditionVisibleRound(int visibleRound) {
        winConditionVisibleRound = visibleRound;
    }

    public boolean toggleSameWinCondition() {
        sameWinCondition = !sameWinCondition;
        return sameWinCondition;
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

                    if (winConditionVisibleRound >= 0 && round >= winConditionVisibleRound) {
                        GenericTask winCondition = ItemHuntV3.itemHuntManager.getPlayerWinCondition(uuid);
                        if (winCondition.hasCompleted(player)) {
                            Bukkit.broadcastMessage(ChatColor.AQUA + player.getDisplayName() + " has completed the Win Condition: " + winCondition.getTaskMessage());
                            ItemHuntV3.logManager.writeLog(player.getDisplayName() + " has completed the Win Condition: " + winCondition.getTaskMessage());

                            ItemHuntV3.itemHuntManager.playerWonGame(player);
                        }
                    }

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
            player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
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

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "recipe give @a *");
    }
}
