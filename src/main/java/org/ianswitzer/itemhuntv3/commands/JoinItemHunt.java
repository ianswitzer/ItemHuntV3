package org.ianswitzer.itemhuntv3.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ianswitzer.itemhuntv3.ItemHuntV3;

public class JoinItemHunt implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!ItemHuntV3.itemHuntManager.isActive()) return inactive(sender);
        ItemHuntV3.itemHuntManager.join((Player) sender);

        return true;
    }

    private boolean inactive(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "ItemHunt is not currently active!");
        return true;
    }
}
