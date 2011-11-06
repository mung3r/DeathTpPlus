package org.simiancage.DeathTpPlus.logs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.simiancage.DeathTpPlus.DeathTpPlus;
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

    public String getRecord(String playerName)
    {
        try {
            String record;
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            while ((record = bufferedReader.readLine()) != null) {
                if (record.split(":")[0].equalsIgnoreCase(playerName)) {
                    break;
                }
            }
    
            bufferedReader.close();
            return record;
        }
        catch (Exception e) {
            DeathTpPlus.logger.severe("Could not read " + file);
        }
    
        return null;
    }

    public void setRecord(String attacker, String defender)
    {

        // read the file
        String line = "";
        ArrayList<String> filetext = new ArrayList<String>();

        String[] splittext;
        int atkCurrentStreak = 0;
        int defCurrentStreak = 0;
        boolean foundDefender = false;
        boolean foundAttacker = false;
        boolean isNewFile = true;

        try {
            // File streakFile = new File("plugins/DeathTpPlus/streak.txt");
            // File streakFile = new File(plugin.getDataFolder()+"/streak.txt");
            BufferedReader br = new BufferedReader(new FileReader(file));

            while ((line = br.readLine()) != null) {
                if (line.contains(defender + ":")) {
                    splittext = line.split(":");
                    defCurrentStreak = Integer.parseInt(splittext[1].trim());
                    if (defCurrentStreak > 0) {
                        defCurrentStreak = 0;
                    }
                    defCurrentStreak--;
                    line = defender + ":" + Integer.toString(defCurrentStreak);
                    foundDefender = true;
                }
                if (line.contains(attacker + ":")) {
                    splittext = line.split(":");
                    atkCurrentStreak = Integer.parseInt(splittext[1].trim());
                    if (atkCurrentStreak < 0) {
                        atkCurrentStreak = 0;
                    }
                    atkCurrentStreak++;
                    line = attacker + ":" + Integer.toString(atkCurrentStreak);
                    foundAttacker = true;
                }
                filetext.add(line);
                isNewFile = false;
            }

            br.close();
        }
        catch (IOException e) {
            DeathTpPlus.logger.severe(e.toString());
        }

        String teststreak = "";
        String testsplit[];

        // Check to see if we should announce a streak
        // Deaths
        for (int i = 0; i < DTPConfig.deathStreakMessages.size(); i++) {
            teststreak = DTPConfig.deathStreakMessages.get(i);
            testsplit = teststreak.split(":");
            if (Integer.parseInt(testsplit[0]) == -(defCurrentStreak)) {
                String announce = DTPUtils.convertColorCode(testsplit[1]);
                plugin.getServer().broadcastMessage(announce.replace("%n", defender));
            }
        }
        // Kills
        for (int i = 0; i < DTPConfig.killStreakMessages.size(); i++) {
            teststreak = DTPConfig.killStreakMessages.get(i);
            testsplit = teststreak.split(":");
            if (Integer.parseInt(testsplit[0]) == atkCurrentStreak) {
                String announce = DTPUtils.convertColorCode(testsplit[1]);
                plugin.getServer().broadcastMessage(announce.replace("%n", attacker));
            }
        }

        // Write streaks to file
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(file));

            for (int i = 0; i < filetext.size(); i++) {
                out.write(filetext.get(i));
                out.newLine();
            }

            if (isNewFile) {
                out.write(attacker + ":" + "1");
                out.newLine();
                out.write(defender + ":" + "-1");
                out.newLine();
            }

            if (!foundDefender && !isNewFile) {
                out.write(defender + ":" + "-1");
                out.newLine();
            }

            if (!foundAttacker && !isNewFile) {
                out.write(attacker + ":" + "1");
                out.newLine();
            }
            // Close the output stream
            out.close();
        }
        catch (IOException e) {
            DeathTpPlus.logger.severe(e.toString());
        }
    }
}
