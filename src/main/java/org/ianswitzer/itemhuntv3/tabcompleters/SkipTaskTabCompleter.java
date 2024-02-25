package org.ianswitzer.itemhuntv3.tabcompleters;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class SkipTaskTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        final List<String> completions = new ArrayList<>();
        ArrayList<String> commands = new ArrayList<>();
        if(command.getName().equalsIgnoreCase("skip")) {
            if(args.length == 1 && sender.isOp()) {
                Bukkit.getOnlinePlayers().forEach(player -> commands.add(player.getDisplayName()));
                StringUtil.copyPartialMatches(args[0], commands, completions);
            }
        }
        return completions;
    }
}
