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
import org.simiancage.DeathTpPlus.models.DeathDetail;
import org.simiancage.DeathTpPlus.models.DeathRecord;
import org.simiancage.DeathTpPlus.models.DeathRecord.DeathRecordType;

public class DTPDeathLog
{
    private static final String DEATH_LOG_FILE = "deathlog.txt";
    private static final String DEATH_LOG_TMP = "deathlog.tmp";

    private File file;

    public DTPDeathLog()
    {
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

    public List<DeathRecord> getRecords(String playerName)
    {
        List<DeathRecord> records = new ArrayList<DeathRecord>();

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                DeathRecord deathRecord = new DeathRecord(line);
                if (playerName.equalsIgnoreCase(deathRecord.getPlayerName())) {
                    records.add(deathRecord);
                }
            }

            bufferedReader.close();
        }
        catch (Exception e) {
            DeathTpPlus.logger.severe("Could not read " + file);
        }

        return records;
    }

    public void setRecord(DeathDetail deathDetail)
    {
        if (deathDetail.isPVPDeath()) {
            setRecord(deathDetail.getKiller().getName(), DeathRecordType.kill, deathDetail.getPlayer().getName());
            setRecord(deathDetail.getPlayer().getName(), DeathRecordType.death, deathDetail.getKiller().getName());
        }
        else {
            setRecord(deathDetail.getPlayer().getDisplayName(), DeathRecordType.death, deathDetail.getCauseOfDeath().toString());
        }
    }

    @Deprecated
    public void setRecord(String playername, DeathRecordType type, String eventName)
    {
        File deathlogTempFile = new File(DeathTpPlus.dataFolder, DEATH_LOG_TMP);
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
            PrintWriter pw = new PrintWriter(new FileWriter(deathlogTempFile));
            BufferedReader br = new BufferedReader(new FileReader(file));

            String line = null;
            while ((line = br.readLine()) != null) {
                DeathRecord deathRecord = new DeathRecord(line);
                if (playername.equalsIgnoreCase(deathRecord.getPlayerName()) && type.equals(deathRecord.getType()) && eventName.equalsIgnoreCase(deathRecord.getEventName())) {
                    deathRecord.setCount(deathRecord.getCount() + 1);
                    foundrecord = true;
                }

                pw.println(deathRecord.toString());
                pw.flush();
            }

            if (!foundrecord) {
                DeathRecord deathRecord = new DeathRecord();
                deathRecord.setPlayerName(playername);
                deathRecord.setType(type);
                deathRecord.setEventName(eventName);
                deathRecord.setCount(1);

                pw.println(deathRecord.toString());
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
