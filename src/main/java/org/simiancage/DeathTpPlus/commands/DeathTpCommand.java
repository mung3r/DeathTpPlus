package org.simiancage.DeathTpPlus.commands;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.models.DeathLocationRecord;
import org.simiancage.DeathTpPlus.utils.DTPConfig;
import org.simiancage.DeathTpPlus.utils.DTPConfig.ConfigFlagType;
import org.simiancage.DeathTpPlus.utils.DTPConfig.ConfigValueType;

public class DeathTpCommand implements Command
{

    public Boolean execute(CommandSender sender, String[] args)
    {
        boolean worldTravel = false;
        boolean canUseCommand = false;
        boolean teleportok = true;
        boolean teleported = false;

        if (sender instanceof Player) {
            Player player = (Player) sender;
            String thisWorld = player.getWorld().getName().toString();
            if (DTPConfig.configValues.get(ConfigValueType.ALLOW_WORLDTRAVEL).equalsIgnoreCase("permissions")) {
                if (DeathTpPlus.permission != null) {
                    worldTravel = DeathTpPlus.permission.has(player, "deathtpplus.worldtravel");
                }
                else {
                    worldTravel = false;
                }
            }
            else if (DTPConfig.configValues.get(ConfigValueType.ALLOW_WORLDTRAVEL).equalsIgnoreCase("yes")) {
                worldTravel = true;
            }
            double deathTpCost = Double.valueOf(DTPConfig.configValues.get(ConfigValueType.DEATHTP_COST).trim()).doubleValue();

            if (DeathTpPlus.permission != null) {
                canUseCommand = DeathTpPlus.permission.has(player, "deathtpplus.deathtp");
            }
            else {
                canUseCommand = DTPConfig.configFlags.get(ConfigFlagType.ALLOW_DEATHTP);
            }

            if (canUseCommand) {
                // costs item in inventory
                if (!DTPConfig.configValues.get(ConfigValueType.CHARGE_ITEM).equals("0")) {
                    if (player.getItemInHand().getType().getId() != Integer.parseInt(DTPConfig.configValues.get(ConfigValueType.CHARGE_ITEM))) {
                        player.sendMessage("You must be holding a " + Material.getMaterial(Integer.parseInt(DTPConfig.configValues.get(ConfigValueType.CHARGE_ITEM))).toString() + " to teleport.");
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

                // costs iconomy
                if (deathTpCost > 0 && DeathTpPlus.economy != null) {
                    if (DeathTpPlus.economy.getBalance(player.getName()) > deathTpCost) {
                        DeathTpPlus.economy.withdrawPlayer(player.getName(), deathTpCost);
                        player.sendMessage("You used " + deathTpCost + " to use /deathtp");
                    }
                    else {
                        player.sendMessage("You need " + deathTpCost + " coins to use /deathtp");
                        teleportok = false;
                    }
                }

                if (teleportok) {

                    DeathLocationRecord teleloc = DeathTpPlus.deathLocationLog.getRecord(player.getName());

                    if (teleloc != null) {
                        Location sendLocation = player.getLocation();
                        double x, y, z;

                        x = teleloc.getLocation().getX();
                        y = teleloc.getLocation().getY();
                        z = teleloc.getLocation().getZ();
                        World deathWorld = player.getServer().getWorld(teleloc.getWorldName());
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
                    if (!teleported && DeathTpPlus.economy != null) {
                        DeathTpPlus.economy.depositPlayer(player.getName(), deathTpCost);
                        player.sendMessage("Giving you back " + deathTpCost);
                    }
                }
                else {
                    player.sendMessage("That command is not available");
                }
            }

            return true;
        }
        else {
            DeathTpPlus.logger.info("This is only a player command.");
            return true;
        }
    }
}
