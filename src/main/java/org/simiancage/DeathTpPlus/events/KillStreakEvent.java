package org.simiancage.DeathTpPlus.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class KillStreakEvent extends Event
{
    private Player player;
    private String message;

    public KillStreakEvent(Player player, String message)
    {
        super("KillStreakEvent");

        this.player = player;
        this.message = message;
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

}
