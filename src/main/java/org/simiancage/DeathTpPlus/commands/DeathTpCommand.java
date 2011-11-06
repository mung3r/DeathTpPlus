package org.simiancage.DeathTpPlus.commands;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.simiancage.DeathTpPlus.DeathTpPlus;
import org.simiancage.DeathTpPlus.utils.DTPConfig;
import org.simiancage.DeathTpPlus.utils.DTPConfig.ConfigType;

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
            if (DeathTpPlus.permission.has(player, "deathtpplus.worldtravel") && DTPConfig.config.get(ConfigType.ALLOW_WORLDTRAVEL).equalsIgnoreCase("permissions")) {
                worldTravel = true;
            }
            double registerCost = Double.valueOf(DTPConfig.config.get(ConfigType.DEATHTP_COST).trim()).doubleValue();

            if (DeathTpPlus.permission.has(player, "deathtpplus.deathtp")) {
                canUseCommand = true;
            }
            else {
                canUseCommand = DTPConfig.config.get(ConfigType.ALLOW_DEATHTP).equals("true");
            }

            if (canUseCommand) {
                // costs item in inventory
                if (!DTPConfig.config.get(ConfigType.CHARGE_ITEM).equals("0")) {
                    if (player.getItemInHand().getType().getId() != Integer.parseInt(DTPConfig.config.get(ConfigType.CHARGE_ITEM))) {
                        player.sendMessage("You must be holding a " + Material.getMaterial(Integer.parseInt(DTPConfig.config.get(ConfigType.CHARGE_ITEM))).toString() + " to teleport.");
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
                    if (DeathTpPlus.useRegister) {
                        if (DeathTpPlus.economy != null && DeathTpPlus.economy.getBalance(player.getName()) > registerCost) {
                            DeathTpPlus.economy.withdrawPlayer(player.getName(), registerCost);
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
                    String teleloc = DeathTpPlus.lastLocationLog.getRecord(player.getName());

                    if (teleloc != "") {
                        location = teleloc.split(":");
                        Location sendLocation = player.getLocation();
                        double x, y, z;

                        x = Double.valueOf(location[1].trim()).doubleValue();
                        y = Double.valueOf(location[2].trim()).doubleValue();
                        z = Double.valueOf(location[3].trim()).doubleValue();
                        World deathWorld = player.getServer().getWorld(location[4].trim());
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
                    if (DeathTpPlus.useRegister && !teleported && DeathTpPlus.economy != null) {
                        if (DeathTpPlus.economy != null)
                            DeathTpPlus.economy.depositPlayer(player.getName(), registerCost);
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
            DeathTpPlus.logger.info("This is only a player command.");
            return true;
        }
    }
}
