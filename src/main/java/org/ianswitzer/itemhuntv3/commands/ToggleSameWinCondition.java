package org.ianswitzer.itemhuntv3.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.ianswitzer.itemhuntv3.ItemHuntV3;

public class ToggleSameWinCondition implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp()) return invalid(sender);
        if (ItemHuntV3.itemHuntManager.isActive()) return alreadyActive(sender);

        boolean newValue = ItemHuntV3.itemHuntManager.toggleSameWinCondition();
        sender.sendMessage(ChatColor.GREEN + "Players have the same win condition: " + newValue);

        return true;
    }

    private boolean invalid(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "You do not have permission to toggle this!");
        return true;
    }

    private boolean alreadyActive(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "ItemHunt is already active!");
        return true;
    }
}
