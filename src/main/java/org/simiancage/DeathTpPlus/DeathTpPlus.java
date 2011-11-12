package org.simiancage.DeathTpPlus;

import java.io.File;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.simiancage.DeathTpPlus.commands.CommandHandler;
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

    // damage and death listener
    private final DTPEntityListener entityListener = new DTPEntityListener(this);
    private final DTPStreakListener streakListener = new DTPStreakListener(this);

    // plugin variables
    public static DTPLogger logger;
    public static DTPConfig config;
    public static DTPDeathLocationLog deathLocationLog;
    public static DTPDeathLog deathLog;
    public static DTPStreakLog streakLog;
    private CommandHandler commandHandler;

    // permissions & economy
    public static Permission permission = null;
    public static Economy economy = null;

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
    }

    public void onDisable()
    {
        logger.info("Disabled");
    }

    public void onEnable()
    {
        logger = new DTPLogger(this);
        config = new DTPConfig(this);
        deathLocationLog = new DTPDeathLocationLog();
        deathLog = new DTPDeathLog();
        streakLog = new DTPStreakLog(this);
        commandHandler = new CommandHandler();

        setupDependencies();

        getServer().getPluginManager().registerEvent(Event.Type.ENTITY_DEATH, entityListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.CUSTOM_EVENT, streakListener, Priority.Normal, this);

        logger.info("version " + getDescription().getVersion() + " is enabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        return commandHandler.dispatch(sender, commandLabel, args);
    }
}
