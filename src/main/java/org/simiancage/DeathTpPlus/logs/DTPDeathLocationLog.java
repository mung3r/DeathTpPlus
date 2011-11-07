package org.simiancage.DeathTpPlus.logs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.models.DeathLocation;

public class DTPDeathLocationLog
{
    private DeathTpPlus plugin;
    private File file;
    private static final String LOCATION_LOG_FILE = "locs.txt";

    public DTPDeathLocationLog(DeathTpPlus plugin)
    {
        this.plugin = plugin;
        file = new File(DeathTpPlus.dataFolder, LOCATION_LOG_FILE);
        if (!file.exists()) {
            try {
                file.createNewFile();
            }
            catch (IOException e) {
                DeathTpPlus.logger.severe("Failed to create " + file.getName());
            }
        }
    }

    public DeathLocation getRecord(String playerName)
    {
        DeathLocation deathLocation = null;

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line = null;

            while ((line = bufferedReader.readLine()) != null) {
                deathLocation = new DeathLocation(line);
                if (playerName.equalsIgnoreCase(deathLocation.getPlayerName())) {
                    return deathLocation;
                }
            }

            bufferedReader.close();
        }
        catch (Exception e) {
            DeathTpPlus.logger.severe("Could not read " + file);
        }

        return deathLocation;
    }

    public void setRecord(Player player)
    {
        List<DeathLocation> deathLocations = new ArrayList<DeathLocation>();
        boolean newPlayerDeath = true;
        DeathLocation newLocation = new DeathLocation();
        newLocation.setPlayerName(player.getName());
        newLocation.setLocation(player.getLocation());
        newLocation.setWorldName(player.getWorld().getName());

        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);

            String line = null;
            while ((line = br.readLine()) != null) {
                DeathLocation deathLocation = new DeathLocation(line);
                if (player.getName().equalsIgnoreCase(deathLocation.getPlayerName())) {
                    deathLocations.add(newLocation);
                    newPlayerDeath = false;
                }
                else {
                    deathLocations.add(deathLocation);
                }
            }

            if (newPlayerDeath) {
                deathLocations.add(newLocation);
            }

            br.close();

            BufferedWriter out = new BufferedWriter(new FileWriter(file));

            for (DeathLocation deathLocation : deathLocations) {
                out.write(deathLocation.toString());
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
