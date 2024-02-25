package org.ianswitzer.itemhuntv3.managers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.*;
import org.ianswitzer.itemhuntv3.ItemHuntV3;
import org.ianswitzer.itemhuntv3.interfaces.GenericTask;

import java.util.HashMap;
import java.util.UUID;

public class StatusManager {
    private final HashMap<UUID, Scoreboard> playerScoreboard;
    private BukkitTask scoreboardUpdater;
    private final ScoreboardManager scoreboardManager;
    private final Plugin plugin;

    public StatusManager() {
        plugin = Bukkit.getPluginManager().getPlugin("ItemHuntV3");
        playerScoreboard = new HashMap<>();
        scoreboardManager = Bukkit.getScoreboardManager();
    }

    public void clear(Player player) {
        UUID uuid = player.getUniqueId();
        if (!playerScoreboard.containsKey(uuid)) return;

        Scoreboard scoreboard = playerScoreboard.get(uuid);
        for (String entry : scoreboard.getEntries()) {
            scoreboard.resetScores(entry);
        }
        scoreboard.clearSlot(DisplaySlot.SIDEBAR);
        scoreboard.clearSlot(DisplaySlot.PLAYER_LIST);
    }

    public void clear(Scoreboard scoreboard) {
        for (String entry : scoreboard.getEntries()) {
            scoreboard.resetScores(entry);
        }
    }

    public void start() {
        scoreboardUpdater = createScoreboardUpdater().runTaskTimer(plugin, 20, 20);
    }

    public void stop() {
        if (scoreboardUpdater != null && !scoreboardUpdater.isCancelled()) scoreboardUpdater.cancel();

        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID uuid = player.getUniqueId();
            if (!playerScoreboard.containsKey(uuid)) continue;

            clear(player);
        }
    }

    public Scoreboard getPlayerScoreboard(UUID uuid) {
        if (!playerScoreboard.containsKey(uuid))
            playerScoreboard.put(uuid, scoreboardManager.getNewScoreboard());

        return playerScoreboard.get(uuid);
    }

    public Objective getScoreboardStatusObjective(Scoreboard scoreboard) {
        Objective objective;
        if (scoreboard.getObjective("Status") == null) {
            objective = scoreboard.registerNewObjective("Status", Criteria.DUMMY, ChatColor.WHITE + "" + ChatColor.BOLD + "Status");
        } else
            objective = scoreboard.getObjective("Status");

        assert objective != null;
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        return objective;
    }

    public Objective getScoreboardGlobalObjective(Scoreboard scoreboard) {
        Objective objective;
        if (scoreboard.getObjective("Global") == null) {
            objective = scoreboard.registerNewObjective("Global", Criteria.DUMMY, ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Round");
        } else
            objective = scoreboard.getObjective("Global");

        assert objective != null;
        objective.setDisplaySlot(DisplaySlot.PLAYER_LIST);

        return objective;
    }

    private BukkitRunnable createScoreboardUpdater() {
        return new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    UUID uuid = player.getUniqueId();
                    Scoreboard scoreboard = ItemHuntV3.statusManager.getPlayerScoreboard(uuid);
                    ItemHuntV3.statusManager.clear(scoreboard);

                    for (Player p : Bukkit.getOnlinePlayers()) {
                        int round = ItemHuntV3.itemHuntManager.getPlayerRound(p.getUniqueId());
                        Objective globalObjective = ItemHuntV3.statusManager.getScoreboardGlobalObjective(scoreboard);
                        Score globalRound = globalObjective.getScore(p.getDisplayName());
                        globalRound.setScore(round);
                    }

                    if (!ItemHuntV3.itemHuntManager.isPlayerActive(uuid)) {
                        player.setScoreboard(scoreboard);
                        continue;
                    }

                    int currentRound = ItemHuntV3.itemHuntManager.getCurrentRound();
                    int currentTime = ItemHuntV3.itemHuntManager.getCurrentTime();
                    int secondsPerRound = ItemHuntV3.itemHuntManager.getSecondsPerRound();
                    int playerRound = ItemHuntV3.itemHuntManager.getPlayerRound(uuid);
                    int playerCount = ItemHuntV3.itemHuntManager.getActivePlayerCount();
                    int skipsLeft = ItemHuntV3.itemHuntManager.getSkipsRemaining(uuid);
                    GenericTask task = ItemHuntV3.itemHuntManager.getPlayerTask(uuid);
                    Objective objective = ItemHuntV3.statusManager.getScoreboardStatusObjective(scoreboard);

                    int timeRemaining = secondsPerRound * (playerRound - currentRound + 1) - currentTime;
                    int minutes = (int)Math.floor(timeRemaining / 60.0);
                    int seconds = timeRemaining % 60;

                    String minuteText = (minutes > 9 ? "" : "0") + minutes;
                    String secondText = (seconds > 9 ? "" : "0") + seconds;

                    Score timer = objective.getScore(ChatColor.AQUA + "Time left: " + ChatColor.WHITE + minuteText + ":" + secondText);
                    timer.setScore(7);

                    Score completed = objective.getScore(ChatColor.BLUE + "Your round: " + ChatColor.WHITE + playerRound);
                    completed.setScore(6);

                    Score skips = objective.getScore(ChatColor.AQUA + "Skips: " + ChatColor.WHITE + skipsLeft);
                    skips.setScore(5);

                    Score remaining = objective.getScore(ChatColor.BLUE + "Players: " + ChatColor.WHITE + playerCount);
                    remaining.setScore(4);

                    Score taskLine1 = objective.getScore(ChatColor.AQUA + "Your task: ");
                    taskLine1.setScore(3);

                    Score taskLine2 = objective.getScore(task.getTaskMessage(player));
                    taskLine2.setScore(2);

                    if (ItemHuntV3.itemHuntManager.showWinCondition()) {
                        GenericTask winCondition = ItemHuntV3.itemHuntManager.getPlayerWinCondition(uuid);

                        Score winConditionLine1 = objective.getScore(ChatColor.YELLOW + "Win condition: ");
                        winConditionLine1.setScore(1);

                        Score winConditionLine2 = objective.getScore(winCondition.getTaskMessage(player));
                        winConditionLine2.setScore(0);
                    }

                    player.setScoreboard(scoreboard);
                }
            }
        };
    }
}
