package me.sat7.dynamicshop.commands;

import org.bukkit.entity.Player;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.utilities.ShopUtil;

import static me.sat7.dynamicshop.utilities.LangUtil.t;

public final class SetDefaultShop
{
    private SetDefaultShop()
    {

    }

    static boolean setDefaultShop(String[] args, Player player)
    {
        if (args.length >= 2)
        {
            if (!player.hasPermission("dshop.admin.setdefaultshop"))
            {
                player.sendMessage(DynamicShop.dsPrefix + t("ERR.NO_PERMISSION"));
                return true;
            }

            try
            {
                if (ShopUtil.shopConfigFiles.containsKey(args[1]))
                {
                    DynamicShop.plugin.getConfig().set("Command.DefaultShopName", args[1]);
                    DynamicShop.plugin.saveConfig();
                    player.sendMessage(DynamicShop.dsPrefix + t("MESSAGE.CHANGES_APPLIED") + args[1]);
                } else
                {
                    player.sendMessage(DynamicShop.dsPrefix + t("ERR.SHOP_NOT_FOUND"));
                }
            } catch (Exception e)
            {
                player.sendMessage(DynamicShop.dsPrefix + t("ERR.SHOP_NOT_FOUND"));
            }
        } else
        {
            player.sendMessage(DynamicShop.dsPrefix + t("ERR.WRONG_USAGE"));
        }
        return false;
    }
}
