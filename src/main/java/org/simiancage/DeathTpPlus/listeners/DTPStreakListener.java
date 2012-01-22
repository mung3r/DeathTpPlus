package org.simiancage.DeathTpPlus.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.simiancage.DeathTpPlus.events.DeathStreakEvent;
import org.simiancage.DeathTpPlus.events.KillStreakEvent;

public class DTPStreakListener implements Listener
{
    public DTPStreakListener()
    {
    }

    @EventHandler(event = DeathStreakEvent.class, priority = EventPriority.NORMAL)
    public void onDeathStreakEvent(DeathStreakEvent event)
    {
        Bukkit.broadcastMessage(event.getMessage().replace("%n", event.getPlayer().getName()));
    }

    @EventHandler(event = KillStreakEvent.class, priority = EventPriority.NORMAL)
    public void onKillStreakEvent(KillStreakEvent event)
    {
        Bukkit.broadcastMessage(event.getMessage().replace("%n", event.getPlayer().getName()));
    }
}
