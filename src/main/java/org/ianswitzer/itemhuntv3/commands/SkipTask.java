package org.ianswitzer.itemhuntv3.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ianswitzer.itemhuntv3.ItemHuntV3;

public class SkipTask implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!ItemHuntV3.itemHuntManager.isActive()) return inactive(sender);

        if (args.length == 1 && sender.isOp()) {
            Player player = Bukkit.getPlayer(args[0]);
            if (player == null) sender.sendMessage(ChatColor.RED + "Player not found!");
            else if (ItemHuntV3.itemHuntManager.isPlayerActive(player.getUniqueId()))
                ItemHuntV3.itemHuntManager.skip(player);
            else sender.sendMessage(ChatColor.RED + "Player is not currently playing Item Hunt!");

        } else if (ItemHuntV3.itemHuntManager.getSkipsRemaining(((Player)sender).getUniqueId()) > 0 || sender.isOp()) {
            ItemHuntV3.itemHuntManager.skip((Player)sender);

        } else sender.sendMessage(ChatColor.RED + "Cannot skip!");

        return true;
    }

    private boolean inactive(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "ItemHunt is not currently active!");
        return true;
    }
}
