package org.simiancage.DeathTpPlus;

//java imports
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

//bukkit imports
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;

public class DTPEntityListener extends EntityListener
{
    public static DeathTpPlus plugin;

    public DTPEntityListener(DeathTpPlus instance)
    {
        plugin = instance;
    }

    public String getEvent(String deathType)
    {
        int messageindex = 0;

        if (!DeathTpPlus.deathevents.containsKey(deathType))
            return null;

        if (DeathTpPlus.deathevents.get(deathType).size() > 1) {
            Random rand = new Random();
            messageindex = rand.nextInt(DeathTpPlus.deathevents.get(deathType).size());
        }
        return DeathTpPlus.deathevents.get(deathType).get(messageindex);
    }

    public void onEntityDeath(EntityDeathEvent event)
    {

        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            String causeOfDeath = null;
            String killerName = null;
            String murderWeapon = null;
            EntityDamageEvent damageEvent = event.getEntity().getLastDamageCause();

            if (damageEvent instanceof EntityDamageByEntityEvent) {
                Entity damager = ((EntityDamageByEntityEvent) damageEvent).getDamager();
                if (damager instanceof Player) {
                    if (((Player) damager).getItemInHand().getType().equals(Material.AIR)) {
                        causeOfDeath = "FISTS";
                        murderWeapon = "fists";
                    }
                    else {
                        causeOfDeath = "PVP";
                        murderWeapon = ((Player) damager).getItemInHand().getType().toString().replace("_", " ").toLowerCase();
                    }
                    killerName = ((Player) damager).getName();
                }
                else if (damager instanceof Creature) {
                    if (damager instanceof Tameable) {
                        if (((Tameable) damager).isTamed()) {
                            causeOfDeath = "TAMED";
                            murderWeapon = getCreatureType(damager).toString().toLowerCase();
                            killerName = ((Player) ((Tameable) damager).getOwner()).getName();
                        }
                    }
                    else {
                        causeOfDeath = getCreatureType(damager).toString();
                    }
                }
                else if (damager instanceof TNTPrimed) {
                    causeOfDeath = "BLOCK_EXPLOSION";
                }
                else {
                    plugin.getLogger().info("[DeathTpPlus] unknown enitity damager" + damager);
                }
            }
            else if (damageEvent.getCause() != null) {
                causeOfDeath = damageEvent.getCause().toString();
            }

            if (causeOfDeath == null) {
                causeOfDeath = "UNKNOWN";
                murderWeapon = "unknown";
            }
            plugin.getLogger().info("DEBUG: deathCause = " + causeOfDeath);

            String eventAnnounce = "";
            String fileOutput = "";
            String line = "";
            String loghowdied = "";

            if (DeathTpPlus.deathconfig.get("ALLOW_DEATHTP").equals("true")) {
                ArrayList<String> filetext = new ArrayList<String>();
                boolean readCheck = false;
                boolean newPlayerDeath = true;
                // text to write to file
                fileOutput = player.getName() + ":" + player.getLocation().getX() + ":" + player.getLocation().getY() + ":" + player.getLocation().getZ() + ":" + player.getWorld().getName().toString();
                try {
                    FileReader fr = new FileReader(DeathTpPlus.locsName);
                    BufferedReader br = new BufferedReader(fr);

                    while ((line = br.readLine()) != null) {
                        if (line.contains(player.getName() + ":")) {
                            line = fileOutput;
                            newPlayerDeath = false;
                        }
                        filetext.add(line);
                        readCheck = true;
                    }

                    br.close();

                    BufferedWriter out = new BufferedWriter(new FileWriter(DeathTpPlus.locsName));

                    for (int i = 0; i < filetext.size(); i++) {
                        out.write(filetext.get(i));
                        out.newLine();
                    }

                    if (!readCheck) {
                        out.write(fileOutput);
                        out.newLine();
                    }

                    if (newPlayerDeath && readCheck) {
                        out.write(fileOutput);
                        out.newLine();
                    }
                    // Close the output stream
                    out.close();
                }
                catch (IOException e) {
                    plugin.getLogger().severe(e.toString());
                }
            }

            if (DeathTpPlus.deathconfig.get("SHOW_DEATHNOTIFY").equals("true") || DeathTpPlus.deathconfig.get("SHOW_STREAKS").equals("true") || DeathTpPlus.deathconfig.get("DEATH_LOGS").equals("true")) {

                loghowdied = causeOfDeath;
                // TODO: change into case statement and create methods for
                // eventAnnounce
                eventAnnounce = getEvent(causeOfDeath).replace("%n", player.getDisplayName());

                if (causeOfDeath.equals("PVP") || causeOfDeath.equals("FISTS") || causeOfDeath.equals("TAMED")) {
                    loghowdied = killerName;
                    eventAnnounce = eventAnnounce.replace("%i", murderWeapon);
                    eventAnnounce = eventAnnounce.replace("%a", killerName);

                    if (DeathTpPlus.deathconfig.get("SHOW_STREAKS").matches("true")) {
                        writeToStreak(player.getDisplayName(), killerName);
                    }
                    // write kill to deathlog
                    if (DeathTpPlus.deathconfig.get("DEATH_LOGS").matches("true")) {
                        writeToLog("kill", killerName, player.getDisplayName());
                    }
                }
                if (eventAnnounce.equals("")) {
                    eventAnnounce = getEvent("UNKNOWN").replace("%n", player.getDisplayName());
                }

                eventAnnounce = plugin.convertSamloean(eventAnnounce);

                if (DeathTpPlus.deathconfig.get("SHOW_DEATHNOTIFY").equals("true")) {
                    // plugin.getServer().broadcastMessage(eventAnnounce);
                    if (event instanceof PlayerDeathEvent) {
                        ((PlayerDeathEvent) event).setDeathMessage(eventAnnounce);
                    }

                }

                // CraftIRC
                if (DeathTpPlus.craftircHandle != null) {
                    DeathTpPlus.craftircHandle.sendMessageToTag(convertForIrc(eventAnnounce), DeathTpPlus.deathconfig.get("CRAFT_IRC_TAG"));
                }

                if (DeathTpPlus.deathconfig.get("DEATH_LOGS").matches("true")) {
                    writeToLog("death", player.getDisplayName(), loghowdied);
                }

                if (DeathTpPlus.deathconfig.get("SHOW_SIGN").equals("true")) {
                    // place sign
                    Block signBlock = player.getWorld().getBlockAt(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());

                    signBlock.setType(Material.SIGN_POST);

                    BlockState state = signBlock.getState();

                    if (state instanceof Sign) {
                        String signtext;
                        Sign sign = (Sign) state;
                        sign.setLine(0, "[RIP]");
                        sign.setLine(1, player.getDisplayName());
                        sign.setLine(2, "Died by");
                        signtext = causeOfDeath.substring(0, 1) + causeOfDeath.substring(1).toLowerCase();
                        if (causeOfDeath.equals("PVP") || causeOfDeath.equals("FISTS") || causeOfDeath.equals("TAMED"))
                            signtext = killerName;

                        sign.setLine(3, signtext);
                    }
                }

            }

            // added compatibility for streaks if notify is off
            else {
                if (causeOfDeath.equals("PVP") || causeOfDeath.equals("FISTS") || causeOfDeath.equals("TAMED")) {
                    if (DeathTpPlus.deathconfig.get("SHOW_STREAKS").matches("true"))
                        writeToStreak(player.getDisplayName(), killerName);
                }

                if (DeathTpPlus.deathconfig.get("DEATH_LOGS").matches("true")) {
                    writeToLog("death", player.getDisplayName(), loghowdied);
                }
            }

        }
    }

    public void writeToStreak(String defender, String attacker)
    {

        // read the file
        String line = "";
        ArrayList<String> filetext = new ArrayList<String>();

        String[] splittext;
        int atkCurrentStreak = 0;
        int defCurrentStreak = 0;
        boolean foundDefender = false;
        boolean foundAttacker = false;
        boolean isNewFile = true;

        try {
            // File streakFile = new File("plugins/DeathTpPlus/streak.txt");
            // File streakFile = new File(plugin.getDataFolder()+"/streak.txt");
            BufferedReader br = new BufferedReader(new FileReader(DeathTpPlus.streakFile));

            while ((line = br.readLine()) != null) {
                if (line.contains(defender + ":")) {
                    splittext = line.split(":");
                    defCurrentStreak = Integer.parseInt(splittext[1].trim());
                    if (defCurrentStreak > 0) {
                        defCurrentStreak = 0;
                    }
                    defCurrentStreak--;
                    line = defender + ":" + Integer.toString(defCurrentStreak);
                    foundDefender = true;
                }
                if (line.contains(attacker + ":")) {
                    splittext = line.split(":");
                    atkCurrentStreak = Integer.parseInt(splittext[1].trim());
                    if (atkCurrentStreak < 0) {
                        atkCurrentStreak = 0;
                    }
                    atkCurrentStreak++;
                    line = attacker + ":" + Integer.toString(atkCurrentStreak);
                    foundAttacker = true;
                }
                filetext.add(line);
                isNewFile = false;
            }

            br.close();
        }
        catch (IOException e) {
            plugin.getLogger().severe(e.toString());
        }

        String teststreak = "";
        String testsplit[];

        // Check to see if we should announce a streak
        // Deaths
        for (int i = 0; i < DeathTpPlus.deathstreak.get("DEATH_STREAK").size(); i++) {
            teststreak = DeathTpPlus.deathstreak.get("DEATH_STREAK").get(i);
            testsplit = teststreak.split(":");
            if (Integer.parseInt(testsplit[0]) == -(defCurrentStreak)) {
                String announce = plugin.convertSamloean(testsplit[1]);
                plugin.getServer().broadcastMessage(announce.replace("%n", defender));
            }
        }
        // Kills
        for (int i = 0; i < DeathTpPlus.killstreak.get("KILL_STREAK").size(); i++) {
            teststreak = DeathTpPlus.killstreak.get("KILL_STREAK").get(i);
            testsplit = teststreak.split(":");
            if (Integer.parseInt(testsplit[0]) == atkCurrentStreak) {
                String announce = plugin.convertSamloean(testsplit[1]);
                plugin.getServer().broadcastMessage(announce.replace("%n", attacker));
            }
        }

        // Write streaks to file
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(DeathTpPlus.streakFile));

            for (int i = 0; i < filetext.size(); i++) {
                out.write(filetext.get(i));
                out.newLine();
            }

            if (isNewFile) {
                out.write(attacker + ":" + "1");
                out.newLine();
                out.write(defender + ":" + "-1");
                out.newLine();
            }

            if (!foundDefender && !isNewFile) {
                out.write(defender + ":" + "-1");
                out.newLine();
            }

            if (!foundAttacker && !isNewFile) {
                out.write(attacker + ":" + "1");
                out.newLine();
            }
            // Close the output stream
            out.close();
        }
        catch (IOException e) {
            plugin.getLogger().severe(e.toString());
        }
    }

    public void writeToLog(String logtype, String playername, String deathtype)
    {
        // File deathlogFile = new File(plugin.getDataFolder()+"/deathlog.txt");
        File deathlogTempFile = new File(plugin.getDataFolder() + System.getProperty("file.separator") + "deathtlog.tmp");
        String line = "";
        String[] splittext;
        String writeline = "";
        int newrecord = 0;
        boolean foundrecord = false;

        if (!deathlogTempFile.exists()) {
            try {
                deathlogTempFile.createNewFile();
            }
            catch (IOException e) {
                plugin.getLogger().severe("cannot create file " + deathlogTempFile);
            }
        }

        try {
            // format name:type:mob/player:number
            PrintWriter pw = new PrintWriter(new FileWriter(deathlogTempFile));
            BufferedReader br = new BufferedReader(new FileReader(DeathTpPlus.deathlogFile));

            while ((line = br.readLine()) != null) {
                splittext = line.split(":");
                writeline = line;
                if (splittext[0].matches(playername)) {
                    if (splittext[1].matches(logtype)) {
                        if (splittext[2].matches(deathtype)) {
                            newrecord = Integer.parseInt(splittext[3]);
                            newrecord++;
                            writeline = playername + ":" + logtype + ":" + deathtype + ":" + newrecord;
                            foundrecord = true;
                        }
                    }
                }

                pw.println(writeline);
                pw.flush();
            }

            if (!foundrecord) {
                writeline = playername + ":" + logtype + ":" + deathtype + ":1";
                pw.println(writeline);
                pw.flush();
            }

            pw.close();
            br.close();

            DeathTpPlus.deathlogFile.delete();
            deathlogTempFile.renameTo(DeathTpPlus.deathlogFile);
        }
        catch (IOException e) {
            plugin.getLogger().severe("Could not edit deathlog: " + e);
        }

    }

    public static CreatureType getCreatureType(Entity entity)
    {
        if (entity instanceof CaveSpider)
            return CreatureType.CAVE_SPIDER;
        if (entity instanceof Chicken)
            return CreatureType.CHICKEN;
        if (entity instanceof Cow)
            return CreatureType.COW;
        if (entity instanceof Creeper)
            return CreatureType.CREEPER;
        if (entity instanceof Enderman)
            return CreatureType.ENDERMAN;
        if (entity instanceof Ghast)
            return CreatureType.GHAST;
        if (entity instanceof Giant)
            return CreatureType.GIANT;
        if (entity instanceof Pig)
            return CreatureType.PIG;
        if (entity instanceof PigZombie)
            return CreatureType.PIG_ZOMBIE;
        if (entity instanceof Sheep)
            return CreatureType.SHEEP;
        if (entity instanceof Skeleton)
            return CreatureType.SKELETON;
        if (entity instanceof Slime)
            return CreatureType.SLIME;
        if (entity instanceof Silverfish)
            return CreatureType.SILVERFISH;
        if (entity instanceof Spider)
            return CreatureType.SPIDER;
        if (entity instanceof Squid)
            return CreatureType.SQUID;
        if (entity instanceof Zombie)
            return CreatureType.ZOMBIE;
        if (entity instanceof Wolf)
            return CreatureType.WOLF;

        // Monster is a parent class and needs to be last
        if (entity instanceof Monster)
            return CreatureType.MONSTER;
        return null;
    }

    public static String convertForIrc(String msg)
    {
        String ircAnnounce;
        ircAnnounce = msg.replace("§0", "");
        ircAnnounce = ircAnnounce.replace("§2", "");
        ircAnnounce = ircAnnounce.replace("§3", "");
        ircAnnounce = ircAnnounce.replace("§4", "");
        ircAnnounce = ircAnnounce.replace("§5", "");
        ircAnnounce = ircAnnounce.replace("§6", "");
        ircAnnounce = ircAnnounce.replace("§7", "");
        ircAnnounce = ircAnnounce.replace("§8", "");
        ircAnnounce = ircAnnounce.replace("§9", "");
        ircAnnounce = ircAnnounce.replace("§a", "");
        ircAnnounce = ircAnnounce.replace("§b", "");
        ircAnnounce = ircAnnounce.replace("§c", "");
        ircAnnounce = ircAnnounce.replace("§d", "");
        ircAnnounce = ircAnnounce.replace("§e", "");
        ircAnnounce = ircAnnounce.replace("§f", "");

        return ircAnnounce;
    }
}
