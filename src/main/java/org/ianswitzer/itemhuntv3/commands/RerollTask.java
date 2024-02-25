package org.ianswitzer.itemhuntv3.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ianswitzer.itemhuntv3.ItemHuntV3;

public class RerollTask implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp()) return invalid(sender);
        if (!ItemHuntV3.itemHuntManager.isActive()) return inactive(sender);

        ItemHuntV3.itemHuntManager.reroll((Player) sender);

        return true;
    }

    private boolean invalid(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "You do not have permission to reroll!");
        return true;
    }

    private boolean inactive(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "ItemHunt is not currently active!");
        return true;
    }
}
