package org.simiancage.DeathTpPlus.commands;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.models.DeathRecord;
import org.simiancage.DeathTpPlus.models.DeathRecord.DeathRecordType;

public class DeathsCommand implements Command
{

    public Boolean execute(CommandSender sender, String[] args)
    {
        boolean canUseCommand = false;
        String playername = "";
        String cause = "";
        int totalnum = 0;
        boolean foundrecord = false;

        if (sender instanceof Player) {
            Player player = (Player) sender;

            canUseCommand = DeathTpPlus.permission.playerHas(player, "deathtpplus.deaths");
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
                cause = args[1];
            }
            else {
                return false;
            }

            List<DeathRecord> records = DeathTpPlus.deathLog.getRecords(playername);
            for (DeathRecord record : records) {
                if (!cause.isEmpty()) {
                    if (record.getPlayerName().equalsIgnoreCase(playername) && record.getType().equals(DeathRecordType.death) && record.getEventName().equalsIgnoreCase(cause)) {
                        String times = record.getCount() == 1 ? "time" : "times";
                        sender.sendMessage(playername + " has died by " + cause + " " + record.getCount() + " " + times);
                        foundrecord = true;
                    }
                }
                // total count
                else {
                    if (record.getPlayerName().equalsIgnoreCase(playername) && record.getType().equals(DeathRecordType.death)) {
                        totalnum = totalnum + record.getCount();
                    }
                }
            }

            if (cause.isEmpty()) {
                String times = "times";
                if (totalnum == 1) {
                    times = "time";
                }
                sender.sendMessage(playername + " has died " + totalnum + " " + times);
            }
            else {
                if (!foundrecord) {
                    sender.sendMessage(playername + " has died by " + cause + " 0 times");
                }
            }
            return true;
        }

        else {
            return true;
        }
    }
}
