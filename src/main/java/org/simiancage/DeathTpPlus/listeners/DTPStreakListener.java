package org.simiancage.DeathTpPlus.listeners;

import org.bukkit.plugin.Plugin;
import org.simiancage.DeathTpPlus.events.DeathStreakEvent;
import org.simiancage.DeathTpPlus.events.KillStreakEvent;

public class DTPStreakListener extends StreakEventsListener
{
    private Plugin plugin;

    public DTPStreakListener(Plugin plugin)
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
