package org.simiancage.DeathTpPlus.commands;

import java.util.Hashtable;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.simiancage.DeathTpPlus.DeathTpPlus;

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
            Command cmd = commands.get(command);

            if (sender instanceof Player) {
                if (DeathTpPlus.hasPermission((Player) sender, cmd.getPermission())) {
                    return commands.get(command).execute(sender, args);
                }
                else {
                    sender.sendMessage("You don't have permission for that command.");
                }
            }
            else if (args.length > 0) {
                return commands.get(command).execute(sender, args);
            }
            else {
                DeathTpPlus.logger.info("This is only a player command.");
            }
        }

        return true;
    }
}
