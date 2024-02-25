package org.ianswitzer.itemhuntv3.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.ianswitzer.itemhuntv3.ItemHuntV3;

public class ShowWinCondition implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp() || args.length != 1) return invalid(sender);
        if (ItemHuntV3.itemHuntManager.isActive()) return alreadyActive(sender);

        try {
            int round = Integer.parseInt(args[0]);
            ItemHuntV3.itemHuntManager.setWinConditionVisibleRound(round);

            if (round >= 0)
                sender.sendMessage(ChatColor.GREEN + "Win condition will appear starting on round " + round);
            else
                sender.sendMessage(ChatColor.GREEN + "Win condition disabled.");

        } catch (Exception exception) {
            return invalid(sender);
        }

        return true;
    }

    private boolean invalid(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "Invalid command!");
        if(sender.isOp()) sender.sendMessage(ChatColor.GRAY + "Usage: /showwincondition <round>");
        else sender.sendMessage(ChatColor.GRAY + "You do not have permission to configure the win condition!");
        return true;
    }

    private boolean alreadyActive(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "ItemHunt is already active!");
        return true;
    }
}
