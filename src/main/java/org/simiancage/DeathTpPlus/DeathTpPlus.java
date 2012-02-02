package org.simiancage.DeathTpPlus;

import java.io.File;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.getspout.spout.Spout;
import org.simiancage.DeathTpPlus.commands.CommandHandler;
import org.simiancage.DeathTpPlus.commands.DeathTpCommand;
import org.simiancage.DeathTpPlus.commands.DeathsCommand;
import org.simiancage.DeathTpPlus.commands.HelpCommand;
import org.simiancage.DeathTpPlus.commands.KillsCommand;
import org.simiancage.DeathTpPlus.commands.ReportCommand;
import org.simiancage.DeathTpPlus.commands.StreakCommand;
import org.simiancage.DeathTpPlus.commands.TopCommand;
import org.simiancage.DeathTpPlus.listeners.DTPEntityListener;
import org.simiancage.DeathTpPlus.listeners.DTPStreakListener;
import org.simiancage.DeathTpPlus.logs.DTPDeathLog;
import org.simiancage.DeathTpPlus.logs.DTPDeathLocationLog;
import org.simiancage.DeathTpPlus.logs.DTPStreakLog;
import org.simiancage.DeathTpPlus.utils.DTPConfig;
import org.simiancage.DeathTpPlus.utils.DTPLogger;

//craftirc
import com.ensifera.animosity.craftirc.CraftIRC;

public class DeathTpPlus extends JavaPlugin
{
    public static final File dataFolder = new File("plugins" + File.separator + "DeathTpPlus");

    // plugin variables
    public static DTPLogger logger = new DTPLogger();
    public static DTPConfig config;
    public static DTPDeathLocationLog deathLocationLog;
    public static DTPDeathLog deathLog;
    public static DTPStreakLog streakLog;
    private static CommandHandler commandHandler = new CommandHandler();

    // permissions & economy
    public static Permission permission = null;
    public static Economy economy = null;

    // Spout
    public static Spout spout = null;

    // CraftIRC
    public static CraftIRC craftIRCPlugin = null;

    private void setupDependencies()
    {
        // permission
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
            logger.info("found permission provider");
        }

        // economy
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
            logger.info("found economy provider");
        }

        // hook CraftIRC
        Plugin plugin = this.getServer().getPluginManager().getPlugin("CraftIRC");
        if (plugin != null && plugin instanceof CraftIRC) {
            craftIRCPlugin = (CraftIRC) plugin;
            logger.info("CraftIRC support enabled");
        }

        Plugin sPlugin = this.getServer().getPluginManager().getPlugin("Spout");
        if (sPlugin != null && sPlugin instanceof Spout) {
            spout = (Spout) sPlugin;
            logger.info("Spout support enabled");
        }
    }

    private void registerEvents()
    {
        Bukkit.getPluginManager().registerEvents(new DTPEntityListener(), this);
        Bukkit.getPluginManager().registerEvents(new DTPStreakListener(this), this);
    }

    private void registerCommands()
    {
        commandHandler.addCommand(new HelpCommand(this));
        commandHandler.addCommand(new KillsCommand(this));
        commandHandler.addCommand(new DeathsCommand(this));
        commandHandler.addCommand(new StreakCommand(this));
        commandHandler.addCommand(new ReportCommand(this));
        commandHandler.addCommand(new TopCommand(this));
        commandHandler.addCommand(new DeathTpCommand(this));
    }

    public void onDisable()
    {
        logger.info("Disabled");
    }

    public void onEnable()
    {
        logger.setName(getDescription().getName());
        config = new DTPConfig(this);
        deathLocationLog = new DTPDeathLocationLog();
        deathLog = new DTPDeathLog();
        streakLog = new DTPStreakLog();

        setupDependencies();
        registerEvents();
        registerCommands();

        logger.info("version " + getDescription().getVersion() + " is enabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        return commandHandler.dispatch(sender, cmd, commandLabel, args);
    }

    public static boolean hasPermission(Player player, String name)
    {
        if (permission != null) {
            return permission.has(player, name);
        }
        return player.hasPermission(name);
    }

    public CommandHandler getCommandHandler()
    {
        return commandHandler;
    }
}
