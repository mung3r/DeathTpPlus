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
import org.simiancage.DeathTpPlus.utils.DTPConfig;
import org.simiancage.DeathTpPlus.utils.DTPConfig.ConfigType;
import org.simiancage.DeathTpPlus.utils.DTPConfig.DeathEventType;
import org.simiancage.DeathTpPlus.utils.DTPUtils;

public class DTPEntityListener extends EntityListener
{
    public static DeathTpPlus plugin;

    public DTPEntityListener(DeathTpPlus instance)
    {
        plugin = instance;
    }

    public void onEntityDeath(EntityDeathEvent event)
    {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();

        DeathEventType causeOfDeath = null;
        String killerName = null;
        String murderWeapon = null;
        EntityDamageEvent damageEvent = event.getEntity().getLastDamageCause();

        if (damageEvent instanceof EntityDamageByEntityEvent) {
            Entity damager = ((EntityDamageByEntityEvent) damageEvent).getDamager();
            if (damager instanceof Player) {
                if (((Player) damager).getItemInHand().getType().equals(Material.AIR)) {
                    causeOfDeath = DeathEventType.PVP_FISTS;
                    murderWeapon = "fists";
                }
                else {
                    causeOfDeath = DeathEventType.PVP;
                    murderWeapon = ((Player) damager).getItemInHand().getType().toString().replace("_", " ").toLowerCase();
                }
                killerName = ((Player) damager).getName();
            }
            else if (damager instanceof Creature) {
                if (damager instanceof Tameable) {
                    if (((Tameable) damager).isTamed()) {
                        causeOfDeath = DeathEventType.PVP_TAMED;
                        murderWeapon = DTPUtils.getCreatureType(damager).toString().toLowerCase();
                        killerName = ((Player) ((Tameable) damager).getOwner()).getName();
                    }
                }
                else {
                    causeOfDeath = DeathEventType.valueOf(DTPUtils.getCreatureType(damager).toString());
                }
            }
            else if (damager instanceof Projectile) {
                // TODO: find out why we never get damager instance of
                // Projectile
            }
            else if (damager instanceof TNTPrimed) {
                causeOfDeath = DeathEventType.BLOCK_EXPLOSION;
            }
            else {
                DeathTpPlus.logger.info("[DeathTpPlus] unknown enitity damager" + damager);
            }
        }
        else if (damageEvent != null) {
            causeOfDeath = DeathEventType.valueOf(damageEvent.getCause().toString());
        }

        if (causeOfDeath == null) {
            causeOfDeath = DeathEventType.UNKNOWN;
            murderWeapon = "unknown";
        }

        String eventAnnounce = "";
        String loghowdied = "";

        if (DTPConfig.config.get(ConfigType.ALLOW_DEATHTP).equals("true")) {
            DeathTpPlus.lastLocationLog.setRecord(player);
        }

        if (DTPConfig.config.get(ConfigType.SHOW_DEATHNOTIFY).equals("true") || DTPConfig.config.get(ConfigType.SHOW_STREAKS).equals("true") || DTPConfig.config.get(ConfigType.ALLOW_DEATHLOG).equals("true")) {

            loghowdied = causeOfDeath.toString();
            // TODO: change into case statement and create methods for
            // eventAnnounce
            eventAnnounce = getDeathMessage(causeOfDeath).replace("%n", player.getDisplayName());

            if (causeOfDeath.equals("PVP") || causeOfDeath.equals("FISTS") || causeOfDeath.equals("TAMED")) {
                loghowdied = killerName;
                eventAnnounce = eventAnnounce.replace("%i", murderWeapon).replace("%a", killerName);

                if (DTPConfig.config.get(ConfigType.SHOW_STREAKS).matches("true")) {
                    DeathTpPlus.streakLog.setRecord(killerName, player.getDisplayName());
                }
                // write kill to death log
                if (DTPConfig.config.get(ConfigType.ALLOW_DEATHLOG).matches("true")) {
                    DeathTpPlus.deathLog.setRecord(killerName, "kill", player.getDisplayName());
                }
            }
            if (eventAnnounce.equals("")) {
                eventAnnounce = getDeathMessage(DeathEventType.UNKNOWN).replace("%n", player.getDisplayName());
            }

            eventAnnounce = DTPUtils.convertColorCode(eventAnnounce);

            if (DTPConfig.config.get(ConfigType.SHOW_DEATHNOTIFY).equals("true")) {
                // plugin.getServer().broadcastMessage(eventAnnounce);
                if (event instanceof PlayerDeathEvent) {
                    ((PlayerDeathEvent) event).setDeathMessage(eventAnnounce);
                }

            }

            // CraftIRC
            if (DeathTpPlus.craftIRCHandle != null) {
                DeathTpPlus.craftIRCHandle.sendMessageToTag(DTPUtils.removeColorCode(eventAnnounce), DTPConfig.config.get(ConfigType.DEATHTP_TAG));
            }

            if (DTPConfig.config.get(ConfigType.ALLOW_DEATHLOG).matches("true")) {
                DeathTpPlus.deathLog.setRecord(player.getDisplayName(), "death", loghowdied);
            }

            if (DTPConfig.config.get(ConfigType.SHOW_SIGN).equals("true")) {
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
                    signtext = causeOfDeath.toString().substring(0, 1) + causeOfDeath.toString().substring(1).toLowerCase();
                    if (causeOfDeath.equals("PVP") || causeOfDeath.equals("FISTS") || causeOfDeath.equals("TAMED"))
                        signtext = killerName;

                    sign.setLine(3, signtext);
                }
            }

        }

        // added compatibility for streaks if notify is off
        else {
            if (causeOfDeath.equals("PVP") || causeOfDeath.equals("FISTS") || causeOfDeath.equals("TAMED")) {
                if (DTPConfig.config.get(ConfigType.SHOW_STREAKS).matches("true"))
                    DeathTpPlus.streakLog.setRecord(killerName, player.getDisplayName());
            }

            if (DTPConfig.config.get(ConfigType.ALLOW_DEATHLOG).matches("true")) {
                DeathTpPlus.deathLog.setRecord(player.getDisplayName(), "death", loghowdied);
            }
        }

    }

    public String getDeathMessage(DeathEventType deathEventType)
    {
        int messageindex = 0;

        if (!DTPConfig.deathMessages.containsKey(deathEventType))
            return null;

        if (DTPConfig.deathMessages.get(deathEventType).size() > 1) {
            Random rand = new Random();
            messageindex = rand.nextInt(DTPConfig.deathMessages.get(deathEventType).size());
        }
        return DTPConfig.deathMessages.get(deathEventType).get(messageindex);
    }
}
