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
import org.simiancage.DeathTpPlus.models.DeathDetail;
import org.simiancage.DeathTpPlus.models.StreakRecord;
import org.simiancage.DeathTpPlus.utils.DTPConfig;
import org.simiancage.DeathTpPlus.utils.DTPUtils;

public class DTPStreakLog
{
    private DeathTpPlus plugin;
    private File file;
    private static final String STREAK_LOG_FILE = "streak.txt";

    public DTPStreakLog(DeathTpPlus plugin)
    {
        this.plugin = plugin;
        file = new File(DeathTpPlus.dataFolder, STREAK_LOG_FILE);
        if (!file.exists()) {
            try {
                file.createNewFile();
            }
            catch (IOException e) {
                DeathTpPlus.logger.severe("Failed to create " + file.getName());
            }
        }
    }

    public StreakRecord getRecord(String playerName)
    {
        StreakRecord streak = null;

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

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
            DeathTpPlus.logger.severe("Could not read " + file);
        }

        return streak;
    }

    public void setRecord(DeathDetail deathDetail)
    {
        if (deathDetail.getKiller() == null) {
            return;
        }

        setRecord(deathDetail.getKiller().getName(), deathDetail.getPlayer().getName());
    }

    @Deprecated
    public void setRecord(String attacker, String defender)
    {

        // read the file
        List<StreakRecord> streakList = new ArrayList<StreakRecord>();

        int atkCurrentStreak = 0;
        int defCurrentStreak = 0;
        boolean foundDefender = false;
        boolean foundAttacker = false;

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            String line = null;
            while ((line = br.readLine()) != null) {
                StreakRecord streak = new StreakRecord(line);
                if (defender.equalsIgnoreCase(streak.getPlayerName())) {
                    if (streak.getCount() > 0) {
                        streak.setCount(0);
                    }
                    streak.setCount(streak.getCount() - 1);
                    defCurrentStreak = streak.getCount();
                    foundDefender = true;
                }
                if (attacker.equalsIgnoreCase(streak.getPlayerName())) {
                    if (streak.getCount() < 0) {
                        streak.setCount(0);
                    }
                    streak.setCount(streak.getCount() + 1);
                    atkCurrentStreak = streak.getCount();
                    foundAttacker = true;
                }
                streakList.add(streak);
            }

            br.close();
        }
        catch (IOException e) {
            DeathTpPlus.logger.severe(e.toString());
        }

        if (!foundAttacker) {
            StreakRecord streak = new StreakRecord();
            streak.setPlayerName(attacker);
            streak.setCount(1);
            streakList.add(streak);
        }

        if (!foundDefender) {
            StreakRecord streak = new StreakRecord();
            streak.setPlayerName(defender);
            streak.setCount(-1);
            streakList.add(streak);
        }

        // Check to see if we should announce a streak
        if (DTPConfig.configFlags.get(DTPConfig.ConfigFlagType.SHOW_STREAKS)) {
            // Deaths
            String deathStreakMessage = DTPConfig.getDeathStreakMessage(defCurrentStreak);
            if (deathStreakMessage != null) {
                plugin.getServer().broadcastMessage(deathStreakMessage.replace("%n", defender));
            }
            // Kills
            String killStreakMessage = DTPConfig.getKillStreakMessage(atkCurrentStreak);
            if (killStreakMessage != null) {
                plugin.getServer().broadcastMessage(killStreakMessage.replace("%n", attacker));
            }
        }

        // Write streaks to file
        try {
            BufferedWriter logFile = new BufferedWriter(new FileWriter(file));

            for (StreakRecord streak : streakList) {
                logFile.write(streak.toString());
                logFile.newLine();
            }

            logFile.close();
        }
        catch (IOException e) {
            DeathTpPlus.logger.severe(e.toString());
        }
    }
}
