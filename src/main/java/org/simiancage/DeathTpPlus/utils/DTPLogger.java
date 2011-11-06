package org.simiancage.DeathTpPlus.utils;

import java.util.logging.Logger;

import org.simiancage.DeathTpPlus.DeathTpPlus;

public class DTPLogger
{
    private DeathTpPlus plugin;
    private Logger logger;

    public DTPLogger(DeathTpPlus plugin)
    {
        this.plugin = plugin;
        logger = Logger.getLogger("Minecraft");
    }

    public void info(String msg)
    {
        logger.info(format(msg));
    }

    public void warning(String msg)
    {
        logger.warning(format(msg));
    }

    public void severe(String msg)
    {
        logger.severe(format(msg));
    }

    public String format(String msg)
    {
        return String.format("[%s] %s", plugin.getDescription().getName(), msg);
    }
}
