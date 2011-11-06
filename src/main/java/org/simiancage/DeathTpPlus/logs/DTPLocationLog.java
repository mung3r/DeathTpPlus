package org.simiancage.DeathTpPlus.logs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.simiancage.DeathTpPlus.DeathTpPlus;

public class DTPLocationLog
{
    private DeathTpPlus plugin;
    private File file;
    private static final String LOCATION_LOG_FILE = "locs.txt";

    public DTPLocationLog(DeathTpPlus plugin)
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

    public void setRecord(Player player)
    {
        // text to write to file
        String line = "";
        ArrayList<String> filetext = new ArrayList<String>();
        boolean readCheck = false;
        boolean newPlayerDeath = true;
        String fileOutput = player.getName() + ":" + player.getLocation().getX() + ":" + player.getLocation().getY() + ":" + player.getLocation().getZ() + ":" + player.getWorld().getName().toString();
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);

            while ((line = br.readLine()) != null) {
                if (line.contains(player.getName() + ":")) {
                    line = fileOutput;
                    newPlayerDeath = false;
                }
                filetext.add(line);
                readCheck = true;
            }

            br.close();

            BufferedWriter out = new BufferedWriter(new FileWriter(file));

            for (int i = 0; i < filetext.size(); i++) {
                out.write(filetext.get(i));
                out.newLine();
            }

            if (!readCheck) {
                out.write(fileOutput);
                out.newLine();
            }

            if (newPlayerDeath && readCheck) {
                out.write(fileOutput);
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
