package org.simiancage.DeathTpPlus.logs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.events.DeathStreakEvent;
import org.simiancage.DeathTpPlus.events.KillStreakEvent;
import org.simiancage.DeathTpPlus.models.DeathDetail;
import org.simiancage.DeathTpPlus.models.StreakRecord;
import org.simiancage.DeathTpPlus.utils.DTPConfig;

public class DTPStreakLog
{
    private static final String STREAK_LOG_FILE = "streak.txt";

    private DeathTpPlus plugin;
    private File streakLogFile;

    public DTPStreakLog(DeathTpPlus plugin)
    {
        this.plugin = plugin;
        streakLogFile = new File(DeathTpPlus.dataFolder, STREAK_LOG_FILE);
        if (!streakLogFile.exists()) {
            try {
                streakLogFile.createNewFile();
            }
            catch (IOException e) {
                DeathTpPlus.logger.severe("Failed to create streak log: " + e.toString());
            }
        }
    }

    public StreakRecord getRecord(String playerName)
    {
        StreakRecord streak = null;

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(streakLogFile));

            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                streak = new StreakRecord(line);
                if (playerName.equalsIgnoreCase(streak.getPlayerName())) {
                    return streak;
                }
            }

            bufferedReader.close();
        }
        catch (Exception e) {
            DeathTpPlus.logger.severe("Failed to read streak log: " + e.toString());
        }

        return streak;
    }

    public void setRecord(DeathDetail deathDetail)
    {
        if (deathDetail.getKiller() == null) {
            return;
        }

        String killerName = deathDetail.getKiller().getName();
        String victimName = deathDetail.getPlayer().getName();

        List<StreakRecord> streakList = new ArrayList<StreakRecord>();

        StreakRecord killStreakRecord = null;
        StreakRecord deathStreakRecord = null;

        // read the file
        try {
            BufferedReader streakLogReader = new BufferedReader(new FileReader(streakLogFile));

            String line = null;
            while ((line = streakLogReader.readLine()) != null) {
                StreakRecord streak = new StreakRecord(line);
                if (victimName.equalsIgnoreCase(streak.getPlayerName())) {
                    if (streak.getCount() > 0) {
                        streak.setCount(0);
                    }
                    streak.setCount(streak.getCount() - 1);
                    deathStreakRecord = streak;
                }
                if (killerName.equalsIgnoreCase(streak.getPlayerName())) {
                    if (streak.getCount() < 0) {
                        streak.setCount(0);
                    }
                    streak.setCount(streak.getCount() + 1);
                    killStreakRecord = streak;
                }
                streakList.add(streak);
            }

            streakLogReader.close();
        }
        catch (IOException e) {
            DeathTpPlus.logger.severe("Failed to read streak log: " + e.toString());
        }

        if (killStreakRecord == null) {
            killStreakRecord = new StreakRecord(killerName, 1);
            streakList.add(killStreakRecord);
        }

        if (deathStreakRecord == null) {
            deathStreakRecord = new StreakRecord(victimName, -1);
            streakList.add(deathStreakRecord);
        }

        // Check to see if we should announce a streak
        if (DTPConfig.configFlags.get(DTPConfig.ConfigFlagType.SHOW_STREAKS)) {
            // Deaths
            String deathStreakMessage = DTPConfig.getDeathStreakMessage(deathStreakRecord.getCount());
            if (deathStreakMessage != null) {
                plugin.getServer().getPluginManager().callEvent(new DeathStreakEvent(deathDetail.getPlayer(), deathStreakMessage, deathStreakRecord.getCount()));
            }
            // Kills
            String killStreakMessage = DTPConfig.getKillStreakMessage(killStreakRecord.getCount());
            if (killStreakMessage != null) {
                plugin.getServer().getPluginManager().callEvent(new KillStreakEvent(deathDetail.getKiller(), killStreakMessage, killStreakRecord.getCount()));
            }
        }

        // Write streaks to file
        try {
            BufferedWriter streakLogWriter = new BufferedWriter(new FileWriter(streakLogFile));

            for (StreakRecord streak : streakList) {
                streakLogWriter.write(streak.toString());
                streakLogWriter.newLine();
            }

            streakLogWriter.close();
        }
        catch (IOException e) {
            DeathTpPlus.logger.severe("Failed to write streak log: " + e.toString());
        }
    }
}
