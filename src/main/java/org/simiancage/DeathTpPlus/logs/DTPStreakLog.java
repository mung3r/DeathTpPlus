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
import org.simiancage.DeathTpPlus.models.Streak;
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

    public Streak getRecord(String playerName)
    {
        Streak streak = null;

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                streak = new Streak(line);
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
        setRecord(deathDetail.getKiller().getName(), deathDetail.getPlayer().getName());
    }

    @Deprecated
    public void setRecord(String attacker, String defender)
    {

        // read the file
        List<Streak> streakList = new ArrayList<Streak>();

        int atkCurrentStreak = 0;
        int defCurrentStreak = 0;
        boolean foundDefender = false;
        boolean foundAttacker = false;

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            String line = null;
            while ((line = br.readLine()) != null) {
                Streak streak = new Streak(line);
                if (defender.equalsIgnoreCase(streak.getPlayerName())) {
                    defCurrentStreak = streak.getCount();
                    if (streak.getCount() > 0) {
                        streak.setCount(0);
                    }
                    streak.setCount(streak.getCount() - 1);
                    foundDefender = true;
                }
                if (attacker.equalsIgnoreCase(streak.getPlayerName())) {
                    if (streak.getCount() < 0) {
                        streak.setCount(0);
                    }
                    streak.setCount(streak.getCount() + 1);
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
            Streak streak = new Streak();
            streak.setPlayerName(attacker);
            streak.setCount(1);
            streakList.add(streak);
        }

        if (!foundDefender) {
            Streak streak = new Streak();
            streak.setPlayerName(defender);
            streak.setCount(-1);
            streakList.add(streak);
        }

        // Check to see if we should announce a streak
        if (DTPConfig.configFlags.get(DTPConfig.ConfigFlagType.SHOW_STREAKS)) {
            // Deaths
            for (int i = 0; i < DTPConfig.deathStreakMessages.size(); i++) {
                String testsplit[] = DTPConfig.deathStreakMessages.get(i).split(":");
                if (Integer.parseInt(testsplit[0]) == -(defCurrentStreak)) {
                    String announce = DTPUtils.convertColorCodes(testsplit[1]);
                    plugin.getServer().broadcastMessage(announce.replace("%n", defender));
                }
            }
            // Kills
            for (int i = 0; i < DTPConfig.killStreakMessages.size(); i++) {
                String testsplit[] = DTPConfig.killStreakMessages.get(i).split(":");
                if (Integer.parseInt(testsplit[0]) == atkCurrentStreak) {
                    String announce = DTPUtils.convertColorCodes(testsplit[1]);
                    plugin.getServer().broadcastMessage(announce.replace("%n", attacker));
                }
            }
        }

        // Write streaks to file
        try {
            BufferedWriter logFile = new BufferedWriter(new FileWriter(file));

            for (Streak streak : streakList) {
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
