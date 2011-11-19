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
import org.simiancage.DeathTpPlus.models.DeathRecord;
import org.simiancage.DeathTpPlus.models.DeathRecord.DeathRecordType;
import org.simiancage.DeathTpPlus.utils.DTPConfig.DeathEventType;

public class DTPDeathLog
{
    private static final String DEATH_LOG_FILE = "deathlog.txt";
    private static final String DEATH_LOG_TMP = "deathlog.tmp";

    private File deathLogFile;

    public DTPDeathLog()
    {
        deathLogFile = new File(DeathTpPlus.dataFolder, DEATH_LOG_FILE);
        if (!deathLogFile.exists()) {
            try {
                deathLogFile.createNewFile();
            }
            catch (IOException e) {
                DeathTpPlus.logger.severe("Failed to create death log: " + e.toString());
            }
        }
    }

    public int getTotalByType(String playerName, DeathRecordType type)
    {
        List<DeathRecord> records = getRecords(playerName);
        int totalDeaths = -1;

        for (DeathRecord record : records) {
            if (record.getPlayerName().equalsIgnoreCase(playerName) && record.getType() == type) {
                totalDeaths += record.getCount();
            }
        }

        return totalDeaths;
    }

    public DeathRecord getRecordByType(String playerName, String eventName, DeathRecordType type)
    {
        List<DeathRecord> records = getRecords(playerName);

        for (DeathRecord record : records) {
            if (record.getPlayerName().equalsIgnoreCase(playerName) && record.getType() == type && record.getEventName().equalsIgnoreCase(eventName)) {
                return record;
            }
        }

        return null;
    }

    public List<DeathRecord> getRecords(String playerName)
    {
        List<DeathRecord> records = new ArrayList<DeathRecord>();

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(deathLogFile));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                DeathRecord deathRecord = new DeathRecord(line);
                if (playerName.equalsIgnoreCase(deathRecord.getPlayerName())) {
                    records.add(deathRecord);
                }
            }

            bufferedReader.close();
        }
        catch (IOException e) {
            DeathTpPlus.logger.severe("Failed to read death log: " + e.toString());
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
            setRecord(deathDetail.getPlayer().getName(), DeathRecordType.death, deathDetail.getCauseOfDeath().toString());
        }
    }

    @Deprecated
    public void setRecord(String playerName, DeathRecordType type, String eventName)
    {
        File tmpDeathLogFile = new File(DeathTpPlus.dataFolder, DEATH_LOG_TMP);
        DeathRecord playerRecord = null;

        if (!tmpDeathLogFile.exists()) {
            try {
                tmpDeathLogFile.createNewFile();
            }
            catch (IOException e) {
                DeathTpPlus.logger.severe("Failed to create tmp death log: " + e.toString());
            }
        }

        try {
            BufferedWriter tmpDeathLogWriter = new BufferedWriter(new FileWriter(tmpDeathLogFile));
            BufferedReader deathLogReader = new BufferedReader(new FileReader(deathLogFile));

            String line = null;
            while ((line = deathLogReader.readLine()) != null) {
                DeathRecord deathRecord = new DeathRecord(line);
                if (playerName.equalsIgnoreCase(deathRecord.getPlayerName()) && type == deathRecord.getType() && eventName.equalsIgnoreCase(deathRecord.getEventName())) {
                    deathRecord.setCount(deathRecord.getCount() + 1);
                    playerRecord = deathRecord;
                }

                tmpDeathLogWriter.write(deathRecord.toString());
                tmpDeathLogWriter.newLine();
            }

            if (playerRecord == null) {
                playerRecord = new DeathRecord(playerName, type, eventName, 1);
                tmpDeathLogWriter.write(playerRecord.toString());
                tmpDeathLogWriter.newLine();
            }

            tmpDeathLogWriter.close();
            deathLogReader.close();

            deathLogFile.delete();
            tmpDeathLogFile.renameTo(deathLogFile);
        }
        catch (IOException e) {
            DeathTpPlus.logger.severe("Failed to edit death log: " + e.toString());
        }
    }
}
