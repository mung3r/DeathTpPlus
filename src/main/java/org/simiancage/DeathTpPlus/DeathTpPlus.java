package org.simiancage.DeathTpPlus;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.jar.JarFile;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.simiancage.DeathTpPlus.listeners.DTPEntityListener;
import org.simiancage.DeathTpPlus.logs.DTPDeathLog;
import org.simiancage.DeathTpPlus.logs.DTPLocationLog;
import org.simiancage.DeathTpPlus.logs.DTPStreakLog;

//craftirc
import com.ensifera.animosity.craftirc.CraftIRC;

public class DeathTpPlus extends JavaPlugin
{
    private static final String CONFIG_FILE = "config.yml";
    private static final String LOCATION_LOG_FILE = "locs.txt";
    private static final String STREAK_LOG_FILE = "streak.txt";
    private static final String DEATH_LOG_FILE = "deathlog.txt";

    public static final File dataFolder = new File("plugins" + File.separator + "DeathTpPlus");
    
    // damage and death listener
    private final DTPEntityListener entityListener = new DTPEntityListener(this);

    // plugin variables
    protected Logger log;
    private DeathTpPlus plugin = this;
    public static HashMap<String, List<String>> killstreak = new HashMap<String, List<String>>();
    public static HashMap<String, List<String>> deathstreak = new HashMap<String, List<String>>();
    public static HashMap<String, List<String>> deathevents = new HashMap<String, List<String>>();
    public static HashMap<String, String> deathconfig = new HashMap<String, String>();
    public static File configFile;
    protected String logName = null;
    protected String pluginName = null;
    protected String pluginVersion = null;
    protected ArrayList<String> pluginAuthor = null;
    protected String pluginPath = null;
    protected boolean worldTravel = false;
    private DTPLocationLog locationLog;
    private DTPStreakLog streakLog;
    private DTPDeathLog deathLog;
    FileConfiguration configuration;

    // Register
    static boolean Register = false;
    boolean useRegister = false;
    Permission permission = null;
    Economy economy = null;

    // craftirc
    public static CraftIRC craftircHandle = null;

    public void onDisable()
    {
        log.info("[DeathTpPlus] Disabled");
    }

    public void onEnable()
    {
        log = Bukkit.getServer().getLogger();
        pluginName = getDescription().getName();
        logName = "[" + pluginName + "] ";
        pluginVersion = getDescription().getVersion();
        pluginAuthor = getDescription().getAuthors();
        configFile = new File(dataFolder, CONFIG_FILE);

        locationLog = new DTPLocationLog(plugin, LOCATION_LOG_FILE);
        streakLog = new DTPStreakLog(plugin, STREAK_LOG_FILE);
        deathLog = new DTPDeathLog(plugin, DEATH_LOG_FILE);

        // Todo write Helper Class for this

        if (!configFile.exists()) {
            new File(getDataFolder().toString()).mkdir();
            try {
                JarFile jar = new JarFile("plugins" + System.getProperty("file.separator") + getDescription().getName() + ".jar");
                ZipEntry config = jar.getEntry("config.yml");
                InputStream in = new BufferedInputStream(jar.getInputStream(config));
                OutputStream out = new BufferedOutputStream(new FileOutputStream(configFile));
                int c;
                while ((c = in.read()) != -1) {
                    out.write(c);
                }
                out.flush();
                out.close();
                in.close();
                jar.close();
                log.info(logName + "Default config created successfully!");
            }
            catch (Exception e) {
                log.warning(logName + "Default config could not be created!");

            }
        }
        configuration = this.getConfig();

        DefaultConfiguration();
        // Death Event nodes
        deathevents.put("FALL", (List<String>) configuration.getList("fall"));
        deathevents.put("DROWNING", (List<String>) configuration.getList("drowning"));
        deathevents.put("FIRE", (List<String>) configuration.getList("fire"));
        deathevents.put("FIRE_TICK", (List<String>) configuration.getList("fire_tick"));
        deathevents.put("LAVA", (List<String>) configuration.getList("lava"));
        deathevents.put("BLOCK_EXPLOSION", (List<String>) configuration.getList("block_explosion"));
        deathevents.put("CREEPER", (List<String>) configuration.getList("creeper"));
        deathevents.put("SKELETON", (List<String>) configuration.getList("skeleton"));
        deathevents.put("SPIDER", (List<String>) configuration.getList("spider"));
        deathevents.put("ZOMBIE", (List<String>) configuration.getList("zombie"));
        deathevents.put("CONTACT", (List<String>) configuration.getList("contact"));
        deathevents.put("PIGZOMBIE", (List<String>) configuration.getList("pigzombie"));
        deathevents.put("GHAST", (List<String>) configuration.getList("ghast"));
        deathevents.put("SLIME", (List<String>) configuration.getList("slime"));
        deathevents.put("PVP", (List<String>) configuration.getList("pvp"));
        deathevents.put("FISTS", (List<String>) configuration.getList("pvp-fists"));
        deathevents.put("TAMED", (List<String>) configuration.getList("pvp-tamed"));
        deathevents.put("SUFFOCATION", (List<String>) configuration.getList("suffocation"));
        deathevents.put("VOID", (List<String>) configuration.getList("void"));
        deathevents.put("WOLF", (List<String>) configuration.getList("wolf"));
        deathevents.put("LIGHTNING", (List<String>) configuration.getList("lightning"));
        deathevents.put("SUICIDE", (List<String>) configuration.getList("suicide"));
        deathevents.put("UNKNOWN", (List<String>) configuration.getList("unknown"));
        deathevents.put("STARVATION", (List<String>) configuration.getList("starvation"));
        deathevents.put("CAVESPIDER", (List<String>) configuration.getList("cavespider"));
        deathevents.put("ENDERMAN", (List<String>) configuration.getList("enderman"));
        deathevents.put("SILVERFISH", (List<String>) configuration.getList("silverfish"));
        deathevents.put("GIANT", (List<String>) configuration.getList("giant"));
        deathevents.put("MONSTER", (List<String>) configuration.getList("monster"));
        // Configuration nodes
        deathconfig.put("SHOW_DEATHNOTIFY", configuration.getString("show-deathnotify"));
        deathconfig.put("ALLOW_DEATHTP", configuration.getString("allow-deathtp"));
        deathconfig.put("SHOW_STREAKS", configuration.getString("show-streaks"));
        deathconfig.put("CHARGE_ITEM_ID", configuration.getString("charge-item"));
        deathconfig.put("SHOW_SIGN", configuration.getString("show-sign"));
        deathconfig.put("REGISTER_COST", configuration.getString("deathtp-cost"));
        deathconfig.put("CRAFT_IRC_TAG", configuration.getString("deathtp-tag"));
        deathconfig.put("DEATH_LOGS", configuration.getString("allow-deathlog"));
        deathconfig.put("WORLD_TRAVEL", configuration.getString("allow-worldtravel"));
        // Kill Streak nodes
        killstreak.put("KILL_STREAK", (List<String>) configuration.getList("killstreak"));
        // Death Streak nodes
        deathstreak.put("DEATH_STREAK", (List<String>) configuration.getList("deathstreak"));
        log.info(logName + killstreak.get("KILL_STREAK").size() + " Kill Streaks loaded.");
        log.info(logName + deathstreak.get("DEATH_STREAK").size() + " Death Streaks loaded.");

        if (deathconfig.get("WORLD_TRAVEL").equalsIgnoreCase("yes")) {
            worldTravel = true;
        }

        if (deathconfig.get("WORLD_TRAVEL").equalsIgnoreCase("yes") || deathconfig.get("WORLD_TRAVEL").equalsIgnoreCase("no") || deathconfig.get("WORLD_TRAVEL").equalsIgnoreCase("permissions")) {
            log.info("[" + pluginName + "] allow-wordtravel is: " + deathconfig.get("WORLD_TRAVEL"));
        }
        else {
            log.warning("[" + pluginName + "] Wrong allow-worldtravel value of " + deathconfig.get("WORLD_TRAVEL") + ". Defaulting to NO!");
            worldTravel = false;
        }

        // Create the pluginmanage pm.
        PluginManager pm = getServer().getPluginManager();

        pm.registerEvent(Event.Type.ENTITY_DEATH, entityListener, Priority.Normal, this);

        // Permission
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
            log.info("[" + pluginName + "] found permission provider");
        }

        // Register
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
            log.info("[" + pluginName + "] found economy provider");
        }

        // craftirc
        Plugin checkCraftIRC = this.getServer().getPluginManager().getPlugin("CraftIRC");
        if (checkCraftIRC != null) {
            try {
                craftircHandle = (CraftIRC) checkCraftIRC;
                // Todo Enable Logger
                log.info(logName + "CraftIRC Support Enabled.");
            }
            catch (ClassCastException ex) {
            }
        }

        // print success
        PluginDescriptionFile pdfFile = this.getDescription();
        // Todo Enable Logger
        log.info("[DeathTpPlus] version " + pdfFile.getVersion() + " is enabled!");
    }

    private void DefaultConfiguration()
    {
        configuration.addDefault("fall", "");
        configuration.addDefault("drowning", "");
        configuration.addDefault("fire", "");
        configuration.addDefault("fire_tick", "");
        configuration.addDefault("lava", "");
        configuration.addDefault("block_explosion", "");
        configuration.addDefault("creeper", "");
        configuration.addDefault("skeleton", "");
        configuration.addDefault("spider", "");
        configuration.addDefault("zombie", "");
        configuration.addDefault("contact", "");
        configuration.addDefault("pigzombie", "");
        configuration.addDefault("ghast", "");
        configuration.addDefault("slime", "");
        configuration.addDefault("pvp", "");
        configuration.addDefault("pvp-fists", "");
        configuration.addDefault("pvp-tamed", "");
        configuration.addDefault("suffocation", "");
        configuration.addDefault("void", "");
        configuration.addDefault("wolf", "");
        configuration.addDefault("lightning", "");
        configuration.addDefault("suicide", "");
        configuration.addDefault("unknown", "");
        configuration.addDefault("starvation", "");
        configuration.addDefault("cavespider", "");
        configuration.addDefault("enderman", "");
        configuration.addDefault("silverfish", "");
        configuration.addDefault("giant", "");
        configuration.addDefault("monster", "");
        // Configuration nodes
        configuration.addDefault("show-deathnotify", "true");
        configuration.addDefault("allow-deathtp", "true");
        configuration.addDefault("show-streaks", "true");
        configuration.addDefault("charge-item", "0");
        configuration.addDefault("show-sign", "false");
        configuration.addDefault("deathtp-cost", "0");
        configuration.addDefault("deathtp-tag", "");
        configuration.addDefault("allow-deathlog", "true");
        configuration.addDefault("allow-worldtravel", "no");
        // Kill Streak nodes
        configuration.addDefault("killstreak", "");
        // Death Streak nodes
        configuration.addDefault("deathstreak", "");
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        String command = commandLabel;
        boolean canUseCommand = false;
        boolean teleportok = true;
        boolean teleported = false;

        if (command.equals("deathtp")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                String thisWorld = player.getWorld().getName().toString();
                if (permission.has(player, "deathtpplus.worldtravel") && deathconfig.get("WORLD_TRAVEL").equalsIgnoreCase("permissions")) {
                    worldTravel = true;
                }
                double registerCost = Double.valueOf(deathconfig.get("REGISTER_COST").trim()).doubleValue();

                if (permission.has(player, "deathtpplus.deathtp")) {
                    canUseCommand = true;
                }
                else {
                    canUseCommand = deathconfig.get("ALLOW_DEATHTP").equals("true");
                }

                if (canUseCommand) {
                    // costs item in inventory
                    if (!deathconfig.get("CHARGE_ITEM_ID").equals("0")) {
                        if (player.getItemInHand().getType().getId() != Integer.parseInt(deathconfig.get("CHARGE_ITEM_ID"))) {
                            player.sendMessage("You must be holding a " + Material.getMaterial(Integer.parseInt(deathconfig.get("CHARGE_ITEM_ID"))).toString() + " to teleport.");
                            teleportok = false;
                        }
                        else {
                            ItemStack currentitem = player.getItemInHand();
                            int itemnum = currentitem.getAmount();
                            itemnum--;
                            if (itemnum > 0) {
                                currentitem.setAmount(itemnum);
                                player.setItemInHand(currentitem);
                            }
                            else {
                                player.getInventory().clear(player.getInventory().getHeldItemSlot());
                            }
                        }
                    }

                    // Todo CHange => register
                    // costs iconomy
                    if (registerCost > 0) {
                        if (useRegister) {
                            if (economy != null && economy.getBalance(player.getName()) > registerCost) {
                                economy.withdrawPlayer(player.getName(), registerCost);
                                player.sendMessage("You used " + registerCost + " to use /deathtp");
                            }
                            else {
                                player.sendMessage("You need " + registerCost + " coins to use /deathtp");
                                teleportok = false;
                            }
                        }

                    }

                    if (teleportok) {

                        String[] location;
                        String teleloc = getLocationLog().getRecord(player.getName());

                        if (teleloc != "") {
                            location = teleloc.split(":");
                            Location sendLocation = player.getLocation();
                            double x, y, z;

                            x = Double.valueOf(location[1].trim()).doubleValue();
                            y = Double.valueOf(location[2].trim()).doubleValue();
                            z = Double.valueOf(location[3].trim()).doubleValue();
                            World deathWorld = this.getServer().getWorld(location[4].trim());
                            sendLocation.setX(x);
                            sendLocation.setY(y);
                            sendLocation.setZ(z);

                            boolean safeTele = false;
                            int test1 = -1, test2 = -1;
                            while (!safeTele) {
                                test1 = player.getWorld().getBlockTypeIdAt(sendLocation);
                                test2 = player.getWorld().getBlockTypeIdAt(sendLocation);
                                if (test1 == 0 && test2 == 0) {
                                    safeTele = true;
                                }

                                sendLocation.setY(sendLocation.getY() + 1);
                            }

                            if (!thisWorld.equals(deathWorld.getName())) {
                                if (worldTravel) {
                                    sendLocation.setWorld(deathWorld);
                                    player.teleport(sendLocation);
                                    teleported = true;
                                }
                                else {
                                    player.sendMessage("You do not have the right to travel between worlds via deathtp!");
                                }
                            }
                            else {
                                player.teleport(sendLocation);
                                teleported = true;
                            }
                        }
                        else {
                            player.sendMessage("You do not have a last known death location.");
                        }
                        if (useRegister && !teleported && economy != null) {
                            if (economy != null)
                                economy.depositPlayer(player.getName(), registerCost);
                            player.sendMessage("Giving you back " + registerCost);
                        }
                    }
                    else {
                        player.sendMessage("That command is not available");
                    }

                }

                return true;
            }

            else {
                System.out.println("This is only a player command.");
                return true;
            }

        }

        else if (command.equals("deaths")) {
            String playername = "";
            String cause = "";
            int totalnum = 0;
            String[] splittext;
            boolean foundrecord = false;

            if (sender instanceof Player) {
                Player player = (Player) sender;

                canUseCommand = permission.playerHas(player, "deathtpplus.deaths");
            }

            if (canUseCommand) {

                if (args.length == 0) {
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        playername = player.getName();
                    }
                    else {
                        return false;
                    }
                }
                else if (args.length == 1) {
                    playername = args[0];
                }
                else if (args.length == 2) {
                    playername = args[0];
                    cause = args[1];
                }
                else {
                    return false;
                }

                List<String> lines = getDeathLog().getRecords(playername);
                for (String line : lines) {
                    splittext = line.split(":");
                    // 0 = name, 1 = type, 2 = cause, 3 = number
                    if (!cause.isEmpty()) {
                        if (splittext[0].equalsIgnoreCase(playername) && splittext[1].equals("death") && splittext[2].equalsIgnoreCase(cause)) {
                            String times = "times";
                            if (splittext[2] == "1") {
                                times = "time";
                            }
                            sender.sendMessage(playername + " has died by " + cause + " " + splittext[3] + " " + times);
                            foundrecord = true;
                        }
                    }
                    // total count
                    else {
                        if (splittext[0].equalsIgnoreCase(playername) && splittext[1].equals("death")) {
                            totalnum = totalnum + Integer.parseInt(splittext[3]);
                        }
                    }
                }

                if (cause.isEmpty()) {
                    String times = "times";
                    if (totalnum == 1) {
                        times = "time";
                    }
                    sender.sendMessage(playername + " has died " + totalnum + " " + times);
                }
                else {
                    if (!foundrecord) {
                        sender.sendMessage(playername + " has died by " + cause + " 0 times");
                    }
                }
                return true;
            }

            else {
                return true;
            }
        }

        else if (command.equals("kills")) {
            String playername = "";
            String username = "";
            int totalnum = 0;
            String[] splittext;
            boolean foundrecord = false;

            if (sender instanceof Player) {
                Player player = (Player) sender;

                canUseCommand = permission.playerHas(player, "deathtpplus.kills");
            }

            if (canUseCommand) {
                if (args.length == 0) {
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        playername = player.getName();
                    }
                    else {
                        return false;
                    }
                }
                else if (args.length == 1) {
                    playername = args[0];
                }
                else if (args.length == 2) {
                    playername = args[0];
                    username = args[1];
                }
                else {
                    return false;
                }
                // File deathlogFile = new
                // File(getDataFolder()+"/deathlog.txt");
                List<String> lines = deathLog.getRecords(playername);
                for (String line : lines) {
                    splittext = line.split(":");
                    // 0 = name, 1 = type, 2 = cause, 3 = number
                    if (!username.isEmpty()) {
                        if (splittext[0].equalsIgnoreCase(playername) && splittext[1].equals("kill") && splittext[2].equalsIgnoreCase(username)) {
                            String times = "times";
                            if (splittext[2] == "1")
                                times = "time";
                            sender.sendMessage(playername + " has killed " + username + " " + splittext[3] + " " + times);
                            foundrecord = true;
                        }
                    }
                    // total count
                    else {
                        if (splittext[0].equalsIgnoreCase(playername) && splittext[1].equals("kill")) {
                            totalnum = totalnum + Integer.parseInt(splittext[3]);
                        }
                    }
                }
                if (username.isEmpty()) {
                    String times = "times";
                    if (totalnum == 1) {
                        times = "time";
                    }
                    sender.sendMessage(playername + " has killed " + totalnum + " " + times);
                }
                else {
                    if (!foundrecord) {
                        sender.sendMessage(playername + " has killed " + username + " 0 times");
                    }
                }
                return true;
            }
            else {
                return true;
            }
        }

        else if (command.equals("streak")) {

            if (sender instanceof Player) {
                Player player = (Player) sender;
                canUseCommand = permission.playerHas(player, "deathtpplus.streak");
            }

            if (canUseCommand) {
                if (DeathTpPlus.deathconfig.get("SHOW_STREAKS").equals("true")) {
                    // File streakFile = new
                    // File("plugins/DeathTpPlus/streak.txt");
                    String line;
                    String[] splittext;
                    Player check;
                    String playername = "player";

                    if (args.length > 0) {
                        playername = args[0];
                    }
                    else {
                        if (sender instanceof Player) {
                            check = (Player) sender;
                            playername = check.getName();
                        }
                    }

                    boolean entryfound = false;
                    line = getStreakLog().getRecord(playername);
                    if (line != null) {
                        if (!line.startsWith("#")) {
                            splittext = line.split(":");
                            if (playername.equalsIgnoreCase(splittext[0])) {
                                if (Integer.parseInt(splittext[1]) < 0) {
                                    sender.sendMessage(ChatColor.GRAY + splittext[0] + "'s Current Streak: " + splittext[1].replace("-", "") + " Death(s)");
                                }
                                else {
                                    sender.sendMessage(ChatColor.GRAY + splittext[0] + "'s Current Streak: " + splittext[1] + " Kill(s)");
                                }

                                entryfound = true;
                            }
                        }
                    }
                    if (!entryfound) {
                        sender.sendMessage("No streak found");
                    }
                    return true;
                }
                else {
                    return true;
                }
            }
            else {
                return true;
            }

        }

        return false;
    }

    public String convertSamloean(String convert)
    {
        convert = convert.replace("&0", "§0");
        convert = convert.replace("&1", "§1");
        convert = convert.replace("&2", "§2");
        convert = convert.replace("&3", "§3");
        convert = convert.replace("&4", "§4");
        convert = convert.replace("&5", "§5");
        convert = convert.replace("&6", "§6");
        convert = convert.replace("&7", "§7");
        convert = convert.replace("&8", "§8");
        convert = convert.replace("&9", "§9");
        convert = convert.replace("&a", "§a");
        convert = convert.replace("&b", "§b");
        convert = convert.replace("&c", "§c");
        convert = convert.replace("&d", "§d");
        convert = convert.replace("&e", "§e");
        convert = convert.replace("&f", "§f");

        return convert;
    }

    public DTPLocationLog getLocationLog()
    {
        return locationLog;
    }

    public DTPStreakLog getStreakLog()
    {
        return streakLog;
    }

    public DTPDeathLog getDeathLog()
    {
        return deathLog;
    }

    public Logger getLogger()
    {
        return log;
    }
}
