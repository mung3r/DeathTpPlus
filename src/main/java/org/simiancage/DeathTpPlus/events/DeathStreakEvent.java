package org.simiancage.DeathTpPlus.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class DeathStreakEvent extends Event
{
    private Player player;
    private String message;
    private Integer deaths;

    public DeathStreakEvent(Player player, String message, Integer deaths)
    {
        super("DeathStreakEvent");

        this.player = player;
        this.message = message;
        this.deaths = deaths;
    }

    public Player getPlayer()
    {
        return player;
    }

    public void setPlayer(Player player)
    {
        this.player = player;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public Integer getDeaths()
    {
        return deaths;
    }

    public void setDeaths(Integer deaths)
    {
        this.deaths = deaths;
    }

}
