package org.simiancage.DeathTpPlus.utils;

import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Giant;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Squid;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;

public class DTPUtils
{
    public static String convertColorCode(String msg)
    {
        msg = msg.replace("&0", "§0");
        msg = msg.replace("&1", "§1");
        msg = msg.replace("&2", "§2");
        msg = msg.replace("&3", "§3");
        msg = msg.replace("&4", "§4");
        msg = msg.replace("&5", "§5");
        msg = msg.replace("&6", "§6");
        msg = msg.replace("&7", "§7");
        msg = msg.replace("&8", "§8");
        msg = msg.replace("&9", "§9");
        msg = msg.replace("&a", "§a");
        msg = msg.replace("&b", "§b");
        msg = msg.replace("&c", "§c");
        msg = msg.replace("&d", "§d");
        msg = msg.replace("&e", "§e");
        msg = msg.replace("&f", "§f");

        return msg;
    }

    public static String removeColorCode(String msg)
    {
        msg = msg.replace("§0", "");
        msg = msg.replace("§2", "");
        msg = msg.replace("§3", "");
        msg = msg.replace("§4", "");
        msg = msg.replace("§5", "");
        msg = msg.replace("§6", "");
        msg = msg.replace("§7", "");
        msg = msg.replace("§8", "");
        msg = msg.replace("§9", "");
        msg = msg.replace("§a", "");
        msg = msg.replace("§b", "");
        msg = msg.replace("§c", "");
        msg = msg.replace("§d", "");
        msg = msg.replace("§e", "");
        msg = msg.replace("§f", "");

        return msg;
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
}
