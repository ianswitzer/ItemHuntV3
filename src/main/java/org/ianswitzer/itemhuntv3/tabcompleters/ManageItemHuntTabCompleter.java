package org.ianswitzer.itemhuntv3.tabcompleters;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class ManageItemHuntTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        final List<String> completions = new ArrayList<>();
        ArrayList<String> commands = new ArrayList<>();
        if(command.getName().equalsIgnoreCase("itemhunt")) {
            if(args.length == 1 && sender.isOp()) {
                commands.add("start");
                commands.add("stop");
                commands.add("extend");
                StringUtil.copyPartialMatches(args[0], commands, completions);
            }
        }
        return completions;
    }
}
