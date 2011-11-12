package org.simiancage.DeathTpPlus.listeners;

import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.events.DeathStreakEvent;
import org.simiancage.DeathTpPlus.events.KillStreakEvent;

public class DTPStreakListener extends StreakEventsListener
{
    private DeathTpPlus plugin;

    public DTPStreakListener(DeathTpPlus plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public void onDeathStreakEvent(DeathStreakEvent event)
    {
        plugin.getServer().broadcastMessage(event.getMessage().replace("%n", event.getPlayer().getName()));
    }

    @Override
    public void onKillStreakEvent(KillStreakEvent event)
    {
        plugin.getServer().broadcastMessage(event.getMessage().replace("%n", event.getPlayer().getName()));
    }
}
