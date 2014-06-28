package org.simiancage.DeathTpPlus.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.events.DeathStreakEvent;
import org.simiancage.DeathTpPlus.events.KillStreakEvent;

public class DTPStreakListener implements Listener {

    private final DeathTpPlus plugin;

    public DTPStreakListener(DeathTpPlus plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onDeathStreakEvent(DeathStreakEvent event) {
        Bukkit.broadcastMessage(event.getMessage().replace("%n", event.getPlayer().getName()));
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onKillStreakEvent(final KillStreakEvent event) {
        Bukkit.broadcastMessage(event.getMessage().replace("%n", event.getPlayer().getName()));
        final Location location = event.getPlayer().getLocation();
    }
}
