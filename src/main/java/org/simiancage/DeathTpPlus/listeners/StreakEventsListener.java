package org.simiancage.DeathTpPlus.listeners;

import org.bukkit.event.CustomEventListener;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.simiancage.DeathTpPlus.events.DeathStreakEvent;
import org.simiancage.DeathTpPlus.events.KillStreakEvent;

public class StreakEventsListener extends CustomEventListener implements Listener
{
    public StreakEventsListener()
    {
    }

    public void onDeathStreakEvent(DeathStreakEvent event)
    {
    }

    public void onKillStreakEvent(KillStreakEvent event)
    {
    }

    public void onCustomEvent(Event event)
    {
        if (event instanceof DeathStreakEvent) {
            onDeathStreakEvent((DeathStreakEvent) event);
        }
        else if (event instanceof KillStreakEvent) {
            onKillStreakEvent((KillStreakEvent) event);
        }
    }
}
