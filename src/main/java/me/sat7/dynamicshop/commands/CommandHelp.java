package me.sat7.dynamicshop.commands;

import org.bukkit.entity.Player;

import me.sat7.dynamicshop.DynamicShop;

import java.util.UUID;

import static me.sat7.dynamicshop.utilities.LangUtil.t;

public final class CommandHelp
{
    private CommandHelp()
    {

    }

    static boolean commandHelp(String[] args, Player player)
    {
        if (args.length < 2)
        {
            player.sendMessage(DynamicShop.dsPrefix + t("ERR.WRONG_USAGE"));
            return true;
        }

        UUID uuid = player.getUniqueId();

        if (args[1].equalsIgnoreCase("on"))
        {
            player.sendMessage(DynamicShop.dsPrefix + "켜짐");
            DynamicShop.userTempData.put(uuid, "");
            DynamicShop.ccUser.get().set(player.getUniqueId() + ".cmdHelp", true);
            DynamicShop.ccUser.save();
        } else if (args[1].equalsIgnoreCase("off"))
        {
            player.sendMessage(DynamicShop.dsPrefix + "꺼짐");
            DynamicShop.userTempData.put(uuid, "");
            DynamicShop.ccUser.get().set(player.getUniqueId() + ".cmdHelp", false);
            DynamicShop.ccUser.save();
        } else
        {
            player.sendMessage(DynamicShop.dsPrefix + t("ERR.WRONG_USAGE"));
        }
        return false;
    }
}
