package org.simiancage.DeathTpPlus.models;

import org.bukkit.Location;

public class DeathLocation
{
    private String playerName;
    private Location location;
    private String worldName;

    public DeathLocation()
    {
    }

    public DeathLocation(String record)
    {
        if (record != null) {
            String[] parts = record.split(":");

            if (parts.length == 5) {
                playerName = parts[0];
                location = new Location(null, Double.valueOf(parts[1]), Double.valueOf(parts[2]), Double.valueOf(parts[3]));
                worldName = parts[4];
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

    public Location getLocation()
    {
        return location;
    }

    public void setLocation(Location location)
    {
        this.location = location;
    }

    public String getWorldName()
    {
        return worldName;
    }

    public void setWorldName(String worldName)
    {
        this.worldName = worldName;
    }

    @Override
    public String toString()
    {
        return String.format("%s:%f:%f:%f:%s", playerName, location.getX(), location.getY(), location.getZ(), worldName);
    }
}
