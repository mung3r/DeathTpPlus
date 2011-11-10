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
import org.simiancage.DeathTpPlus.models.DeathRecord.DeathRecordType;
import org.simiancage.DeathTpPlus.utils.DTPConfig;
import org.simiancage.DeathTpPlus.utils.DTPConfig.ConfigFlagType;
import org.simiancage.DeathTpPlus.utils.DTPConfig.ConfigValueType;
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

        if (DTPConfig.configFlags.get(ConfigFlagType.ALLOW_DEATHTP)) {
            DeathTpPlus.deathLocationLog.setRecord(player);
        }

        if (DTPConfig.configFlags.get(ConfigFlagType.SHOW_DEATHNOTIFY) || DTPConfig.configFlags.get(ConfigFlagType.SHOW_STREAKS) || DTPConfig.configFlags.get(ConfigFlagType.ALLOW_DEATHLOG)) {

            loghowdied = causeOfDeath.toString();
            // TODO: change into case statement and create methods for
            // eventAnnounce
            eventAnnounce = getDeathMessage(causeOfDeath).replace("%n", player.getDisplayName());

            if (causeOfDeath.equals(DeathEventType.PVP) || causeOfDeath.equals(DeathEventType.PVP_FISTS) || causeOfDeath.equals(DeathEventType.PVP_TAMED)) {
                loghowdied = killerName;
                eventAnnounce = eventAnnounce.replace("%i", murderWeapon).replace("%a", killerName);

                if (DTPConfig.configFlags.get(ConfigFlagType.SHOW_STREAKS)) {
                    DeathTpPlus.streakLog.setRecord(killerName, player.getDisplayName());
                }
                // write kill to death log
                if (DTPConfig.configFlags.get(ConfigFlagType.ALLOW_DEATHLOG)) {
                    DeathTpPlus.deathLog.setRecord(killerName, DeathRecordType.kill, player.getDisplayName());
                }
            }
            if (eventAnnounce.isEmpty()) {
                eventAnnounce = getDeathMessage(DeathEventType.UNKNOWN).replace("%n", player.getDisplayName());
            }

            eventAnnounce = DTPUtils.convertColorCodes(eventAnnounce);

            if (DTPConfig.configFlags.get(ConfigFlagType.SHOW_DEATHNOTIFY)) {
                // plugin.getServer().broadcastMessage(eventAnnounce);
                if (event instanceof PlayerDeathEvent) {
                    ((PlayerDeathEvent) event).setDeathMessage(eventAnnounce);
                }

            }

            // CraftIRC
            if (DeathTpPlus.craftIRCHandle != null) {
                DeathTpPlus.craftIRCHandle.sendMessageToTag(DTPUtils.removeColorCodes(eventAnnounce), DTPConfig.configValues.get(ConfigValueType.DEATHTP_TAG));
            }

            if (DTPConfig.configFlags.get(ConfigFlagType.ALLOW_DEATHLOG)) {
                DeathTpPlus.deathLog.setRecord(player.getDisplayName(), DeathRecordType.death, loghowdied);
            }

            if (DTPConfig.configFlags.get(ConfigFlagType.SHOW_SIGN)) {
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
                    if (causeOfDeath.equals(DeathEventType.PVP) || causeOfDeath.equals(DeathEventType.PVP_FISTS) || causeOfDeath.equals(DeathEventType.PVP_TAMED))
                        signtext = killerName;

                    sign.setLine(3, signtext);
                }
            }

        }

        // added compatibility for streaks if notify is off
        else {
            if (causeOfDeath.equals(DeathEventType.PVP) || causeOfDeath.equals(DeathEventType.PVP_FISTS) || causeOfDeath.equals(DeathEventType.PVP_TAMED)) {
                if (DTPConfig.configFlags.get(ConfigFlagType.SHOW_STREAKS))
                    DeathTpPlus.streakLog.setRecord(killerName, player.getDisplayName());
            }

            if (DTPConfig.configFlags.get(ConfigFlagType.ALLOW_DEATHLOG)) {
                DeathTpPlus.deathLog.setRecord(player.getDisplayName(), DeathRecordType.death, loghowdied);
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
