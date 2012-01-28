package org.simiancage.DeathTpPlus.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.models.StreakRecord;
import org.simiancage.DeathTpPlus.utils.DTPConfig;
import org.simiancage.DeathTpPlus.utils.DTPConfig.ConfigFlagType;

public class StreakCommand extends BasicCommand
{
    public StreakCommand(DeathTpPlus plugin)
    {
        super("Streak");
        setDescription("Display player streak counts");
        setUsage("/dtp streak §9[player]");
        setArgumentRange(0, 1);
        setIdentifiers("streak");
        setPermission("deathtpplus.streak");
    }

    @Override
    public boolean execute(CommandSender sender, String identifier, String[] args)
    {
        if (DTPConfig.configFlags.get(ConfigFlagType.SHOW_STREAKS)) {

            if (args.length < 1 && !(sender instanceof Player)) {
                sender.sendMessage("Console cannot display streaks for themselves!");
                return true;
            }

            StreakRecord streak = DeathTpPlus.streakLog.getRecord(args.length > 0 ? args[0] : ((Player) sender).getName());

            if (streak != null) {
                if (streak.getCount() < 0) {
                    if (args.length > 0) {
                        sender.sendMessage(String.format("%s is on a %d death streak.", args[0], streak.getCount() * -1));
                    }
                    else {
                        sender.sendMessage(String.format("You are on a %d death streak.", streak.getCount() * -1));
                    }
                }
                else {
                    if (args.length > 0) {
                        sender.sendMessage(String.format("%s is on a %d kill streak.", args[0], streak.getCount()));
                    }
                    else {
                        sender.sendMessage(String.format("You are on a %d kill streak.", streak.getCount()));
                    }
                }
            }
            else {
                sender.sendMessage("No record found.");
            }
        }

        return true;
    }
}
