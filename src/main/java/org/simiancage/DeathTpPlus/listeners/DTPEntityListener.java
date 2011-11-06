package org.simiancage.DeathTpPlus.listeners;

import java.util.Random;

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
import org.simiancage.DeathTpPlus.DeathTpPlus;

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
                else if (damager instanceof Projectile) {
                    // TODO: find out why we never get damager
                }
                else if (damager instanceof TNTPrimed) {
                    causeOfDeath = "BLOCK_EXPLOSION";
                }
                else {
                    plugin.getLogger().info("[DeathTpPlus] unknown enitity damager" + damager);
                }
            }
            else if (damageEvent != null) {
                causeOfDeath = damageEvent.getCause().toString();
            }

            if (causeOfDeath == null) {
                causeOfDeath = "UNKNOWN";
                murderWeapon = "unknown";
            }

            String eventAnnounce = "";
            String loghowdied = "";

            if (DeathTpPlus.deathconfig.get("ALLOW_DEATHTP").equals("true")) {
                plugin.getLocationLog().setRecord(player);
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
                        plugin.getStreakLog().setRecord(killerName, player.getDisplayName());
                    }
                    // write kill to deathlog
                    if (DeathTpPlus.deathconfig.get("DEATH_LOGS").matches("true")) {
                        plugin.getDeathLog().setRecord(killerName, "kill", player.getDisplayName());
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
                    DeathTpPlus.craftircHandle.sendMessageToTag(convertForIRC(eventAnnounce), DeathTpPlus.deathconfig.get("CRAFT_IRC_TAG"));
                }

                if (DeathTpPlus.deathconfig.get("DEATH_LOGS").matches("true")) {
                    plugin.getDeathLog().setRecord(player.getDisplayName(), "death", loghowdied);
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
                        plugin.getStreakLog().setRecord(killerName, player.getDisplayName());
                }

                if (DeathTpPlus.deathconfig.get("DEATH_LOGS").matches("true")) {
                    plugin.getDeathLog().setRecord(player.getDisplayName(), "death", loghowdied);
                }
            }

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

    public static String convertForIRC(String msg)
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
