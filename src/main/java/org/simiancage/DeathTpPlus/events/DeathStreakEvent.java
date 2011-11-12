package org.simiancage.DeathTpPlus.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class DeathStreakEvent extends Event
{
    private Player player;
    private String message;

    public DeathStreakEvent(Player player, String message)
    {
        super("DeathStreakEvent");

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
