package org.simiancage.DeathTpPlus.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.models.DeathDetail;
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
        DeathDetail deathDetail;

        if (event.getEntity() instanceof Player) {
            deathDetail = new DeathDetail(event);
        }
        else {
            return;
        }

        if (DTPConfig.configFlags.get(ConfigFlagType.ALLOW_DEATHTP)) {
            DeathTpPlus.deathLocationLog.setRecord(deathDetail);
        }

        if (DTPConfig.configFlags.get(ConfigFlagType.SHOW_STREAKS)) {
            DeathTpPlus.streakLog.setRecord(deathDetail);
        }

        if (DTPConfig.configFlags.get(ConfigFlagType.ALLOW_DEATHLOG)) {
            DeathTpPlus.deathLog.setRecord(deathDetail);
        }

        if (DTPConfig.configFlags.get(ConfigFlagType.SHOW_DEATHNOTIFY)) {

            String deathMessage = DTPConfig.getDeathMessage(deathDetail);
            
            if (event instanceof PlayerDeathEvent) {
                ((PlayerDeathEvent) event).setDeathMessage(deathMessage);
            }

            // CraftIRC
            if (DeathTpPlus.craftIRCHandle != null) {
                DeathTpPlus.craftIRCHandle.sendMessageToTag(DTPUtils.removeColorCodes(deathMessage), DTPConfig.configValues.get(ConfigValueType.DEATHTP_TAG));
            }
        }

        if (DTPConfig.configFlags.get(ConfigFlagType.SHOW_SIGN)) {
            // place sign
            Block signBlock = deathDetail.getPlayer().getWorld().getBlockAt(deathDetail.getPlayer().getLocation().getBlockX(), deathDetail.getPlayer().getLocation().getBlockY(), deathDetail.getPlayer().getLocation().getBlockZ());

            signBlock.setType(Material.SIGN_POST);

            BlockState state = signBlock.getState();

            if (state instanceof Sign) {
                String signtext;
                Sign sign = (Sign) state;
                sign.setLine(0, "[RIP]");
                sign.setLine(1, deathDetail.getPlayer().getDisplayName());
                sign.setLine(2, "Died by");
                signtext = deathDetail.getCauseOfDeath().toString().substring(0, 1) + deathDetail.getCauseOfDeath().toString().substring(1).toLowerCase();
                if (deathDetail.isPVPDeath())
                    signtext = deathDetail.getKiller().getName();

                sign.setLine(3, signtext);
            }
        }
    }
}
