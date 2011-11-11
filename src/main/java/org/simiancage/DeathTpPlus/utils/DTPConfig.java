package org.simiancage.DeathTpPlus.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.util.config.Configuration;
import org.simiancage.DeathTpPlus.DeathTpPlus;

public class DTPConfig
{
    public static enum DeathEventType {
        BLOCK_EXPLOSION, CAVE_SPIDER, CONTACT, CREEPER, DROWNING, ENDERMAN, FALL, FIRE, FIRE_TICK, GHAST, GIANT, LAVA, LIGHTNING, MONSTER, PIG_ZOMBIE, PVP, PVP_FISTS, PVP_TAMED, SILVERFISH, SKELETON, SLIME, SPIDER, STARVATION, SUFFOCATION, SUICIDE, UNKNOWN, VOID, WOLF, ZOMBIE
    };

    public static enum ConfigValueType {
        CHARGE_ITEM, DEATHTP_COST, DEATHTP_TAG, ALLOW_WORLDTRAVEL
    };

    public static enum ConfigFlagType {
        SHOW_DEATHNOTIFY, ALLOW_DEATHTP, SHOW_STREAKS, SHOW_SIGN, ALLOW_DEATHLOG,
    }

    public static HashMap<DeathEventType, List<String>> deathMessages = new HashMap<DeathEventType, List<String>>();
    public static HashMap<ConfigValueType, String> configValues = new HashMap<ConfigValueType, String>();
    public static HashMap<ConfigFlagType, Boolean> configFlags = new HashMap<ConfigFlagType, Boolean>();
    public static List<String> deathStreakMessages;
    public static List<String> killStreakMessages;
    public static boolean worldTravel = false;

    private static final String CONFIG_FILE = "config.yml";
    private static File configFile;

    private Configuration configuration;
    private DeathTpPlus plugin;

    public DTPConfig(DeathTpPlus plugin)
    {
        this.plugin = plugin;

        configFile = new File(DeathTpPlus.dataFolder, CONFIG_FILE);
        configuration = getConfig(configFile);
        configuration.load();

        // Death Event nodes
        for (DeathEventType deathEventType : DeathEventType.values()) {
            deathMessages.put(deathEventType, configuration.getStringList(mapTypeToNodeName(deathEventType), new ArrayList<String>()));
            DeathTpPlus.logger.info(deathMessages.get(deathEventType).size() + " messages loaded for " + deathEventType);
        }

        // Configuration nodes
        for (ConfigValueType configNode : ConfigValueType.values()) {
            configValues.put(configNode, configuration.getString(configNode.toString().toLowerCase().replace("_", "-"), ""));
        }

        for (ConfigFlagType configNode : ConfigFlagType.values()) {
            configFlags.put(configNode, new Boolean(configuration.getString(configNode.toString().toLowerCase().replace("_", "-"), "")));
        }

        // Kill Streak nodes
        killStreakMessages = configuration.getStringList("killstreak", new ArrayList<String>());
        DeathTpPlus.logger.info(killStreakMessages.size() + " messages loaded for killstreak");

        // Death Streak nodes
        deathStreakMessages = configuration.getStringList("deathstreak", new ArrayList<String>());
        DeathTpPlus.logger.info(deathStreakMessages.size() + " messages loaded for deathstreak");

        if (configValues.get(ConfigValueType.ALLOW_WORLDTRAVEL).equalsIgnoreCase("yes") || configValues.get(ConfigValueType.ALLOW_WORLDTRAVEL).equalsIgnoreCase("no") || configValues.get(ConfigValueType.ALLOW_WORLDTRAVEL).equalsIgnoreCase("permissions")) {
            DeathTpPlus.logger.info("allow-wordtravel is: " + configValues.get(ConfigValueType.ALLOW_WORLDTRAVEL));
        }
        else {
            DeathTpPlus.logger.warning("Wrong allow-worldtravel value of " + configValues.get(ConfigValueType.ALLOW_WORLDTRAVEL) + ". Defaulting to NO!");
        }
    }

    private Configuration getConfig(File file)
    {
        if (!file.exists()) {
            try {
                file.getParentFile().mkdir();
                file.createNewFile();
                InputStream inputStream = DTPConfig.class.getResourceAsStream(File.separator + file.getName());
                FileOutputStream outputStream = new FileOutputStream(file);

                byte[] buffer = new byte[8192];
                int length = 0;
                while ((length = inputStream.read(buffer)) > 0)
                    outputStream.write(buffer, 0, length);

                inputStream.close();
                outputStream.close();

                DeathTpPlus.logger.info("Default config created successfully!");
            }
            catch (Exception e) {
                DeathTpPlus.logger.warning("Default config could not be created!");
            }
        }

        return new Configuration(file);
    }

    private String mapTypeToNodeName(DeathEventType deathEventType)
    {
        if (deathEventType.equals(DeathEventType.CAVE_SPIDER)) {
            return "cavespider";
        }
        else if (deathEventType.equals(DeathEventType.PIG_ZOMBIE)) {
            return "pigzombie";
        }

        String nodeName = deathEventType.toString().toLowerCase();
        if (!deathEventType.toString().equals("BLOCK_EXPLOSION") && !deathEventType.toString().equals("FIRE_TICK")) {
            nodeName = nodeName.replace("_", "-");
        }

        return nodeName;
    }
}