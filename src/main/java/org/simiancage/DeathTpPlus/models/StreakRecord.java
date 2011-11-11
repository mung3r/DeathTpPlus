package org.simiancage.DeathTpPlus.models;

public class StreakRecord
{
    private String playerName;
    private int count;

    public StreakRecord()
    {
    }

    public StreakRecord(String record)
    {
        if (record != null) {
            String[] parts = record.split(":");

            if (parts.length == 2) {
                playerName = parts[0];
                count = Integer.valueOf(parts[1]);
            }
        }
    }

    public String getPlayerName()
    {
        return playerName;
    }

    public void setPlayerName(String playerName)
    {
        this.playerName = playerName;
    }

    public int getCount()
    {
        return count;
    }

    public void setCount(int count)
    {
        this.count = count;
    }

    @Override
    public String toString()
    {
        return String.format("%s:%d", playerName, count);
    }
}
