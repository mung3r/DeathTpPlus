package org.simiancage.DeathTpPlus.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.utils.DTPConfig;
import org.simiancage.DeathTpPlus.utils.DTPConfig.ConfigType;

public class StreakCommand implements Command
{

    public Boolean execute(CommandSender sender, String[] args)
    {
        boolean canUseCommand = false;

        if (sender instanceof Player) {
            Player player = (Player) sender;
            canUseCommand = DeathTpPlus.permission.playerHas(player, "deathtpplus.streak");
        }

        if (canUseCommand) {
            if (DTPConfig.config.get(ConfigType.SHOW_STREAKS).equals("true")) {
                // File streakFile = new
                // File("plugins/DeathTpPlus/streak.txt");
                String line;
                String[] splittext;
                Player check;
                String playername = "player";

                if (args.length > 0) {
                    playername = args[0];
                }
                else {
                    if (sender instanceof Player) {
                        check = (Player) sender;
                        playername = check.getName();
                    }
                }

                boolean entryfound = false;
                line = DeathTpPlus.streakLog.getRecord(playername);
                if (line != null) {
                    if (!line.startsWith("#")) {
                        splittext = line.split(":");
                        if (playername.equalsIgnoreCase(splittext[0])) {
                            if (Integer.parseInt(splittext[1]) < 0) {
                                sender.sendMessage(ChatColor.GRAY + splittext[0] + "'s Current Streak: " + splittext[1].replace("-", "") + " Death(s)");
                            }
                            else {
                                sender.sendMessage(ChatColor.GRAY + splittext[0] + "'s Current Streak: " + splittext[1] + " Kill(s)");
                            }

                            entryfound = true;
                        }
                    }
                }
                if (!entryfound) {
                    sender.sendMessage("No streak found");
                }
                return true;
            }
            else {
                return true;
            }
        }
        else {
            return true;
        }
    }
}
