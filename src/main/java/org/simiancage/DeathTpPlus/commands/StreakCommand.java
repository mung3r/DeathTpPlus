package org.simiancage.DeathTpPlus.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.models.Streak;
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

        if (canUseCommand && DTPConfig.config.get(ConfigType.SHOW_STREAKS).equals("true")) {
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
            Streak streak = DeathTpPlus.streakLog.getRecord(playername);
            if (streak != null) {
                if (playername.equalsIgnoreCase(streak.getPlayerName())) {
                    if (streak.getCount() < 0) {
                        sender.sendMessage(ChatColor.GRAY + streak.getPlayerName() + "'s Current Streak: " + streak.getCount() * -1 + " Death(s)");
                    }
                    else {
                        sender.sendMessage(ChatColor.GRAY + streak.getPlayerName() + "'s Current Streak: " + streak.getCount() + " Kill(s)");
                    }

                    entryfound = true;
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
}
