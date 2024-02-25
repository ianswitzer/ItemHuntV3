package org.ianswitzer.itemhuntv3.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.ianswitzer.itemhuntv3.ItemHuntV3;

public class SetSkipCount implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp() || args.length != 1) return invalid(sender);

        if (ItemHuntV3.itemHuntManager.isActive()) return alreadyActive(sender);

        try {
            int skipCount = Integer.parseInt(args[0]);
            if (skipCount < 0) return invalid(sender);

            ItemHuntV3.itemHuntManager.setSkipCount(skipCount);
        } catch (Exception exception) {
            return invalid(sender);
        }

        return true;
    }

    private boolean invalid(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "Invalid command!");
        if(sender.isOp()) sender.sendMessage(ChatColor.GRAY + "Usage: /setskipcount <number>");
        else sender.sendMessage(ChatColor.GRAY + "You do not have permission to set the skip count!");
        return true;
    }

    private boolean alreadyActive(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "ItemHunt is already active!");
        return true;
    }
}
