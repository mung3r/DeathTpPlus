package org.simiancage.DeathTpPlus.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.models.DeathRecord;
import org.simiancage.DeathTpPlus.models.DeathRecord.DeathRecordType;

public class DeathsCommand implements Command
{
    public Boolean execute(CommandSender sender, String[] args)
    {
        int total;

        if (args.length > 2)
            return false;

        switch (args.length) {
            case 0:
                Player player = (Player) sender;
                total = DeathTpPlus.deathLog.getTotalByType(player.getName(), DeathRecordType.death);
                if (total > -1) {
                    sender.sendMessage(String.format("You died %d time(s)", total));
                }
                else {
                    sender.sendMessage("No record found.");
                }
                break;
            case 1:
                total = DeathTpPlus.deathLog.getTotalByType(args[0], DeathRecordType.death);
                if (total > -1) {
                    sender.sendMessage(String.format("%s died %d time(s)", args[0], total));
                }
                else {
                    sender.sendMessage("No record found.");
                }
                break;
            case 2:
                DeathRecord record = DeathTpPlus.deathLog.getRecordByType(args[0], args[1], DeathRecordType.death);
                if (record != null) {
                    sender.sendMessage(String.format("%s died by %s %d time(s)", args[0], args[1], record.getCount()));
                }
                else {
                    sender.sendMessage("No record found.");
                }
                break;
        }

        return true;
    }

    public String getPermission()
    {
        return "deathtpplus.deaths";
    }
}
