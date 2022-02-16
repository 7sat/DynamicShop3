package me.sat7.dynamicshop.commands;

import org.bukkit.entity.Player;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.utilities.ShopUtil;

import static me.sat7.dynamicshop.utilities.LangUtil.t;

public final class RenameShop
{
    private RenameShop()
    {

    }

    static boolean renameShop(String[] args, Player player)
    {
        if (args.length >= 3)
        {
            if (!player.hasPermission("dshop.admin.renameshop"))
            {
                player.sendMessage(DynamicShop.dsPrefix + t("ERR.NO_PERMISSION"));
                return true;
            }

            try
            {
                if (ShopUtil.shopConfigFiles.containsKey(args[1]))
                {
                    String newName = args[2].replace("/", "");
                    ShopUtil.renameShop(args[1], newName);
                    player.sendMessage(DynamicShop.dsPrefix + t("MESSAGE.CHANGES_APPLIED") + newName);
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
