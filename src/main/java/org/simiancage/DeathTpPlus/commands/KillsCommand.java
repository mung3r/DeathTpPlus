package org.simiancage.DeathTpPlus.commands;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.simiancage.DeathTpPlus.DeathTpPlus;

public class KillsCommand implements Command
{

    public Boolean execute(CommandSender sender, String[] args)
    {
        boolean canUseCommand = false;
        String playername = "";
        String username = "";
        int totalnum = 0;
        String[] splittext;
        boolean foundrecord = false;

        if (sender instanceof Player) {
            Player player = (Player) sender;

            canUseCommand = DeathTpPlus.permission.playerHas(player, "deathtpplus.kills");
        }

        if (canUseCommand) {
            if (args.length == 0) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    playername = player.getName();
                }
                else {
                    return false;
                }
            }
            else if (args.length == 1) {
                playername = args[0];
            }
            else if (args.length == 2) {
                playername = args[0];
                username = args[1];
            }
            else {
                return false;
            }
            // File deathlogFile = new
            // File(getDataFolder()+"/deathlog.txt");
            List<String> lines = DeathTpPlus.deathLog.getRecords(playername);
            for (String line : lines) {
                splittext = line.split(":");
                // 0 = name, 1 = type, 2 = cause, 3 = number
                if (!username.isEmpty()) {
                    if (splittext[0].equalsIgnoreCase(playername) && splittext[1].equals("kill") && splittext[2].equalsIgnoreCase(username)) {
                        String times = "times";
                        if (splittext[2] == "1")
                            times = "time";
                        sender.sendMessage(playername + " has killed " + username + " " + splittext[3] + " " + times);
                        foundrecord = true;
                    }
                }
                // total count
                else {
                    if (splittext[0].equalsIgnoreCase(playername) && splittext[1].equals("kill")) {
                        totalnum = totalnum + Integer.parseInt(splittext[3]);
                    }
                }
            }
            if (username.isEmpty()) {
                String times = "times";
                if (totalnum == 1) {
                    times = "time";
                }
                sender.sendMessage(playername + " has killed " + totalnum + " " + times);
            }
            else {
                if (!foundrecord) {
                    sender.sendMessage(playername + " has killed " + username + " 0 times");
                }
            }
            return true;
        }
        else {
            return true;
        }
    }
}
