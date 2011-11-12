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
import org.simiancage.DeathTpPlus.models.DeathLocationRecord;

public class DTPDeathLocationLog
{
    private static final String LOCATION_LOG_FILE = "locs.txt";

    private File deathLocationLogFile;

    public DTPDeathLocationLog()
    {
        deathLocationLogFile = new File(DeathTpPlus.dataFolder, LOCATION_LOG_FILE);
        if (!deathLocationLogFile.exists()) {
            try {
                deathLocationLogFile.createNewFile();
            }
            catch (IOException e) {
                DeathTpPlus.logger.severe("Failed to create death location log: " + e.toString());
            }
        }
    }

    public DeathLocationRecord getRecord(String playerName)
    {
        DeathLocationRecord deathLocation = null;

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(deathLocationLogFile));
            String line = null;

            while ((line = bufferedReader.readLine()) != null) {
                deathLocation = new DeathLocationRecord(line);
                if (playerName.equalsIgnoreCase(deathLocation.getPlayerName())) {
                    return deathLocation;
                }
            }

            bufferedReader.close();
        }
        catch (IOException e) {
            DeathTpPlus.logger.severe("Failed to read death location log: " + e.toString());
        }

        return deathLocation;
    }

    public void setRecord(DeathDetail deathDetail)
    {
        List<DeathLocationRecord> deathLocations = new ArrayList<DeathLocationRecord>();
        DeathLocationRecord playerRecord = null;

        try {
            BufferedReader deathLocationLogReader = new BufferedReader(new FileReader(deathLocationLogFile));

            String line = null;
            while ((line = deathLocationLogReader.readLine()) != null) {
                DeathLocationRecord deathLocation = new DeathLocationRecord(line);
                if (deathDetail.getPlayer().getName().equalsIgnoreCase(deathLocation.getPlayerName())) {
                    deathLocation.setLocation(deathDetail.getPlayer().getLocation());
                    deathLocation.setWorldName(deathDetail.getPlayer().getWorld().getName());
                    playerRecord = deathLocation;
                }
                deathLocations.add(deathLocation);
            }

            deathLocationLogReader.close();
        }
        catch (IOException e) {
            DeathTpPlus.logger.severe("Failed to read death location log: " + e.toString());
        }

        if (playerRecord == null) {
            playerRecord = new DeathLocationRecord(deathDetail.getPlayer());
            deathLocations.add(playerRecord);
        }

        try {
            BufferedWriter deathLocationLogWriter = new BufferedWriter(new FileWriter(deathLocationLogFile));

            for (DeathLocationRecord deathLocation : deathLocations) {
                deathLocationLogWriter.write(deathLocation.toString());
                deathLocationLogWriter.newLine();
            }

            deathLocationLogWriter.close();
        }
        catch (IOException e) {
            DeathTpPlus.logger.severe("Failed to write death location log: " + e.toString());
        }
    }
}
