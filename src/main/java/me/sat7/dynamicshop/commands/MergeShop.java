package me.sat7.dynamicshop.commands;

import org.bukkit.entity.Player;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.utilities.ShopUtil;

import static me.sat7.dynamicshop.utilities.LangUtil.t;

public final class MergeShop
{
    private MergeShop()
    {

    }

    static boolean mergeShop(String[] args, Player player)
    {
        if (args.length >= 3)
        {
            if (!player.hasPermission("dshop.admin.mergeshop"))
            {
                player.sendMessage(DynamicShop.dsPrefix + t("ERR.NO_PERMISSION"));
                return true;
            }

            if (args[1].equals(args[2]))
            {
                player.sendMessage(DynamicShop.dsPrefix + t("ERR.WRONG_USAGE"));
                return true;
            }

            try
            {
                if (ShopUtil.shopConfigFiles.containsKey(args[1]) &&
                    ShopUtil.shopConfigFiles.containsKey(args[2]))
                {
                    ShopUtil.mergeShop(args[1], args[2]);
                    player.sendMessage(DynamicShop.dsPrefix + t("MESSAGE.CHANGES_APPLIED") + args[1]);
                }
                else
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
