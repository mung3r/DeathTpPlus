package org.simiancage.DeathTpPlus.logs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.simiancage.DeathTpPlus.DeathTpPlus;

public class DTPDeathLog
{
    private DeathTpPlus plugin;
    private File file;
    private static final String DEATH_LOG_FILE = "deathlog.txt";

    public DTPDeathLog(DeathTpPlus plugin)
    {
        this.plugin = plugin;
        file = new File(DeathTpPlus.dataFolder, DEATH_LOG_FILE);
        if (!file.exists()) {
            try {
                file.createNewFile();
            }
            catch (IOException e) {
                DeathTpPlus.logger.severe("Failed to create " + file.getName());
            }
        }
    }

    public List<String> getRecords(String playerName)
    {
        List<String> records = new ArrayList<String>();

        try {
            String record;
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            while ((record = bufferedReader.readLine()) != null) {
                if (record.split(":")[0].equalsIgnoreCase(playerName)) {
                    records.add(record);
                }
            }

            bufferedReader.close();
        }
        catch (Exception e) {
            DeathTpPlus.logger.severe("Could not read " + file);
        }

        return records;
    }

    public void setRecord(String playername, String type, String deathtype)
    {
        File deathlogTempFile = new File(DeathTpPlus.dataFolder, "deathtlog.tmp");
        String line = "";
        String[] splittext;
        String writeline = "";
        int newrecord = 0;
        boolean foundrecord = false;

        if (!deathlogTempFile.exists()) {
            try {
                deathlogTempFile.createNewFile();
            }
            catch (IOException e) {
                DeathTpPlus.logger.severe("cannot create file " + deathlogTempFile);
            }
        }

        try {
            // format name:type:mob/player:number
            PrintWriter pw = new PrintWriter(new FileWriter(deathlogTempFile));
            BufferedReader br = new BufferedReader(new FileReader(file));

            while ((line = br.readLine()) != null) {
                splittext = line.split(":");
                writeline = line;
                if (splittext[0].matches(playername)) {
                    if (splittext[1].matches(type)) {
                        if (splittext[2].matches(deathtype)) {
                            newrecord = Integer.parseInt(splittext[3]);
                            newrecord++;
                            writeline = playername + ":" + type + ":" + deathtype + ":" + newrecord;
                            foundrecord = true;
                        }
                    }
                }

                pw.println(writeline);
                pw.flush();
            }

            if (!foundrecord) {
                writeline = playername + ":" + type + ":" + deathtype + ":1";
                pw.println(writeline);
                pw.flush();
            }

            pw.close();
            br.close();

            file.delete();
            deathlogTempFile.renameTo(file);
        }
        catch (IOException e) {
            DeathTpPlus.logger.severe("Could not edit deathlog: " + e);
        }
    }
}
