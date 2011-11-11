package org.simiancage.DeathTpPlus.models;

import org.bukkit.Material;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Tameable;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.utils.DTPUtils;
import org.simiancage.DeathTpPlus.utils.DTPConfig.DeathEventType;

public class DeathDetail
{
    private Player player;
    private DeathEventType causeOfDeath;
    private Player killer;
    private String murderWeapon;

    public DeathDetail()
    {
    }

    public DeathDetail(EntityDeathEvent event)
    {
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
                killer = (Player) damager;
            }
            else if (damager instanceof Creature) {
                if (damager instanceof Tameable) {
                    if (((Tameable) damager).isTamed()) {
                        causeOfDeath = DeathEventType.PVP_TAMED;
                        murderWeapon = DTPUtils.getCreatureType(damager).toString().toLowerCase();
                        killer = (Player) ((Tameable) damager).getOwner();
                    }
                }
                else {
                    causeOfDeath = DeathEventType.valueOf(DTPUtils.getCreatureType(damager).toString());
                }
            }
            else if (damager instanceof Projectile) {
                // TODO: find out why we never get damager instance of
                // Projectile
                if (((Projectile) damager).getShooter() instanceof Player) {
                    causeOfDeath = DeathEventType.PVP;
                    murderWeapon = ((Projectile) damager).getClass().getName();
                    killer = (Player) ((Projectile) damager).getShooter();
                }
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
    }

    public Player getPlayer()
    {
        return player;
    }

    public void setPlayer(Player player)
    {
        this.player = player;
    }

    public DeathEventType getCauseOfDeath()
    {
        return causeOfDeath;
    }

    public void setCauseOfDeath(DeathEventType causeOfDeath)
    {
        this.causeOfDeath = causeOfDeath;
    }

    public Player getKiller()
    {
        return killer;
    }

    public void setKiller(Player killer)
    {
        this.killer = killer;
    }

    public String getMurderWeapon()
    {
        return murderWeapon;
    }

    public void setMurderWeapon(String murderWeapon)
    {
        this.murderWeapon = murderWeapon;
    }

    public Boolean isPVPDeath()
    {
        return causeOfDeath.equals(DeathEventType.PVP) || causeOfDeath.equals(DeathEventType.PVP_FISTS) || causeOfDeath.equals(DeathEventType.PVP_TAMED);
    }
}
