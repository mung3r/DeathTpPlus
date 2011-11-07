package org.simiancage.DeathTpPlus.commands;

import java.util.Hashtable;

import org.bukkit.command.CommandSender;

public class CommandHandler
{
    private Hashtable<String, Command> commands;

    public CommandHandler()
    {
        commands = new Hashtable<String, Command>();
        commands.put("deaths", new DeathsCommand());
        commands.put("deathtp", new DeathTpCommand());
        commands.put("kills", new KillsCommand());
        commands.put("streak", new StreakCommand());
    }

    public Boolean dispatch(CommandSender sender, String command, String[] args)
    {
        if (commands.containsKey(command)) {
            return commands.get(command).execute(sender, args);
        }

        return false;
    }
}
