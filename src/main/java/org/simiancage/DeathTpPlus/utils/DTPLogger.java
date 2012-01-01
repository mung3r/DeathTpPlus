package org.simiancage.DeathTpPlus.utils;

import java.util.logging.Logger;

public class DTPLogger
{
    private String name = "DeathTpPlus";
    private Logger logger;

    public DTPLogger()
    {
        logger = Logger.getLogger("Minecraft");
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
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
        return String.format("[%s] %s", name, msg);
    }
}
