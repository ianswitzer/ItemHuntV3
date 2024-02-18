package org.ianswitzer.itemhuntv3.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ianswitzer.itemhuntv3.ItemHuntV3;

public class ManageItemHunt implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length < 1) return invalid(sender);

        if (sender.isOp()) {
            switch (args[0].toLowerCase()) {
                case "join" -> {
                    if (!ItemHuntV3.itemHuntManager.isActive()) return inactive(sender);
                    ItemHuntV3.itemHuntManager.join((Player) sender);
                }
                case "start" -> {
                    if (ItemHuntV3.itemHuntManager.isActive()) return alreadyActive(sender);
                    ItemHuntV3.itemHuntManager.start();
                }
                case "stop" -> {
                    if (!ItemHuntV3.itemHuntManager.isActive()) return inactive(sender);
                    ItemHuntV3.itemHuntManager.stop();
                    sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Item Hunt stopped.");
                }
                case "clear" -> ItemHuntV3.statusManager.clear((Player) sender);
                case "reroll" -> {
                    if (!ItemHuntV3.itemHuntManager.isActive()) return inactive(sender);
                    ItemHuntV3.itemHuntManager.reroll((Player) sender);
                }
                case "shuffle" -> {
                    if (!ItemHuntV3.itemHuntManager.isActive()) return inactive(sender);
                    ItemHuntV3.itemHuntManager.shuffle();
                }
                case "extend" -> {
                    if (args.length != 2) return invalid(sender);
                    if (!ItemHuntV3.itemHuntManager.isActive()) return inactive(sender);
                    try {
                        ItemHuntV3.itemHuntManager.extend(Integer.parseInt(args[1]));
                    } catch (Exception exception) {
                        return invalid(sender);
                    }
                }
                case "skip" -> {
                    if (!ItemHuntV3.itemHuntManager.isActive()) return inactive(sender);
                    if (!ItemHuntV3.itemHuntManager.skip((Player) sender))
                        sender.sendMessage(ChatColor.RED + "Cannot skip!");
                }
                case "setskipcount" -> {
                    if (args.length != 2) return invalid(sender);
                    if (ItemHuntV3.itemHuntManager.isActive()) return alreadyActive(sender);
                    try {
                        ItemHuntV3.itemHuntManager.setSkipCount(Integer.parseInt(args[1]));
                    } catch (Exception exception) {
                        return invalid(sender);
                    }
                }
                default -> {
                    return invalid(sender);
                }
            }
        } else {
            switch (args[0].toLowerCase()) {
                case "join" -> {
                    if (!ItemHuntV3.itemHuntManager.isActive()) return inactive(sender);
                    ItemHuntV3.itemHuntManager.join((Player) sender);
                }
                case "skip" -> {
                    if (!ItemHuntV3.itemHuntManager.isActive()) return inactive(sender);
                    if (!ItemHuntV3.itemHuntManager.skip((Player) sender))
                        sender.sendMessage(ChatColor.RED + "Cannot skip!");
                }
                default -> {
                    return invalid(sender);
                }
            }
        }

        return true;
    }

    private boolean invalid(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "Invalid command!");
        if(sender.isOp()) sender.sendMessage(ChatColor.GRAY + "Usage: /itemhunt <start/stop/clear/skip/join/reroll/shuffle/extend/setskipcount>");
        else sender.sendMessage(ChatColor.GRAY + "Usage: /itemhunt <join/skip>");
        return true;
    }

    private boolean alreadyActive(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "ItemHunt is already active!");
        return true;
    }

    private boolean inactive(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "ItemHunt is not currently active!");
        return true;
    }
}
