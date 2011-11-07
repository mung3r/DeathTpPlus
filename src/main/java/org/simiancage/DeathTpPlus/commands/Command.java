package org.simiancage.DeathTpPlus.commands;

import org.bukkit.command.CommandSender;

public interface Command
{
    public Boolean execute(CommandSender sender, String[] args);
}
